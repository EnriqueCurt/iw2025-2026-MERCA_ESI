package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Burgers")
@AnonymousAllowed
@Route(value = "burgers", layout = MainLayout.class)
@Menu(title = "Burgers")
public class BurgersView extends CategoriaProductosView {

    public BurgersView(ProductoService productoService) {
        super(productoService, "Nuestras Hamburguesas", "Hamburguesas");
    }
}
