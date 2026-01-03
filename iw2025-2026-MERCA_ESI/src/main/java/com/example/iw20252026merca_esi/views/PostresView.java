package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Postres")
@AnonymousAllowed
@Route(value = "postres", layout = MainLayout.class)
@Menu(title = "Postres")
public class PostresView extends CategoriaProductosView {

    public PostresView(ProductoService productoService) {
        super(productoService, "Nuestros Postres", "Desserts");
    }
}
