package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.EstadoPedido;
import com.example.iw20252026merca_esi.model.Pedido;
import com.example.iw20252026merca_esi.repository.PedidoRepository;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "repartidor", layout = MainLayout.class)
@PageTitle("Panel de Repartidor - MercaESI")
@RolesAllowed({"ADMINISTRADOR", "PROPIETARIO", "REPARTIDOR"})
public class RepartidorView extends VerticalLayout implements BeforeEnterObserver {
    
    private final PedidoRepository pedidoRepository;
    private final SessionService sessionService;
    private Grid<Pedido> pedidosGrid;
    private Checkbox checkboxListos;
    private Checkbox checkboxAsignados;
    private Div statsPanel;
    private Pedido pedidoSeleccionado;

    @Autowired
    public RepartidorView(PedidoRepository pedidoRepository, SessionService sessionService) {
        this.pedidoRepository = pedidoRepository;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Título y botones de acción
        HorizontalLayout headerLayout = createHeader();
        
        // Panel de estadísticas
        statsPanel = createStatsPanel();
        
        // Filtros
        HorizontalLayout filtrosLayout = createFiltros();

        // Grid de pedidos
        configurarGrid();

        add(headerLayout, statsPanel, filtrosLayout, pedidosGrid);

        // Cargar datos iniciales
        cargarPedidos();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar que el usuario esté autenticado
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            event.rerouteTo("");
            Notification.show("Acceso denegado.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private HorizontalLayout createHeader() {
        H2 titulo = new H2("Panel de Repartidor");
        titulo.getStyle().set("color", "#D32F2F");

        Button btnActualizar = new Button("Actualizar", new Icon(VaadinIcon.REFRESH));
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.getStyle().set("background-color", "#D32F2F");
        btnActualizar.addClickListener(e -> cargarPedidos());

        HorizontalLayout headerLayout = new HorizontalLayout(titulo, btnActualizar);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.setFlexGrow(1, titulo);
        
        return headerLayout;
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
        actualizarEstadisticas(panel);
        
        return panel;
    }

    private void actualizarEstadisticas(Div panel) {
        Empleado empleado = sessionService.getEmpleado();
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.AROUND);

        // Contar solo pedidos LISTO (disponibles) y EN_REPARTO asignados a este repartidor
        long listos = pedidoRepository.countByEstado("LISTO");
        long enReparto = 0;
        if (empleado != null) {
            enReparto = pedidoRepository.findByEstadoOrderByFechaAsc("EN_REPARTO").stream()
                    .filter(p -> p.getEmpleado() != null && 
                                 p.getEmpleado().getIdEmpleado().equals(empleado.getIdEmpleado()))
                    .count();
        }
        
        statsLayout.add(
            createStatCard("Listos", String.valueOf(listos), "#4CAF50"),
            createStatCard("Mis Repartos", String.valueOf(enReparto), "#9C27B0")
        );

        panel.removeAll();
        panel.add(statsLayout);
    }

    private Component createStatCard(String titulo, String valor, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setAlignItems(Alignment.CENTER);
        card.setPadding(true);
        card.getStyle()
                .set("border-left", "4px solid " + color)
                .set("background-color", "#f9f9f9")
                .set("border-radius", "4px");

        Span tituloSpan = new Span(titulo);
        tituloSpan.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        Span valorSpan = new Span(valor);
        valorSpan.getStyle()
                .set("color", color)
                .set("font-size", "32px")
                .set("font-weight", "bold");

        card.add(valorSpan, tituloSpan);
        return card;
    }

    private HorizontalLayout createFiltros() {
        HorizontalLayout filtrosLayout = new HorizontalLayout();
        filtrosLayout.setAlignItems(Alignment.CENTER);
        filtrosLayout.setSpacing(true);

        Span labelFiltro = new Span("Mostrar:");
        labelFiltro.getStyle().set("font-weight", "bold").set("margin-right", "10px");

        checkboxListos = new Checkbox("Listos");
        checkboxListos.setValue(true);
        checkboxListos.addValueChangeListener(e -> cargarPedidos());

        checkboxAsignados = new Checkbox("Asignados");
        checkboxAsignados.setValue(true);
        checkboxAsignados.addValueChangeListener(e -> cargarPedidos());

        filtrosLayout.add(labelFiltro, checkboxListos, checkboxAsignados);
        return filtrosLayout;
    }

