package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.ItemPedido;
import com.example.iw20252026merca_esi.service.PedidoActualService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;

@PageTitle("Mi Pedido")
@AnonymousAllowed
@Route(value = "carrito", layout = MainLayout.class)
@Menu(title = "Mi Pedido")
public class CarritoView extends VerticalLayout {

    private final PedidoActualService pedidoActualService;
    private Grid<ItemPedido> grid;
    private Span totalSpan;
    private Div mainContainer;

    public CarritoView(PedidoActualService pedidoActualService) {
        this.pedidoActualService = pedidoActualService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set("background", "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)")
                .set("overflow-y", "auto");

        crearContenido();
    }

    private void crearContenido() {
        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "clamp(16px, 3vw, 24px)")
                .set("flex-wrap", "wrap");

        H1 titulo = new H1("Mi Pedido");
        titulo.getStyle()
                .set("margin", "0")
                .set("color", "#e30613");

        Button limpiarBtn = new Button("Vaciar Pedido", new Icon(VaadinIcon.TRASH));
        limpiarBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        limpiarBtn.addClickListener(e -> confirmarLimpiarPedido());

        header.add(titulo, limpiarBtn);
        add(header);

        // Contenedor principal
        mainContainer = new Div();
        mainContainer.getStyle()
                .set("max-width", "1200px")
                .set("margin", "0 auto")
                .set("padding", "20px")
                .set("width", "100%");

        // Verificar si hay items
        List<ItemPedido> items = pedidoActualService.getPedidoActual();
        if (items.isEmpty()) {
            mostrarPedidoVacio(mainContainer);
        } else {
            mostrarPedidoConItems(mainContainer);
        }

