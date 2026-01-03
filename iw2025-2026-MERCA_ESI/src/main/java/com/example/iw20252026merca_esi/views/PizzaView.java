package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Pizzas")
@AnonymousAllowed
@Route(value = "pizzas", layout = MainLayout.class)
@Menu(title = "Pizzas")
public class PizzaView extends CategoriaProductosView {

    public PizzaView(ProductoService productoService) {
        super(productoService, "Nuestras Pizzas", "Pizzas");
    }
}
