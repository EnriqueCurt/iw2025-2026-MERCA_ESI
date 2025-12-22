package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.components.ProductoCard; // Importar el nuevo componente
import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Pizzas")
@AnonymousAllowed
@Route(value = "pizzas", layout = MainLayout.class)
@Menu(title = "Pizzas")
public class PizzaView extends VerticalLayout {

    private final ProductoService productoService;

    public PizzaView(ProductoService productoService) {
        this.productoService = productoService;

        H1 titulo = new H1("Nuestras pizzas");
        titulo.getStyle().set("text-align", "center").set("margin-bottom", "20px");
        add(titulo);

        HorizontalLayout productosLayout = new HorizontalLayout();
        productosLayout.setWidthFull();
        productosLayout.setJustifyContentMode(JustifyContentMode.CENTER); // Centrar las tarjetas
        productosLayout.getStyle().set("flex-wrap", "wrap");

        // Aquí reutilizamos el componente ProductoCard
        productoService.findByCategoriaNombre("Pizza").forEach(producto -> {
            productosLayout.add(new ProductoCard(producto));
        });

        add(productosLayout);
    }
}
