package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Bebidas")
@AnonymousAllowed
@Route(value = "bebidas", layout = MainLayout.class)
@Menu(title = "bebidas")
public class BebidasView extends CategoriaProductosView {

    public BebidasView(ProductoService productoService) {
        super(productoService, "Nuestras Bebidas", "Bebidas");
    }
}
