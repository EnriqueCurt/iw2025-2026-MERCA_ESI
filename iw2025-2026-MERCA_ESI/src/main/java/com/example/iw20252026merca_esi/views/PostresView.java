package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.components.ProductoCard;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Postres")
@AnonymousAllowed
@Route(value = "postres", layout = MainLayout.class)
@Menu(title = "Postres")

public class PostresView extends VerticalLayout {
    private final ProductoService productoService;

    public PostresView(ProductoService productoService) {
        this.productoService = productoService;

        H1 titulo = new H1("Postres");
        titulo.getStyle().set("text-align", "center").set("margin-bottom", "20px");
        add(titulo);

        HorizontalLayout productosLayout = new HorizontalLayout();
        productosLayout.setWidthFull();
        productosLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Centrar las tarjetas
        productosLayout.getStyle().set("flex-wrap", "wrap");

        // Aquí reutilizamos el componente ProductoCard
        productoService.findByCategoriaNombre("Postre").forEach(producto -> {
            productosLayout.add(new ProductoCard(producto));
        });

        add(productosLayout);
    }
}
