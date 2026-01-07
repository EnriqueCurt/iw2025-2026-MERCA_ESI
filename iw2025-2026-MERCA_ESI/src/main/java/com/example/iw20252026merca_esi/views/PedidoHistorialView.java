package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.EstadoPedido;
import com.example.iw20252026merca_esi.model.ItemPedido;
import com.example.iw20252026merca_esi.model.Pedido;
import com.example.iw20252026merca_esi.service.PedidoActualService;
import com.example.iw20252026merca_esi.service.PedidoService;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@PageTitle("Mis Pedidos")
@AnonymousAllowed
@Route(value = "mis-pedidos", layout = MainLayout.class)
@Menu(title = "Mis pedidos")
public class PedidoHistorialView extends VerticalLayout {

    private final PedidoService pedidoService;
    private final PedidoActualService pedidoActualService;
    private final SessionService sessionService;

    private final Grid<Pedido> grid = new Grid<>(Pedido.class, false);

    public PedidoHistorialView(PedidoService pedidoService, PedidoActualService pedidoActualService, SessionService sessionService) {
        this.pedidoService = pedidoService;
        this.pedidoActualService = pedidoActualService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Historial de pedidos"));

        configurarGrid();
        add(grid);

        cargar();
    }

    private void configurarGrid() {
        grid.setWidthFull();
        grid.setHeight("650px");

        grid.addColumn(Pedido::getIdPedido).setHeader("ID").setWidth("90px").setFlexGrow(0);
        grid.addColumn(p -> p.getFecha() != null ? p.getFecha().toString() : "").setHeader("Fecha");
        grid.addColumn(p -> String.format("%.2f €", p.getTotal())).setHeader("Total").setWidth("140px").setFlexGrow(0);
        grid.addColumn(Pedido::getEstadoDescripcion).setHeader("Estado").setWidth("180px").setFlexGrow(0);

        grid.addComponentColumn(p -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button ver = new Button("Ver");
            ver.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            ver.addClickListener(e -> mostrarDetalle(p));
            actions.add(ver);

            // Botón Modificar/Rehacer - disponible para todos los pedidos
            Button modificar = new Button(p.tieneEstado(EstadoPedido.PENDIENTE_PAGO) ? "Modificar" : "Rehacer");
            modificar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            modificar.getStyle().set("background-color", "#e30613");
            modificar.addClickListener(e -> cargarPedidoEnCarrito(p));
            actions.add(modificar);

            // Botón Cancelar - solo para PENDIENTE_PAGO
            if (p.tieneEstado(EstadoPedido.PENDIENTE_PAGO)) {
                Button cancelar = new Button("Cancelar");
                cancelar.addThemeVariants(ButtonVariant.LUMO_ERROR);
                cancelar.addClickListener(e -> dialogoCancelar(p));
                actions.add(cancelar);
            }

            return actions;
        }).setHeader("Acciones");
    }

    private void cargar() {
        Cliente cliente = sessionService.getCliente();
        if (cliente == null) {
            Notification.show("Debes iniciar sesión para ver tus pedidos", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            grid.setItems(List.of());
            return;
        }

        grid.setItems(pedidoService.listarPedidosPorCliente(cliente.getIdCliente()));
    }

    private void mostrarDetalle(Pedido pedido) {
        Dialog d = new Dialog();
        d.setWidth("700px");
        d.setHeight("600px");

        VerticalLayout l = new VerticalLayout();
        l.setPadding(true);
        l.setSpacing(true);

        // Cabecera del pedido
        Span titulo = new Span("Pedido #" + pedido.getIdPedido());
        titulo.getStyle()
                .set("font-size", "1.5rem")
                .set("font-weight", "bold")
                .set("color", "#e30613");
        l.add(titulo);

        // Información general
        VerticalLayout infoGeneral = new VerticalLayout();
        infoGeneral.setPadding(false);
        infoGeneral.setSpacing(false);
        infoGeneral.getStyle()
                .set("background-color", "#f5f5f5")
                .set("padding", "12px")
                .set("border-radius", "8px");

        infoGeneral.add(new Span("📅 Fecha: " + pedido.getFecha()));
        infoGeneral.add(new Span("📊 Estado: " + pedido.getEstadoDescripcion()));
        infoGeneral.add(new Span("💰 Total: " + String.format("%.2f €", pedido.getTotal())));

        if (pedido.getADomicilio() != null && pedido.getADomicilio()) {
            infoGeneral.add(new Span("🚚 Entrega: A domicilio"));
            if (pedido.getDireccion() != null) {
                infoGeneral.add(new Span("📍 Dirección: " + pedido.getDireccion()));
            }
        } else if (pedido.getParaLlevar() != null && pedido.getParaLlevar()) {
            infoGeneral.add(new Span("🥡 Entrega: Para llevar"));
        } else {
            infoGeneral.add(new Span("🍽️ Entrega: Local"));
        }

        if (pedido.getMotivoCancelacion() != null && !pedido.getMotivoCancelacion().trim().isEmpty()) {
            Span motivoCancelacion = new Span("❌ Motivo cancelación: " + pedido.getMotivoCancelacion());
            motivoCancelacion.getStyle().set("color", "#d32f2f");
            infoGeneral.add(motivoCancelacion);
        }

        l.add(infoGeneral);

        // Cargar detalles del pedido
        Cliente cliente = sessionService.getCliente();
        if (cliente != null) {
            try {
                List<ItemPedido> items = pedidoService.cargarItemsDePedidoSinValidacion(
                    pedido.getIdPedido(), cliente.getIdCliente());

                if (!items.isEmpty()) {
                    H3 tituloItems = new H3("Contenido del pedido:");
                    tituloItems.getStyle().set("margin-top", "16px");
                    l.add(tituloItems);

                    VerticalLayout itemsContainer = new VerticalLayout();
                    itemsContainer.setPadding(false);
                    itemsContainer.setSpacing(true);

                    for (ItemPedido item : items) {
                        HorizontalLayout itemLayout = new HorizontalLayout();
                        itemLayout.setWidthFull();
                        itemLayout.getStyle()
                                .set("background-color", "#ffffff")
                                .set("padding", "10px")
                                .set("border-radius", "6px")
                                .set("border", "1px solid #e0e0e0");

                        VerticalLayout itemInfo = new VerticalLayout();
                        itemInfo.setPadding(false);
                        itemInfo.setSpacing(false);
                        itemInfo.setWidthFull();

                        Span nombreItem = new Span((item.esProducto() ? "🍕 " : "📦 ") + item.getNombre());
                        nombreItem.getStyle()
                                .set("font-weight", "bold")
                                .set("font-size", "1.05rem");
                        itemInfo.add(nombreItem);

                        Span cantidadPrecio = new Span(
                            "Cantidad: " + item.getCantidad() +
                            " × " + String.format("%.2f €", item.getPrecio()) +
                            " = " + String.format("%.2f €", item.getSubtotal())
                        );
                        cantidadPrecio.getStyle().set("color", "#666");
                        itemInfo.add(cantidadPrecio);

                        if (item.tieneExclusiones()) {
                            Span exclusiones = new Span("🚫 " + item.getTextoExclusiones());
                            exclusiones.getStyle()
                                    .set("font-size", "0.9rem")
                                    .set("color", "#d32f2f")
                                    .set("font-style", "italic");
                            itemInfo.add(exclusiones);
                        }

                        if (item.esMenu() && item.getProductosIncluidos() != null) {
                            Span incluye = new Span("Incluye: " + item.getProductosIncluidos());
                            incluye.getStyle()
                                    .set("font-size", "0.85rem")
                                    .set("color", "#999");
                            itemInfo.add(incluye);
                        }

                        itemLayout.add(itemInfo);
                        itemsContainer.add(itemLayout);
                    }

                    l.add(itemsContainer);
                }
            } catch (Exception ex) {
                Span error = new Span("No se pudieron cargar los detalles del pedido");
                error.getStyle().set("color", "#d32f2f");
                l.add(error);
            }
        }

        // Botones
        HorizontalLayout botonesLayout = new HorizontalLayout();
        botonesLayout.setWidthFull();
        botonesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        botonesLayout.setSpacing(true);

        Button cerrar = new Button("Cerrar", e -> d.close());
        cerrar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button rehacer = new Button(
            pedido.tieneEstado(EstadoPedido.PENDIENTE_PAGO) ? "Modificar pedido" : "Rehacer pedido",
            e -> {
                cargarPedidoEnCarrito(pedido);
                d.close();
            }
        );
        rehacer.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        rehacer.getStyle().set("background-color", "#e30613");

        botonesLayout.add(cerrar, rehacer);
        l.add(botonesLayout);

        d.add(l);
        d.open();
    }

    private void cargarPedidoEnCarrito(Pedido pedido) {
        Cliente cliente = sessionService.getCliente();
        if (cliente == null) {
            Notification.show("Debes iniciar sesión", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            // Cargar items del pedido sin validar estado (permite rehacer cualquier pedido)
            List<ItemPedido> items = pedidoService.cargarItemsDePedidoSinValidacion(
                pedido.getIdPedido(), cliente.getIdCliente());

            pedidoActualService.setPedidoActual(items);

            // Si es PENDIENTE_PAGO, navegar en modo edición; si no, como pedido nuevo
            if (pedido.tieneEstado(EstadoPedido.PENDIENTE_PAGO)) {
                getUI().ifPresent(ui -> ui.navigate("carrito?editarPedidoId=" + pedido.getIdPedido()));
            } else {
                getUI().ifPresent(ui -> ui.navigate("carrito"));
            }

            Notification.show(
                pedido.tieneEstado(EstadoPedido.PENDIENTE_PAGO) ?
                    "Pedido cargado para edición" :
                    "Pedido cargado en el carrito para rehacer",
                2500,
                Notification.Position.MIDDLE
            ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("No se pudo cargar el pedido: " + ex.getMessage(), 4000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void dialogoCancelar(Pedido pedido) {
        Dialog d = new Dialog();
        d.setWidth("520px");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Span("¿Cancelar el pedido #" + pedido.getIdPedido() + "?"));

        TextArea motivo = new TextArea("Motivo de cancelación (opcional)");
        motivo.setWidthFull();

        Button confirmar = new Button("Confirmar cancelación");
        confirmar.addThemeVariants(ButtonVariant.LUMO_ERROR);
        confirmar.addClickListener(e -> {
            Cliente cliente = sessionService.getCliente();
            if (cliente == null) {
                Notification.show("Debes iniciar sesión", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                pedidoService.cancelarPedidoSiPendiente(pedido.getIdPedido(), cliente.getIdCliente(), motivo.getValue());
                Notification.show("Pedido cancelado", 2500, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                d.close();
                cargar();
            } catch (Exception ex) {
                Notification.show("No se pudo cancelar: " + ex.getMessage(), 4500, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cerrar = new Button("Cerrar", e -> d.close());

        HorizontalLayout botones = new HorizontalLayout(cerrar, confirmar);
        layout.add(motivo, botones);
        d.add(layout);
        d.open();
    }
}

