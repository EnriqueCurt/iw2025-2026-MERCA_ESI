package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("") // Ruta principal "/"
public class MainView extends VerticalLayout {

    public MainView() {
        add(
                new H1("¡Hola soy Quique y molo un huevo!"),
                new Button("Haz clic", e -> e.getSource().setText("¡Hecho!"))
        );
    }
}