package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.*;
import com.example.iw20252026merca_esi.repository.DetallePedidoMenuRepository;
import com.example.iw20252026merca_esi.repository.DetallePedidoProductoRepository;
import com.example.iw20252026merca_esi.repository.PedidoRepository;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Route(value = "cocina", layout = MainLayout.class)
@PageTitle("Panel de Cocina - MercaESI")
@RolesAllowed({"ADMINISTRADOR", "PROPIETARIO", "COCINA"})
public class CocinaView extends VerticalLayout implements BeforeEnterObserver {
    
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoProductoRepository detallePedidoProductoRepository;
    private final DetallePedidoMenuRepository detallePedidoMenuRepository;
    private final SessionService sessionService;
    private Grid<Pedido> pedidosGrid;
    private MultiSelectComboBox<String> filtroEstados;
    private Div statsPanel;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final String COLOR = "color";
    private static final String COLOR1 = "white";
    private static final String COLOR2 = "#D32F2F";
    private static final String COLOR3 = "#FF9800";
    private static final String COLOR4 = "#4CAF50";
    private static final String BACKGROUNDCOLOR = "background-color";
    private static final String BADGE = "badge";
    private static final String PADDING = "padding";
    private static final String PADDING1 = "2px 8px";
    private  static final String BORDERRADIUS = "border-radius";
    private static final String FONTSIZE = "font-size";
    private static final String FONTWEIGHT = "font-weight";
    private static final String MARGINTOP = "margin-top";

    @Autowired
    public CocinaView(PedidoRepository pedidoRepository, 
                      DetallePedidoProductoRepository detallePedidoProductoRepository,
                      DetallePedidoMenuRepository detallePedidoMenuRepository,
                      SessionService sessionService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoProductoRepository = detallePedidoProductoRepository;
        this.detallePedidoMenuRepository = detallePedidoMenuRepository;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("🍳 Panel de Cocina");
        titulo.getStyle().set(COLOR, COLOR2);

        Button btnActualizar = new Button("Actualizar", new Icon(VaadinIcon.REFRESH));
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.getStyle().set(BACKGROUNDCOLOR, COLOR2);
        btnActualizar.addClickListener(e -> cargarPedidos());

        Button btnExpandirTodos = new Button("Expandir Todos", new Icon(VaadinIcon.EXPAND_FULL));
        btnExpandirTodos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnExpandirTodos.addClickListener(e -> {
            if ("Expandir Todos".equals(btnExpandirTodos.getText())) {
                expandirTodos();
                btnExpandirTodos.setText("Colapsar Todos");
                btnExpandirTodos.setIcon(new Icon(VaadinIcon.COMPRESS));
            } else {
                colapsarTodos();
                btnExpandirTodos.setText("Expandir Todos");
                btnExpandirTodos.setIcon(new Icon(VaadinIcon.EXPAND_FULL));
            }
        });

        HorizontalLayout headerLayout = new HorizontalLayout(titulo, btnExpandirTodos, btnActualizar);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setSpacing(true);

        // Panel de estadísticas
        statsPanel = createStatsPanel();

        HorizontalLayout filtrosLayout = createFiltros();
        configurarGrid();

        add(headerLayout, statsPanel, filtrosLayout, pedidosGrid);
        cargarPedidos();
    }

    private void expandirTodos() {
        pedidosGrid.getDataProvider().fetch(new com.vaadin.flow.data.provider.Query<>())
            .forEach(pedido -> pedidosGrid.setDetailsVisible(pedido, true));
    }

