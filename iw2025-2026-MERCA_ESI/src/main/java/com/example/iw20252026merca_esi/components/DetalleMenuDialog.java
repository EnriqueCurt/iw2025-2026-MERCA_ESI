package com.example.iw20252026merca_esi.components;

import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.model.Producto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class DetalleMenuDialog extends Dialog {

    private static final String MARGIN = "margin";
    private static final String COLOR = "color";
    private static final String COLOR1 = "#E3001B";
    private static final String BORDER_RADIUS = "border-radius";
    private static final String FONT_WEIGHT = "font-weight";

    public DetalleMenuDialog(Menu menu) {
        setWidth("500px");
        setCloseOnEsc(true);
        setCloseOnOutsideClick(true);

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        // Título
        H2 titulo = new H2(menu.getNombre());
        titulo.getStyle()
                .set(MARGIN, "0 0 16px 0")
                .set(COLOR, COLOR1);

        // Imagen
        if (menu.getImagen() != null && menu.getImagen().length > 0) {
            Image imagen = new Image();
            String base64 = java.util.Base64.getEncoder().encodeToString(menu.getImagen());
            imagen.setSrc("data:image/png;base64," + base64);
            imagen.setAlt(menu.getNombre());
            imagen.setWidth("100%");
            imagen.setHeight("250px");
            imagen.getStyle()
                    .set("object-fit", "cover")
                    .set(BORDER_RADIUS, "12px");
            layout.add(imagen);
        }

        // Descripción
        Div descripcionDiv = new Div();
        H3 descripcionTitulo = new H3("Descripción");
        descripcionTitulo.getStyle().set(MARGIN, "8px 0");

        Span descripcion = new Span(menu.getDescripcion() != null ? menu.getDescripcion() : "Sin descripción");
        descripcion.getStyle()
                .set(COLOR, "#666")
                .set("line-height", "1.6");

        descripcionDiv.add(descripcionTitulo, descripcion);

        // Productos incluidos
        Div productosDiv = new Div();
        H3 productosTitulo = new H3("Productos incluidos");
        productosTitulo.getStyle().set(MARGIN, "16px 0 8px 0");
        productosDiv.add(productosTitulo);

        if (menu.getProductos() != null && !menu.getProductos().isEmpty()) {
            for (Producto producto : menu.getProductos()) {
                Div itemProducto = new Div();
                itemProducto.getStyle()
                        .set("padding", "8px 12px")
                        .set("background-color", "#f5f5f5")
                        .set(BORDER_RADIUS, "8px")
                        .set("margin-bottom", "8px")
                        .set("display", "flex")
                        .set("justify-content", "space-between")
                        .set("align-items", "center");

                Span nombreProducto = new Span(producto.getNombre());
                nombreProducto.getStyle()
                        .set(FONT_WEIGHT, "500")
                        .set(COLOR, "#333");

                Span precioProducto = new Span(String.format("%.2f€", producto.getPrecio()));
                precioProducto.getStyle()
                        .set(COLOR, COLOR1)
                        .set(FONT_WEIGHT, "bold");

                itemProducto.add(nombreProducto);
                productosDiv.add(itemProducto);
            }
        } else {
            Span sinProductos = new Span("No hay productos asociados");
            sinProductos.getStyle()
                    .set(COLOR, "#999")
                    .set("font-style", "italic");
            productosDiv.add(sinProductos);
        }

        // Precio total
        Div precioTotal = new Div();
        precioTotal.getStyle()
                .set("margin-top", "16px")
                .set("padding", "12px")
                .set("background-color", COLOR1)
                .set(COLOR, "white")
                .set(BORDER_RADIUS, "8px")
                .set("text-align", "center")
                .set(FONT_WEIGHT, "bold")
                .set("font-size", "1.2rem");
        precioTotal.setText(String.format("Precio Total: %.2f€", menu.getPrecio()));

        // Botón cerrar
        Button btnCerrar = new Button("Cerrar", e -> close());
        btnCerrar.getStyle()
                .set("width", "100%")
                .set("margin-top", "16px");

        layout.add(titulo, descripcionDiv, productosDiv, precioTotal, btnCerrar);
        add(layout);
    }
}
