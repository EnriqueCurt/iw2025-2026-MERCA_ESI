package com.example.iw20252026merca_esi.views;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Carrito")
@AnonymousAllowed
@Route(value = "carrito", layout = MainLayout.class)
@Menu(title = "Carrito")
public class CarritoView extends VerticalLayout {

    public CarritoView(){

        H1 titulo = new H1("Tu Carrito");

        add(titulo);
    }
}
