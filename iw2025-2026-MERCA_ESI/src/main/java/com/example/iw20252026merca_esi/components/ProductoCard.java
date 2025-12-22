package com.example.iw20252026merca_esi.components;

import com.example.iw20252026merca_esi.model.Producto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

public class ProductoCard extends Div {

    private static final String WIDTH = "width";
    private static final String DISPLAY = "display";
    private static final String FONTSIZE = "font-size";
    private static final String FONTWEIGHT = "font-weight";
    private static final String COLOR = "color";

    public ProductoCard(Producto producto) {
        // Estilos del contenedor principal (Tarjeta)
        getStyle()
                .set("background", "white")
                .set("border-radius", "24px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.08)")
                .set(WIDTH, "300px")
                .set(DISPLAY, "flex")
                .set("flex-direction", "column")
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
                .set("background-color", "#f0f0f0")
                .set("position", "relative");

        if (producto.getImagen() != null && producto.getImagen().length > 0) {
            Image imagen = new Image();
            String base64 = java.util.Base64.getEncoder().encodeToString(producto.getImagen());
            imagen.setSrc("data:image/png;base64," + base64);
            imagen.setAlt(producto.getNombre());
            imagen.setWidth("100%");
            imagen.setHeight("100%");
            imagen.getStyle().set("object-fit", "cover");
            contenedorImagen.add(imagen);
        }

        // 2. CONTENIDO
        Div contenido = new Div();
        contenido.getStyle()
                .set("padding", "24px")
                .set(DISPLAY, "flex")
                .set("flex-direction", "column")
                .set("flex-grow", "1")
                .set("gap", "12px");

        H3 nombre = new H3(producto.getNombre());
        nombre.getStyle()
                .set("margin", "0")
                .set(FONTSIZE, "1.2rem")
                .set(FONTWEIGHT, "800")
                .set(COLOR, "#000");

        Span descripcion = new Span(producto.getDescripcion() != null ? producto.getDescripcion() : "Descripción no disponible");
        descripcion.getStyle()
                .set(FONTSIZE, "0.95rem")
                .set(COLOR, "#444")
                .set("line-height", "1.4")
                .set(DISPLAY, "-webkit-box")
                .set("-webkit-line-clamp", "3")
                .set("-webkit-box-orient", "vertical")
                .set("overflow", "hidden");

        Div infoExtra = new Div();
        infoExtra.getStyle()
                .set(DISPLAY, "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("margin-top", "auto");

        Span precio = new Span(String.format("%.2f€", producto.getPrecio()));
        precio.getStyle().set(FONTWEIGHT, "bold");

        Span verMas = new Span("Ver más");
        verMas.getStyle()
                .set(COLOR, "#E3001B")
                .set("text-decoration", "underline")
                .set("cursor", "pointer")
                .set(FONTSIZE, "0.9rem");

        infoExtra.add(precio, verMas);

        Button btnPedir = new Button("PEDIR");
        btnPedir.getStyle()
                .set("background-color", "#E3001B")
                .set(COLOR, "white")
                .set("border-radius", "999px")
                .set(FONTWEIGHT, "700")
                .set(WIDTH , "100%")
                .set("height", "45px")
                .set(FONTSIZE, "1rem")
                .set("margin-top", "16px")
                .set("cursor", "pointer");
        btnPedir.addThemeName("primary");

        // Lógica del botón (puedes agregar eventos aquí)
        btnPedir.addClickListener(e -> {
            // Aquí iría la lógica para añadir al carrito
            System.out.println("Añadido al carrito: " + producto.getNombre());
        });

        contenido.add(nombre, descripcion, infoExtra, btnPedir);
        add(contenedorImagen, contenido);
    }
}