        add(mainContainer);
    }

    private void mostrarPedidoVacio(Div container) {
        Div emptyState = new Div();
        emptyState.getStyle()
                .set("text-align", "center")
                .set("padding", "60px 20px")
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 14px rgba(0,0,0,0.10)");

        Icon icon = new Icon(VaadinIcon.CART);
        icon.setSize("80px");
        icon.getStyle().set("color", "#ccc");

        H2 mensaje = new H2("Tu pedido está vacío");
        mensaje.getStyle()
                .set("color", "#666")
                .set("margin", "20px 0");

        Paragraph texto = new Paragraph("¡Agrega productos de nuestra carta para comenzar!");
        texto.getStyle().set("color", "#999");

        Button irCartaBtn = new Button("Ver Carta", new Icon(VaadinIcon.BOOK));
        irCartaBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        irCartaBtn.getStyle()
                .set("background-color", "#e30613")
                .set("margin-top", "20px");
        irCartaBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("carta")));

        emptyState.add(icon, mensaje, texto, irCartaBtn);
        container.add(emptyState);
    }

    private void mostrarPedidoConItems(Div container) {
        // Grid de items
        grid = new Grid<>(ItemPedido.class, false);
        grid.setAllRowsVisible(true);
        grid.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 14px rgba(0,0,0,0.10)");

        // Columna de producto/menú
        grid.addColumn(new ComponentRenderer<>(item -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(false);
            layout.setPadding(false);

            H4 nombre = new H4(item.getNombre());
            nombre.getStyle()
                    .set("margin", "0")
                    .set("color", "#333");

            Span tipo = new Span(item.esProducto() ? "Producto" : "Menú");
            tipo.getStyle()
                    .set("font-size", "0.85rem")
                    .set("color", "#666");

            layout.add(nombre, tipo);

            // Mostrar exclusiones de ingredientes si las hay
            if (item.tieneExclusiones()) {
                String textoExclusiones = item.getTextoExclusiones();
                if (!textoExclusiones.isEmpty()) {
                    Span exclusiones = new Span(textoExclusiones);
                    exclusiones.getStyle()
                            .set("font-size", "0.8rem")
                            .set("color", "#d32f2f")
                            .set("font-weight", "600")
                            .set("font-style", "italic");
                    layout.add(exclusiones);
                }
            }

            if (item.esMenu() && item.getProductosIncluidos() != null) {
                Span incluye = new Span("Incluye: " + item.getProductosIncluidos());
                incluye.getStyle()
                        .set("font-size", "0.8rem")
                        .set("color", "#999")
                        .set("font-style", "italic");
                layout.add(incluye);
            }

            return layout;
        })).setHeader("Item").setAutoWidth(true).setFlexGrow(1);

        // Columna de cantidad
        grid.addColumn(new ComponentRenderer<>(item -> {
            IntegerField cantidadField = new IntegerField();
            cantidadField.setValue(item.getCantidad());
            cantidadField.setMin(1);
            cantidadField.setMax(99);
            cantidadField.setStepButtonsVisible(true);
            cantidadField.setWidth("120px");
            cantidadField.addValueChangeListener(e -> {
                if (e.getValue() != null && e.getValue() > 0) {
                    pedidoActualService.actualizarCantidad(item, e.getValue());
                    grid.getDataProvider().refreshItem(item);
                    actualizarTotal();
                }
            });
            return cantidadField;
        })).setHeader("Cantidad").setAutoWidth(true);

        // Columna de precio
        grid.addColumn(item -> String.format("%.2f€", item.getPrecio()))
                .setHeader("Precio Unit.")
                .setAutoWidth(true);

        // Columna de subtotal
        grid.addColumn(item -> String.format("%.2f€", item.getSubtotal()))
                .setHeader("Subtotal")
                .setAutoWidth(true);

        // Columna de acciones
        grid.addColumn(new ComponentRenderer<>(item -> {
            Button eliminarBtn = new Button(new Icon(VaadinIcon.TRASH));
            eliminarBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_ICON);
            eliminarBtn.addClickListener(e -> {
                pedidoActualService.eliminarItem(item);
                Notification.show("Item eliminado", 2000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                actualizarVista();
            });
            return eliminarBtn;
        })).setHeader("").setAutoWidth(true);

        grid.setItems(pedidoActualService.getPedidoActual());

        container.add(grid);

        // Panel de total y confirmación
        Div totalPanel = new Div();
        totalPanel.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 14px rgba(0,0,0,0.10)")
                .set("padding", "20px")
                .set("margin-top", "20px")
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("flex-wrap", "wrap")
                .set("gap", "20px");

        VerticalLayout totalInfo = new VerticalLayout();
        totalInfo.setSpacing(false);
        totalInfo.setPadding(false);

        Span totalLabel = new Span("Total del pedido:");
        totalLabel.getStyle()
                .set("font-size", "1.1rem")
                .set("color", "#666");

        totalSpan = new Span(String.format("%.2f€", pedidoActualService.getTotal()));
        totalSpan.getStyle()
                .set("font-size", "2rem")
                .set("font-weight", "700")
                .set("color", "#e30613");

        totalInfo.add(totalLabel, totalSpan);

        Button confirmarBtn = new Button("Confirmar Pedido", new Icon(VaadinIcon.CHECK_CIRCLE));
        confirmarBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        confirmarBtn.getStyle()
                .set("background-color", "#e30613")
                .set("font-size", "1.1rem")
                .set("padding", "15px 30px");
        confirmarBtn.addClickListener(e -> confirmarPedido());

        totalPanel.add(totalInfo, confirmarBtn);
        container.add(totalPanel);
    }

    private void actualizarTotal() {
        if (totalSpan != null) {
            totalSpan.setText(String.format("%.2f€", pedidoActualService.getTotal()));
        }
    }

    private void confirmarLimpiarPedido() {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar");

        Paragraph mensaje = new Paragraph("¿Estás seguro de que deseas vaciar tu pedido?");

        Button confirmarBtn = new Button("Sí, vaciar", e -> {
            pedidoActualService.limpiarPedido();
            Notification.show("Pedido vaciado", 2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            confirmDialog.close();
            actualizarVista();
        });
        confirmarBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button cancelarBtn = new Button("Cancelar", e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(cancelarBtn, confirmarBtn);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(mensaje, buttons);
        dialogLayout.setPadding(false);

        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }

    private void confirmarPedido() {
        // TODO: Implementar cuando tengamos PedidoService
        Notification notification = Notification.show(
                "Funcionalidad de confirmación en desarrollo. Por ahora, tu pedido se mantiene en la sesión.",
                3000,
                Notification.Position.MIDDLE
        );
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    private void actualizarVista() {
        mainContainer.removeAll();
        List<ItemPedido> items = pedidoActualService.getPedidoActual();
        if (items.isEmpty()) {
            mostrarPedidoVacio(mainContainer);
        } else {
            mostrarPedidoConItems(mainContainer);
        }
    }
}
