package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.DetallePedido;
import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.EstadoPedido;
import com.example.iw20252026merca_esi.model.Pedido;
import com.example.iw20252026merca_esi.repository.PedidoRepository;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.details.Details;
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
    private final SessionService sessionService;
    private Grid<Pedido> pedidosGrid;
    private MultiSelectComboBox<String> filtroEstados;
    private Div statsPanel;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    public CocinaView(PedidoRepository pedidoRepository, SessionService sessionService) {
        this.pedidoRepository = pedidoRepository;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("🍳 Panel de Cocina");
        titulo.getStyle().set("color", "#D32F2F");

        Button btnActualizar = new Button("Actualizar", new Icon(VaadinIcon.REFRESH));
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.getStyle().set("background-color", "#D32F2F");
        btnActualizar.addClickListener(e -> cargarPedidos());

        HorizontalLayout headerLayout = new HorizontalLayout(titulo, btnActualizar);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);

        // Panel de estadísticas
        statsPanel = createStatsPanel();

        HorizontalLayout filtrosLayout = createFiltros();
        configurarGrid();

        add(headerLayout, statsPanel, filtrosLayout, pedidosGrid);
        cargarPedidos();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null || (!empleado.esCocina() && !empleado.esAdministrador() && !empleado.esPropietario())) {
            event.rerouteTo("");
            Notification.show("Acceso denegado. Solo personal de cocina.")
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
        pedidosGrid.setHeight("600px");
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
                badge.getElement().getThemeList().add("badge");
                badge.getStyle()
                    .set("background-color", "#FF5722")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "8px")
                    .set("font-size", "11px");
                layout.add(badge);
            }
            
            if (Boolean.TRUE.equals(pedido.getParaLlevar())) {
                Span badge = new Span("📦 Para llevar");
                badge.getElement().getThemeList().add("badge");
                badge.getStyle()
                    .set("background-color", "#FF9800")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "8px")
                    .set("font-size", "11px");
                layout.add(badge);
            }
            
            if (!Boolean.TRUE.equals(pedido.getADomicilio()) && !Boolean.TRUE.equals(pedido.getParaLlevar())) {
                Span badge = new Span("🍽️ En local");
                badge.getElement().getThemeList().add("badge");
                badge.getStyle()
                    .set("background-color", "#4CAF50")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "8px")
                    .set("font-size", "11px");
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
            badge.getElement().getThemeList().add("badge");
            
            String colorFondo = switch(pedido.getEstado()) {
                case "PENDIENTE_PAGO" -> "#FF9800";
                case "EN_COCINA" -> "#2196F3";
                case "EN_REPARTO" -> "#9C27B0";
                case "LISTO" -> "#4CAF50";
                case "FINALIZADO" -> "#607D8B";
                default -> "#757575";
            };
            
            badge.getStyle()
                    .set("background-color", colorFondo)
                    .set("color", "white")
                    .set("padding", "4px 12px")
                    .set("border-radius", "12px")
                    .set("font-size", "12px")
                    .set("font-weight", "500");
            
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
                texto.getStyle().set("color", "#4CAF50").set("font-weight", "bold");
                actions.add(texto);
            }
            
            return actions;
        })
        .setHeader("Acciones")
        .setFlexGrow(1);

        // Detalles del pedido (productos)
        pedidosGrid.setItemDetailsRenderer(new ComponentRenderer<>(pedido -> {
            VerticalLayout detallesLayout = new VerticalLayout();
            detallesLayout.setSpacing(true);
            detallesLayout.setPadding(true);
            detallesLayout.getStyle()
                .set("background-color", "#f5f5f5")
                .set("border-radius", "8px");

            H4 tituloProductos = new H4("📋 Productos del Pedido #" + pedido.getIdPedido());
            tituloProductos.getStyle().set("color", "#D32F2F").set("margin-top", "0");
            detallesLayout.add(tituloProductos);

            if (pedido.getDetallePedidos() != null && !pedido.getDetallePedidos().isEmpty()) {
                for (DetallePedido detalle : pedido.getDetallePedidos()) {
                    HorizontalLayout productoLayout = new HorizontalLayout();
                    productoLayout.setWidthFull();
                    productoLayout.setAlignItems(Alignment.CENTER);
                    productoLayout.getStyle()
                        .set("background-color", "white")
                        .set("padding", "10px")
                        .set("border-radius", "4px")
                        .set("margin-bottom", "5px");

                    // Cantidad
                    Span cantidad = new Span(String.valueOf(detalle.getCantidad()) + "x");
                    cantidad.getStyle()
                        .set("font-weight", "bold")
                        .set("font-size", "18px")
                        .set("color", "#D32F2F")
                        .set("min-width", "40px");

                    // Nombre del producto
                    VerticalLayout infoProducto = new VerticalLayout();
                    infoProducto.setSpacing(false);
                    infoProducto.setPadding(false);
                    
                    Span nombreProducto = new Span(detalle.getProducto().getNombre());
                    nombreProducto.getStyle()
                        .set("font-weight", "bold")
                        .set("font-size", "16px");
                    
                    infoProducto.add(nombreProducto);
                    
                    // Mostrar descripción si existe
                    if (detalle.getProducto().getDescripcion() != null && !detalle.getProducto().getDescripcion().isEmpty()) {
                        Span descripcion = new Span(detalle.getProducto().getDescripcion());
                        descripcion.getStyle()
                            .set("font-size", "12px")
                            .set("color", "#666");
                        infoProducto.add(descripcion);
                    }

                    // Precio
                    Span precio = new Span(String.format("%.2f €", detalle.calcularSubtotal()));
                    precio.getStyle()
                        .set("font-weight", "bold")
                        .set("color", "#4CAF50");

                    productoLayout.add(cantidad, infoProducto, precio);
                    productoLayout.setFlexGrow(1, infoProducto);
                    detallesLayout.add(productoLayout);
                }

                // Total del pedido
                HorizontalLayout totalLayout = new HorizontalLayout();
                totalLayout.setWidthFull();
                totalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
                totalLayout.getStyle()
                    .set("border-top", "2px solid #D32F2F")
                    .set("padding-top", "10px")
                    .set("margin-top", "10px");
                
                Span totalLabel = new Span("TOTAL: ");
                totalLabel.getStyle()
                    .set("font-weight", "bold")
                    .set("font-size", "18px");
                
                Span totalValor = new Span(String.format("%.2f €", pedido.getTotal()));
                totalValor.getStyle()
                    .set("font-weight", "bold")
                    .set("font-size", "20px")
                    .set("color", "#D32F2F");
                
                totalLayout.add(totalLabel, totalValor);
                detallesLayout.add(totalLayout);
            } else {
                Paragraph sinProductos = new Paragraph("No hay productos en este pedido");
                sinProductos.getStyle().set("color", "#999");
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
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("padding", "20px")
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

        // Obtener todos los pedidos EN_COCINA con detalles cargados
        List<Pedido> pedidosEnCocina = pedidoRepository.findAllWithDetails().stream()
                .filter(p -> "EN_COCINA".equals(p.getEstado()))
                .collect(Collectors.toList());
        
        // Contar productos
        Map<String, Integer> productosCantidad = new HashMap<>();
        for (Pedido pedido : pedidosEnCocina) {
            if (pedido.getDetallePedidos() != null) {
                for (DetallePedido detalle : pedido.getDetallePedidos()) {
                    String nombreProducto = detalle.getProducto().getNombre();
                    productosCantidad.merge(nombreProducto, detalle.getCantidad(), Integer::sum);
                }
            }
        }
        
        // Obtener top 4 productos más pedidos
        List<Map.Entry<String, Integer>> topProductos = productosCantidad.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(4)
                .collect(Collectors.toList());
        
        // Título del panel
        VerticalLayout tituloLayout = new VerticalLayout();
        tituloLayout.setAlignItems(Alignment.CENTER);
        tituloLayout.setSpacing(false);
        tituloLayout.setPadding(false);
        tituloLayout.getStyle().set("margin-right", "20px");
        
        Span icono = new Span("🍕");
        icono.getStyle().set("font-size", "32px");
        
        Span titulo = new Span("Productos");
        titulo.getStyle()
                .set("font-size", "14px")
                .set("color", "#666")
                .set("font-weight", "500");
        
        Span subtitulo = new Span("a preparar");
        subtitulo.getStyle()
                .set("font-size", "12px")
                .set("color", "#999");
        
        tituloLayout.add(icono, titulo, subtitulo);
        statsLayout.add(tituloLayout);
        
        // Mostrar productos o mensaje si no hay
        if (topProductos.isEmpty()) {
            Span sinPedidos = new Span("No hay pedidos en cocina");
            sinPedidos.getStyle()
                    .set("color", "#999")
                    .set("font-style", "italic")
                    .set("padding", "20px");
            statsLayout.add(sinPedidos);
        } else {
            String[] colores = {"#D32F2F", "#FF5722", "#FF9800", "#FFC107"};
            for (int i = 0; i < topProductos.size(); i++) {
                Map.Entry<String, Integer> entry = topProductos.get(i);
                statsLayout.add(createProductoCard(entry.getKey(), String.valueOf(entry.getValue()), colores[i]));
            }
        }

        panel.removeAll();
        panel.add(statsLayout);
    }

    private Component createProductoCard(String nombreProducto, String cantidad, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setAlignItems(Alignment.CENTER);
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("border-left", "4px solid " + color)
                .set("background-color", "#f9f9f9")
                .set("border-radius", "8px")
                .set("min-width", "140px")
                .set("padding", "15px");

        Span cantidadSpan = new Span(cantidad);
        cantidadSpan.getStyle()
                .set("color", color)
                .set("font-size", "42px")
                .set("font-weight", "bold")
                .set("line-height", "1");

        Span nombreSpan = new Span(nombreProducto);
        nombreSpan.getStyle()
                .set("color", "#333")
                .set("font-size", "13px")
                .set("font-weight", "500")
                .set("margin-top", "8px")
                .set("text-align", "center")
                .set("max-width", "120px")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("white-space", "nowrap");

        card.add(cantidadSpan, nombreSpan);
        return card;
    }
}