    private void colapsarTodos() {
        pedidosGrid.getDataProvider().fetch(new com.vaadin.flow.data.provider.Query<>())
            .forEach(pedido -> pedidosGrid.setDetailsVisible(pedido, false));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            event.rerouteTo("");
            Notification.show("Acceso denegado.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private HorizontalLayout createFiltros() {
        HorizontalLayout filtrosLayout = new HorizontalLayout();
        filtrosLayout.setAlignItems(Alignment.END);
        filtrosLayout.setSpacing(true);

        filtroEstados = new MultiSelectComboBox<>("Filtrar por Estados");
        filtroEstados.setItems("PENDIENTE_PAGO", "EN_COCINA", "LISTO", "EN_REPARTO", "FINALIZADO");
        filtroEstados.select("EN_COCINA");
        filtroEstados.setWidth("400px");
        filtroEstados.addValueChangeListener(e -> cargarPedidos());

        filtrosLayout.add(filtroEstados);
        return filtrosLayout;
    }

    private void configurarGrid() {
        pedidosGrid = new Grid<>(Pedido.class, false);
        pedidosGrid.setHeight("calc(100vh - 220px)");
        pedidosGrid.setWidthFull();

        // Columna de tiempo transcurrido
        pedidosGrid.addColumn(pedido -> {
            if (pedido.getFecha() != null) {
                Duration duracion = Duration.between(pedido.getFecha(), LocalDateTime.now());
                long minutos = duracion.toMinutes();
                if (minutos < 60) {
                    return minutos + " min";
                } else {
                    long horas = duracion.toHours();
                    long mins = minutos % 60;
                    return horas + "h " + mins + "m";
                }
            }
            return "";
        })
        .setHeader("Tiempo")
        .setSortable(false)
        .setFlexGrow(0)
        .setWidth("100px");

        // Columna de hora
        pedidosGrid.addColumn(pedido -> {
            if (pedido.getFecha() != null) {
                return pedido.getFecha().format(timeFormatter);
            }
            return "";
        })
        .setHeader("Hora")
        .setSortable(true)
        .setFlexGrow(0)
        .setWidth("80px");

        // Columna de cliente
        pedidosGrid.addColumn(pedido -> {
            if (pedido.getCliente() != null) {
                return pedido.getCliente().getNombre();
            }
            return "Sin cliente";
        })
        .setHeader("Cliente")
        .setSortable(true)
        .setFlexGrow(1);

        // Columna de tipo de pedido
        pedidosGrid.addComponentColumn(pedido -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            
            if (Boolean.TRUE.equals(pedido.getADomicilio())) {
                Span badge = new Span("🏠 Domicilio");
                badge.getElement().getThemeList().add(BADGE);
                badge.getStyle()
                    .set(BACKGROUNDCOLOR, "#FF5722")
                    .set(COLOR, COLOR1)
                    .set(PADDING, PADDING1)
                    .set(BORDERRADIUS, "8px")
                    .set(FONTSIZE, "11px");
                layout.add(badge);
            }
            
            if (Boolean.TRUE.equals(pedido.getParaLlevar())) {
                Span badge = new Span("📦 Para llevar");
                badge.getElement().getThemeList().add(BADGE);
                badge.getStyle()
                    .set(BACKGROUNDCOLOR, COLOR3)
                    .set(COLOR, COLOR1)
                    .set(PADDING, PADDING1)
                    .set(BORDERRADIUS, "8px")
                    .set(FONTSIZE, "11px");
                layout.add(badge);
            }
            
            if (!Boolean.TRUE.equals(pedido.getADomicilio()) && !Boolean.TRUE.equals(pedido.getParaLlevar())) {
                Span badge = new Span("🍽️ En local");
                badge.getElement().getThemeList().add(BADGE);
                badge.getStyle()
                    .set(BACKGROUNDCOLOR, COLOR4)
                    .set(COLOR, COLOR1)
                    .set(PADDING, PADDING1)
                    .set(BORDERRADIUS, "8px")
                    .set(FONTSIZE, "11px");
                layout.add(badge);
            }
            
            return layout;
        })
        .setHeader("Tipo")
        .setSortable(false)
        .setFlexGrow(1);

        // Columna de estado
        pedidosGrid.addComponentColumn(pedido -> {
            Span badge = new Span(pedido.getEstadoDescripcion());
            badge.getElement().getThemeList().add(BADGE);
            
            String colorFondo = switch(pedido.getEstado()) {
                case "PENDIENTE_PAGO" -> COLOR3;
                case "EN_COCINA" -> "#2196F3";
                case "EN_REPARTO" -> "#9C27B0";
                case "LISTO" -> COLOR4;
                case "FINALIZADO" -> "#607D8B";
                default -> "#757575";
            };
            
            badge.getStyle()
                    .set(BACKGROUNDCOLOR, colorFondo)
                    .set(COLOR, COLOR1)
                    .set(PADDING, "4px 12px")
                    .set(BORDERRADIUS, "12px")
                    .set(FONTSIZE, "12px")
                    .set(FONTWEIGHT, "500");
            
            return badge;
        })
        .setHeader("Estado")
        .setSortable(true)
        .setFlexGrow(0)
        .setWidth("150px");

        // Columna de acciones con botón para marcar como listo
        pedidosGrid.addComponentColumn(pedido -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            
            if ("EN_COCINA".equals(pedido.getEstado())) {
                Button btnListo = new Button("Marcar Listo", new Icon(VaadinIcon.CHECK_CIRCLE));
                btnListo.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                btnListo.addClickListener(e -> marcarPedidoListo(pedido));
                actions.add(btnListo);
            } else if ("LISTO".equals(pedido.getEstado())) {
                Span texto = new Span("✓ Listo");
                texto.getStyle().set(COLOR, COLOR4).set(FONTWEIGHT, "bold");
                actions.add(texto);
            }
            
            return actions;
        })
        .setHeader("Acciones")
        .setFlexGrow(1);

