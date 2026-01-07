package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.EstadoPedido;
import com.example.iw20252026merca_esi.model.Pedido;
import com.example.iw20252026merca_esi.repository.PedidoRepository;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "pedidos-pendientes", layout = MainLayout.class)
@PageTitle("Gestión de Pedidos - MercaESI")
@RolesAllowed({"ADMINISTRADOR", "PROPIETARIO", "MANAGER", "REPARTIDOR"})
public class PedidosPendientesView extends VerticalLayout implements BeforeEnterObserver {

    private static final String FONTSIZE = "font-size";
    private static final String BORDERRADIUS = "border-radius";
    private static final String COLOR = "color";
    private static final String COLOR2 = "#4CAF50";
    private static final String COLOR3 = "#2196F3";
    private static final String COLOR4 = "#D32F2F";
    private static final String BACKGROUNDCOLOR = "background-color";

    private final PedidoRepository pedidoRepository;
    private final SessionService sessionService;
    private Grid<Pedido> pedidosGrid;
    private MultiSelectComboBox<String> filtroEstados;
    private Div statsPanel;

    @Autowired
    public PedidosPendientesView(PedidoRepository pedidoRepository, SessionService sessionService) {
        this.pedidoRepository = pedidoRepository;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 titulo = new H2("Gestión de Pedidos");
        titulo.getStyle().set(COLOR, COLOR4);

        Button btnActualizar = new Button("Actualizar", new Icon(VaadinIcon.REFRESH));
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.getStyle().set(BACKGROUNDCOLOR, COLOR4);
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
        filtroEstados.setItems("PENDIENTE_PAGO", "EN_COCINA", "EN_REPARTO", "LISTO", "FINALIZADO");
        filtroEstados.select("PENDIENTE_PAGO", "EN_COCINA");
        filtroEstados.setWidth("400px");
        filtroEstados.addValueChangeListener(e -> cargarPedidos());

        filtrosLayout.add(filtroEstados);
        return filtrosLayout;
    }

    private void configurarGrid() {
        pedidosGrid = new Grid<>(Pedido.class, false);
        pedidosGrid.setHeight("600px");
        pedidosGrid.setWidthFull();

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

        pedidosGrid.addComponentColumn(pedido -> {
            Span badge = new Span(pedido.getEstadoDescripcion());
            badge.getElement().getThemeList().add("badge");
            
            String colorFondo = switch(pedido.getEstado()) {
                case "PENDIENTE_PAGO" -> "#FF9800";
                case "EN_COCINA" -> COLOR3;
                case "EN_REPARTO" -> "#9C27B0";
                case "LISTO" -> COLOR2;
                case "FINALIZADO" -> COLOR2;
                default -> "#757575";
            };
            
            badge.getStyle()
                    .set(BACKGROUNDCOLOR, colorFondo)
                    .set(COLOR, "white")
                    .set("padding", "4px 12px")
                    .set(BORDERRADIUS, "12px")
                    .set(FONTSIZE, "12px")
                    .set("font-weight", "500");
            
            return badge;
        })
                .setHeader("Estado")
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("150px");

        pedidosGrid.addComponentColumn(pedido -> {
            Button btnCambiarEstado = new Button("Cambiar Estado", new Icon(VaadinIcon.EDIT));
            btnCambiarEstado.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            btnCambiarEstado.getStyle().set(BACKGROUNDCOLOR, COLOR3);
            btnCambiarEstado.addClickListener(e -> mostrarDialogoCambioEstado(pedido));
            
            if ("FINALIZADO".equals(pedido.getEstado())) {
                btnCambiarEstado.setEnabled(false);
            }

            return btnCambiarEstado;
        })
                .setHeader("Acciones")
                .setFlexGrow(1);
    }

    private void cargarPedidos() {
        Set<String> estadosSeleccionados = filtroEstados.getSelectedItems();
        List<Pedido> pedidos;
        
        if (estadosSeleccionados.isEmpty()) {
            pedidos = pedidoRepository.findAllByOrderByFechaAsc();
        } else {
            pedidos = pedidoRepository.findAllByOrderByFechaAsc().stream()
                    .filter(p -> estadosSeleccionados.contains(p.getEstado()))
                    .collect(Collectors.toList());
        }
        
        pedidosGrid.setItems(pedidos);
        actualizarEstadisticas(statsPanel);
        
        Notification.show(String.format("Se encontraron %d pedidos", pedidos.size()))
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void mostrarDialogoCambioEstado(Pedido pedido) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H3 titulo = new H3("Cambiar Estado - Pedido #" + pedido.getIdPedido());
        titulo.getStyle().set(COLOR, COLOR4).set("margin-top", "0");

        ComboBox<EstadoPedido> estadoCombo = new ComboBox<>("Nuevo Estado");
        estadoCombo.setItems(EstadoPedido.values());
        estadoCombo.setValue(pedido.getEstadoPedido());
        estadoCombo.setItemLabelGenerator(EstadoPedido::getDescripcion);
        estadoCombo.setWidthFull();

        Button btnGuardar = new Button("Guardar", new Icon(VaadinIcon.CHECK));
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set(BACKGROUNDCOLOR, COLOR2);
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

        VerticalLayout dialogLayout = new VerticalLayout(titulo, estadoCombo, botonesLayout);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void cambiarEstadoPedido(Pedido pedido, EstadoPedido nuevoEstado) {
        String estadoAnterior = pedido.getEstadoDescripcion();
        pedido.setEstadoPedido(nuevoEstado);
        
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

    private Div createStatsPanel() {
        Div panel = new Div();
        panel.getStyle()
                .set(BACKGROUNDCOLOR, "white")
                .set(BORDERRADIUS, "8px")
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
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.AROUND);

        long pendientesPago = pedidoRepository.countByEstado("PENDIENTE_PAGO");
        long enCocina = pedidoRepository.countByEstado("EN_COCINA");
        long listos = pedidoRepository.countByEstado("LISTO");
        long enReparto = pedidoRepository.countByEstado("EN_REPARTO");
        
        statsLayout.add(
            createStatCard("Pendiente de Pago", String.valueOf(pendientesPago), "#FF9800"),
            createStatCard("En Cocina", String.valueOf(enCocina), COLOR3),
            createStatCard("Listos", String.valueOf(listos), COLOR2),
            createStatCard("En Reparto", String.valueOf(enReparto), "#9C27B0")
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
                .set(BACKGROUNDCOLOR, "#f9f9f9")
                .set(BORDERRADIUS, "4px");

        Span tituloSpan = new Span(titulo);
        tituloSpan.getStyle()
                .set(COLOR, "#666")
                .set(FONTSIZE, "14px");

        Span valorSpan = new Span(valor);
        valorSpan.getStyle()
                .set(COLOR, color)
                .set(FONTSIZE, "32px")
                .set("font-weight", "bold");

        card.add(valorSpan, tituloSpan);
        return card;
    }

}