    private void configurarGrid() {
        pedidosGrid = new Grid<>(Pedido.class, false);
        pedidosGrid.setHeight("500px");
        pedidosGrid.setWidthFull();
        pedidosGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        // Columnas
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

        pedidosGrid.addColumn(pedido -> {
            if (pedido.getCliente() != null) {
                return pedido.getCliente().getNombre();
            }
            return "Sin cliente";
        })
                .setHeader("Cliente")
                .setSortable(true)
                .setFlexGrow(1);

        pedidosGrid.addColumn(pedido -> String.format("%.2f €", pedido.getTotal()))
                .setHeader("Total")
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("100px");

        pedidosGrid.addColumn(pedido -> {
            if (pedido.getDireccion() != null && !pedido.getDireccion().isEmpty()) {
                return pedido.getDireccion();
            }
            return "En local";
        })
                .setHeader("Dirección")
                .setSortable(true)
                .setFlexGrow(2);

        pedidosGrid.addColumn(pedido -> {
            if (pedido.getEmpleado() != null) {
                return pedido.getEmpleado().getNombre();
            }
            return "Sin asignar";
        })
                .setHeader("Repartidor")
                .setSortable(true)
                .setFlexGrow(1);

        pedidosGrid.addComponentColumn(pedido -> {
            Span badge = new Span(pedido.getEstadoDescripcion());
            badge.getElement().getThemeList().add("badge");
            
            String colorFondo = switch(pedido.getEstado()) {
                case "PENDIENTE_PAGO" -> "#FF9800";
                case "EN_COCINA" -> "#2196F3";
                case "EN_REPARTO" -> "#9C27B0";
                case "LISTO" -> "#4CAF50";
                case "FINALIZADO" -> "#4CAF50";
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

        // Columna de acciones
        pedidosGrid.addComponentColumn(pedido -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            
            // Botón Asignarme solo para pedidos LISTO
            if ("LISTO".equals(pedido.getEstado())) {
                Button btnAsignar = new Button("Asignarme", new Icon(VaadinIcon.USER_CHECK));
                btnAsignar.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                btnAsignar.getStyle().set("background-color", "#4CAF50");
                btnAsignar.addClickListener(e -> asignarPedido(pedido));
                actions.add(btnAsignar);
            }
            
            Button btnCambiarEstado = new Button("Cambiar Estado", new Icon(VaadinIcon.EDIT));
            btnCambiarEstado.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            btnCambiarEstado.getStyle().set("background-color", "#2196F3");
            btnCambiarEstado.addClickListener(e -> mostrarDialogoCambioEstado(pedido));

            Button btnDetalles = new Button(new Icon(VaadinIcon.INFO_CIRCLE));
            btnDetalles.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            btnDetalles.addClickListener(e -> mostrarDetallesPedido(pedido));

            actions.add(btnCambiarEstado, btnDetalles);
            return actions;
        })
                .setHeader("Acciones")
                .setFlexGrow(1);

        pedidosGrid.addSelectionListener(selection -> {
            selection.getFirstSelectedItem().ifPresent(pedido -> {
                pedidoSeleccionado = pedido;
            });
        });
    }

    private void cargarPedidos() {
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            pedidosGrid.setItems();
            return;
        }
        
        boolean mostrarListos = checkboxListos.getValue();
        boolean mostrarAsignados = checkboxAsignados.getValue();
        
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaAsc().stream()
                .filter(p -> {
                    boolean esListo = "LISTO".equals(p.getEstado());
                    boolean esAsignado = "EN_REPARTO".equals(p.getEstado()) && 
                                        p.getEmpleado() != null && 
                                        p.getEmpleado().getIdEmpleado().equals(empleado.getIdEmpleado());
                    
                    if (mostrarListos && mostrarAsignados) {
                        return esListo || esAsignado;
                    } else if (mostrarListos) {
                        return esListo;
                    } else if (mostrarAsignados) {
                        return esAsignado;
                    } else {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        
        pedidosGrid.setItems(pedidos);
        actualizarEstadisticas(statsPanel);
        
        Notification.show(String.format("Se encontraron %d pedidos", pedidos.size()))
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void asignarPedido(Pedido pedido) {
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            Notification.show("Error: No se pudo obtener el empleado actual")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        // Asignar el pedido al repartidor y cambiar a EN_REPARTO
        pedido.setEmpleado(empleado);
        pedido.setEstadoPedido(EstadoPedido.EN_REPARTO);
        pedidoRepository.save(pedido);
        
        cargarPedidos();
        
        Notification.show("Pedido #" + pedido.getIdPedido() + " asignado y en reparto")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void mostrarDialogoCambioEstado(Pedido pedido) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H3 titulo = new H3("Cambiar Estado - Pedido #" + pedido.getIdPedido());
        titulo.getStyle().set("color", "#D32F2F").set("margin-top", "0");

        ComboBox<EstadoPedido> estadoCombo = new ComboBox<>("Nuevo Estado");
        estadoCombo.setItems(EstadoPedido.values());
        estadoCombo.setValue(pedido.getEstadoPedido());
        estadoCombo.setItemLabelGenerator(EstadoPedido::getDescripcion);
        estadoCombo.setWidthFull();

        Paragraph infoActual = new Paragraph("Estado actual: " + pedido.getEstadoDescripcion());
        infoActual.getStyle().set("color", "#666").set("margin", "10px 0");

        Button btnGuardar = new Button("Guardar", new Icon(VaadinIcon.CHECK));
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set("background-color", "#4CAF50");
        btnGuardar.addClickListener(e -> {
            EstadoPedido nuevoEstado = estadoCombo.getValue();
            if (nuevoEstado != null) {
                cambiarEstadoPedido(pedido, nuevoEstado);
                dialog.close();
            } else {
                Notification.show("Debes seleccionar un estado")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.addClickListener(e -> dialog.close());

        HorizontalLayout botonesLayout = new HorizontalLayout(btnGuardar, btnCancelar);
        botonesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        botonesLayout.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout(titulo, infoActual, estadoCombo, botonesLayout);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void cambiarEstadoPedido(Pedido pedido, EstadoPedido nuevoEstado) {
        String estadoAnterior = pedido.getEstadoDescripcion();
        pedido.setEstadoPedido(nuevoEstado);
        
        // Si el pedido pasa a EN_REPARTO y no tiene empleado asignado, asignar el actual
        if (nuevoEstado == EstadoPedido.EN_REPARTO && pedido.getEmpleado() == null) {
            Empleado empleado = sessionService.getEmpleado();
            pedido.setEmpleado(empleado);
        }
        
        // Si el pedido pasa a FINALIZADO, registrar la fecha de cierre
        if (nuevoEstado == EstadoPedido.FINALIZADO) {
            pedido.setFechaCierre(LocalDateTime.now());
        }
        
        pedidoRepository.save(pedido);
        
        Notification.show(String.format("Pedido #%d: %s → %s", 
                pedido.getIdPedido(), estadoAnterior, nuevoEstado.getDescripcion()))
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        
        cargarPedidos();
    }

    private void mostrarDetallesPedido(Pedido pedido) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H3 titulo = new H3("Detalles del Pedido #" + pedido.getIdPedido());
        titulo.getStyle().set("color", "#D32F2F").set("margin-top", "0");

        VerticalLayout detallesLayout = new VerticalLayout();
        detallesLayout.setPadding(false);
        detallesLayout.setSpacing(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        detallesLayout.add(
            crearLineaDetalle("Cliente:", pedido.getCliente() != null ? pedido.getCliente().getNombre() : "Sin cliente"),
            crearLineaDetalle("Fecha:", pedido.getFecha() != null ? pedido.getFecha().format(formatter) : ""),
            crearLineaDetalle("Estado:", pedido.getEstadoDescripcion()),
            crearLineaDetalle("Total:", String.format("%.2f €", pedido.getTotal())),
            crearLineaDetalle("Empleado:", pedido.getEmpleado() != null ? pedido.getEmpleado().getNombre() : "Sin asignar")
        );

        Button btnCerrar = new Button("Cerrar", e -> dialog.close());
        btnCerrar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout botonesLayout = new HorizontalLayout(btnCerrar);
        botonesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        botonesLayout.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout(titulo, detallesLayout, botonesLayout);
        dialogLayout.setPadding(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private HorizontalLayout crearLineaDetalle(String etiqueta, String valor) {
        HorizontalLayout linea = new HorizontalLayout();
        linea.setWidthFull();
        linea.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Span labelSpan = new Span(etiqueta);
        labelSpan.getStyle().set("font-weight", "bold").set("color", "#333");

        Span valorSpan = new Span(valor);
        valorSpan.getStyle().set("color", "#666");

        linea.add(labelSpan, valorSpan);
        return linea;
    }
}