        // Detalles del pedido - Lista ultra simple
        pedidosGrid.setItemDetailsRenderer(new ComponentRenderer<>(pedido -> {
            VerticalLayout detallesLayout = new VerticalLayout();
            detallesLayout.setSpacing(false);
            detallesLayout.setPadding(false);
            detallesLayout.getStyle()
                .set(BACKGROUNDCOLOR, "#fff")
                .set(PADDING, "15px");

            // Cargar productos individuales
            List<DetallePedidoProducto> productosIndividuales = 
                detallePedidoProductoRepository.findByPedidoIdPedido(pedido.getIdPedido());
            
            // Cargar menús
            List<DetallePedidoMenu> menus = 
                detallePedidoMenuRepository.findByPedidoIdPedido(pedido.getIdPedido());

            // Mostrar productos individuales
            for (DetallePedidoProducto detalle : productosIndividuales) {
                Div itemDiv = new Div();
                itemDiv.getStyle()
                    .set(PADDING, "5px 0")
                    .set(FONTSIZE, "18px")
                    .set(FONTWEIGHT, "600")
                    .set(COLOR, "#333");
                
                itemDiv.setText("- " + detalle.getCantidad() + "x " + detalle.getProducto().getNombre());
                detallesLayout.add(itemDiv);
            }

            // Mostrar menús y sus productos
            for (DetallePedidoMenu detalleMenu : menus) {
                // Nombre del menú
                Div menuDiv = new Div();
                menuDiv.getStyle()
                    .set(PADDING, "5px 0")
                    .set(FONTSIZE, "18px")
                    .set(FONTWEIGHT, "600")
                    .set(COLOR, "#333");
                
                menuDiv.setText("- " + detalleMenu.getCantidad() + "x " + detalleMenu.getMenu().getNombre());
                detallesLayout.add(menuDiv);
                
                // Productos del menú (indentados)
                if (detalleMenu.getMenu().getProductos() != null && 
                    !detalleMenu.getMenu().getProductos().isEmpty()) {
                    for (Producto producto : detalleMenu.getMenu().getProductos()) {
                        Div productoMenuDiv = new Div();
                        productoMenuDiv.getStyle()
                            .set(PADDING, "3px 0")
                            .set("padding-left", "30px")
                            .set(FONTSIZE, "16px")
                            .set(COLOR, "#666");
                        
                        productoMenuDiv.setText("→ " + producto.getNombre());
                        detallesLayout.add(productoMenuDiv);
                    }
                }
            }

            // Si no hay productos ni menús
            if (productosIndividuales.isEmpty() && menus.isEmpty()) {
                Div sinProductos = new Div();
                sinProductos.setText("No hay productos en este pedido");
                sinProductos.getStyle()
                    .set(COLOR, "#999")
                    .set(PADDING, "20px")
                    .set("text-align", "center");
                detallesLayout.add(sinProductos);
            }

            return detallesLayout;
        }));
    }

    private void cargarPedidos() {
        Set<String> estadosSeleccionados = filtroEstados.getSelectedItems();
        List<Pedido> pedidos;
        
        if (estadosSeleccionados.isEmpty()) {
            // Por defecto mostrar solo pedidos en cocina
            pedidos = pedidoRepository.findAllWithDetails().stream()
                    .filter(p -> "EN_COCINA".equals(p.getEstado()))
                    .collect(Collectors.toList());
        } else {
            pedidos = pedidoRepository.findAllWithDetails().stream()
                    .filter(p -> estadosSeleccionados.contains(p.getEstado()))
                    .collect(Collectors.toList());
        }
        
        pedidosGrid.setItems(pedidos);
        actualizarEstadisticas(statsPanel);
        
        Notification.show(String.format("🍳 %d pedidos", pedidos.size()),
                3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void marcarPedidoListo(Pedido pedido) {
        pedido.setEstadoPedido(EstadoPedido.LISTO);
        pedidoRepository.save(pedido);
        
        Notification.show(String.format("✓ Pedido #%d marcado como LISTO", pedido.getIdPedido()),
                4000, Notification.Position.MIDDLE)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        
        cargarPedidos();
    }

    private Div createStatsPanel() {
        Div panel = new Div();
        panel.getStyle()
                .set(BACKGROUNDCOLOR, COLOR1)
                .set(BORDERRADIUS, "8px")
                .set(PADDING, "20px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("margin-bottom", "20px");

        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.AROUND);
        
        panel.add(statsLayout);
        // No cargar estadísticas aquí, se cargarán después en cargarPedidos()
        
        return panel;
    }

    private void actualizarEstadisticas(Div panel) {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.AROUND);
        statsLayout.setAlignItems(Alignment.CENTER);

        // Obtener todos los pedidos EN_COCINA
        List<Pedido> pedidosEnCocina = pedidoRepository.findAllWithDetails().stream()
                .filter(p -> "EN_COCINA".equals(p.getEstado()))
                .collect(Collectors.toList());
        
        // Contar productos de todas las fuentes
        Map<String, Integer> productosCantidad = new HashMap<>();
        
        for (Pedido pedido : pedidosEnCocina) {
            // 1. Contar productos individuales
            List<DetallePedidoProducto> productosIndividuales = 
                detallePedidoProductoRepository.findByPedidoIdPedido(pedido.getIdPedido());
            
            for (DetallePedidoProducto detalle : productosIndividuales) {
                String nombreProducto = detalle.getProducto().getNombre();
                productosCantidad.merge(nombreProducto, detalle.getCantidad(), Integer::sum);
            }
            
            // 2. Contar productos dentro de menús
            List<DetallePedidoMenu> menus = 
                detallePedidoMenuRepository.findByPedidoIdPedido(pedido.getIdPedido());
            
            for (DetallePedidoMenu detalleMenu : menus) {
                if (detalleMenu.getMenu().getProductos() != null) {
                    for (Producto producto : detalleMenu.getMenu().getProductos()) {
                        String nombreProducto = producto.getNombre();
                        // La cantidad del menú multiplica la cantidad de cada producto
                        productosCantidad.merge(nombreProducto, detalleMenu.getCantidad(), Integer::sum);
                    }
                }
            }
        }
        
        // Obtener top 4 productos más pedidos
        List<Map.Entry<String, Integer>> topProductos = productosCantidad.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(4)
                .collect(Collectors.toList());
        
        // Título del panel más compacto
        HorizontalLayout tituloLayout = new HorizontalLayout();
        tituloLayout.setAlignItems(Alignment.CENTER);
        tituloLayout.setSpacing(true);
        
        Span icono = new Span("🍕");
        icono.getStyle().set(FONTSIZE, "20px");
        
        Span titulo = new Span("Productos a preparar");
        titulo.getStyle()
                .set(FONTSIZE, "13px")
                .set(COLOR, "#666")
                .set(FONTWEIGHT, "600");
        
        tituloLayout.add(icono, titulo);
        statsLayout.add(tituloLayout);
        
        // Mostrar productos o mensaje si no hay
        if (topProductos.isEmpty()) {
            Span sinPedidos = new Span("No hay pedidos en cocina");
            sinPedidos.getStyle()
                    .set(COLOR, "#999")
                    .set("font-style", "italic")
                    .set(PADDING, "20px");
            statsLayout.add(sinPedidos);
        } else {
            String[] colores = {COLOR2, "#FF5722", COLOR3, "#FFC107"};
            for (int i = 0; i < topProductos.size(); i++) {
                Map.Entry<String, Integer> entry = topProductos.get(i);
                statsLayout.add(createProductoCard(entry.getKey(), String.valueOf(entry.getValue()), colores[i]));
            }
        }

        panel.removeAll();
        panel.add(statsLayout);
    }

    private Component createProductoCard(String nombreProducto, String cantidad, String color) {
        HorizontalLayout card = new HorizontalLayout();
        card.setAlignItems(Alignment.CENTER);
        card.setPadding(false);
        card.setSpacing(true);
        card.getStyle()
                .set("border-left", "3px solid " + color)
                .set(BACKGROUNDCOLOR, "#f9f9f9")
                .set(BORDERRADIUS, "4px")
                .set("min-width", "110px")
                .set(PADDING, "6px 10px");

        Span cantidadSpan = new Span(cantidad);
        cantidadSpan.getStyle()
                .set(COLOR, color)
                .set(FONTSIZE, "24px")
                .set(FONTWEIGHT, "bold")
                .set("min-width", "30px")
                .set("text-align", "center");

        Span nombreSpan = new Span(nombreProducto);
        nombreSpan.getStyle()
                .set(COLOR, "#333")
                .set(FONTSIZE, "11px")
                .set(FONTWEIGHT, "500")
                .set("max-width", "100px")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        card.add(cantidadSpan, nombreSpan);
        return card;
    }
}
