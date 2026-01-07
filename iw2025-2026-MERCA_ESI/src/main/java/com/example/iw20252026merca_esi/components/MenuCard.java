package com.example.iw20252026merca_esi.components;

import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.service.PedidoActualService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public class MenuCard extends Div {

    private static final String WIDTH = "width";
    private static final String DISPLAY = "display";
    private static final String FONTSIZE = "font-size";
    private static final String FONTWEIGHT = "font-weight";
    private static final String COLOR = "color";
    private static final String COLOR1 = "white";
    private  static final String COLOR2 = "#E3001B";
    private static final String PADDING = "padding";
    private static final String BORDERRADIUS = "border-radius";
    private static final String FLEXDIRECTION = "flex-direction";
    private static final String COLUMN = "column";
    private static final String BACKGROUNDCOLOR = "background-color";

    public MenuCard(Menu menu, PedidoActualService pedidoActualService) {
        // Estilos del contenedor principal (Tarjeta)
        getStyle()
                .set("background", COLOR1)
                .set(BORDERRADIUS, "24px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)")
                .set(WIDTH, "300px")
                .set(DISPLAY, "flex")
                .set(FLEXDIRECTION, COLUMN)
                .set("margin", "16px")
                .set("overflow", "hidden")
                .set("transition", "transform 0.2s");

        // Efecto hover
        getElement().addEventListener("mouseenter", e ->
                getStyle().set("transform", "translateY(-5px)"));
        getElement().addEventListener("mouseleave", e ->
                getStyle().set("transform", "translateY(0)"));

        // 1. IMAGEN
        Div contenedorImagen = new Div();
        contenedorImagen.getStyle()
                .set(WIDTH, "100%")
                .set("height", "200px")
                .set(BACKGROUNDCOLOR, "#f0f0f0")
                .set("position", "relative");

        if (menu.getImagen() != null && menu.getImagen().length > 0) {
            Image imagen = new Image();
            String base64 = java.util.Base64.getEncoder().encodeToString(menu.getImagen());
            imagen.setSrc("data:image/png;base64," + base64);
            imagen.setAlt(menu.getNombre());
            imagen.setWidth("100%");
            imagen.setHeight("100%");
            imagen.getStyle().set("object-fit", "cover");
            contenedorImagen.add(imagen);
        }

        // Badges de estado
        Div badges = new Div();
        badges.getStyle()
                .set("position", "absolute")
                .set("top", "12px")
                .set("right", "12px")
                .set(DISPLAY, "flex")
                .set("gap", "8px")
                .set(FLEXDIRECTION, COLUMN)
                .set("align-items", "flex-end");

        if (menu.getEsOferta()) {
            Span ofertaBadge = createBadge("OFERTA", "#FF9800");
            badges.add(ofertaBadge);
        }

        if (menu.getPuntos()) {
            Span puntosBadge = createBadge("PUNTOS", "#4CAF50");
            badges.add(puntosBadge);
        }

        if (!menu.getEstado()) {
            Span inactivoBadge = createBadge("INACTIVO", "#9E9E9E");
            badges.add(inactivoBadge);
        }

        contenedorImagen.add(badges);

        // 2. CONTENIDO
        Div contenido = new Div();
        contenido.getStyle()
                .set(PADDING, "24px")
                .set(DISPLAY, "flex")
                .set(FLEXDIRECTION, COLUMN)
                .set("flex-grow", "1")
                .set("gap", "12px");

        H3 nombre = new H3(menu.getNombre());
        nombre.getStyle()
                .set("margin", "0")
                .set(FONTSIZE, "1.2rem")
                .set(FONTWEIGHT, "800")
                .set(COLOR, "#000");

        Span descripcion = new Span(menu.getDescripcion() != null ? menu.getDescripcion() : "Descripción no disponible");
        descripcion.getStyle()
                .set(FONTSIZE, "0.95rem")
                .set(COLOR, "#444")
                .set("line-height", "1.4")
                .set(DISPLAY, "-webkit-box")
                .set("-webkit-line-clamp", "3")
                .set("-webkit-box-orient", "vertical")
                .set("overflow", "hidden");

        // Información de productos incluidos
        if (menu.getProductos() != null && !menu.getProductos().isEmpty()) {
            Span productosInfo = new Span("Incluye " + menu.getProductos().size() + " producto(s)");
            productosInfo.getStyle()
                    .set(FONTSIZE, "0.85rem")
                    .set(COLOR, "#666")
                    .set("font-style", "italic");
            contenido.add(productosInfo);
        }

        Div infoExtra = new Div();
        infoExtra.getStyle()
                .set(DISPLAY, "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("margin-top", "auto");

        Span precio = new Span(String.format("%.2f€", menu.getPrecio()));
        precio.getStyle()
                .set(FONTWEIGHT, "bold")
                .set(FONTSIZE, "1.1rem")
                .set(COLOR, COLOR2);

        Span verMas = new Span("Ver más");
        verMas.getStyle()
                .set(COLOR, COLOR2)
                .set("text-decoration", "underline")
                .set("cursor", "pointer")
                .set(FONTSIZE, "0.9rem");

        infoExtra.add(precio, verMas);

        Button btnPedir = new Button("PEDIR");
        btnPedir.getStyle()
                .set(BACKGROUNDCOLOR, COLOR2)
                .set(COLOR, COLOR1)
                .set(BORDERRADIUS, "999px")
                .set(FONTWEIGHT, "700")
                .set(WIDTH, "100%")
                .set("height", "45px")
                .set(FONTSIZE, "1rem")
                .set("margin-top", "16px")
                .set("cursor", "pointer");
        btnPedir.addThemeName("primary");

        btnPedir.addClickListener(e -> {
            SeleccionIngredientesDialog dialog = new SeleccionIngredientesDialog(
                menu,
                itemPedido -> {
                    // Agregar el item completo con sus exclusiones
                    pedidoActualService.agregarItem(itemPedido);
                    
                    String mensaje = "✓ Menú " + menu.getNombre() + " añadido al pedido";
                    if (itemPedido.tieneExclusiones()) {
                        mensaje += " (personalizado)";
                    }
                    
                    Notification notification = Notification.show(
                        mensaje,
                        2000,
                        Notification.Position.BOTTOM_END
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }
            );
            dialog.open();
        });

        contenido.add(nombre, descripcion, infoExtra, btnPedir);
        add(contenedorImagen, contenido);
    }

    private Span createBadge(String text, String backgroundColor) {
        Span badge = new Span(text);
        badge.getStyle()
                .set(BACKGROUNDCOLOR, backgroundColor)
                .set(COLOR, COLOR1)
                .set(PADDING, "4px 10px")
                .set(BORDERRADIUS, "12px")
                .set(FONTSIZE, "0.7rem")
                .set(FONTWEIGHT, "bold")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");
        return badge;
    }
}
