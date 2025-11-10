package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Image;

@Route(value = "/", layout = MainLayout.class)
public class MainView extends Div {

    public MainView() {
        setSizeFull();
        getStyle()
                .set("background-image", "url('/images/pizita.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("min-height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column");

        Span welcomeMessage = new Span("Bienvenido a Merca-ESI");
        welcomeMessage.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "bold")
                .set("color", "#ffffff")
                .set("text-shadow", "0 2px 6px rgba(0,0,0,0.4)")
                .set("margin", "40px auto 0 auto");

        Div overlay = new Div();
        overlay.add(welcomeMessage);
        overlay.getStyle()
                .set("background", "rgba(0,0,0,0.35)")
                .set("padding", "30px 40px")
                .set("border-radius", "16px")
                .set("backdrop-filter", "blur(3px)")
                .set("margin", "40px auto")
                .set("max-width", "600px")
                .set("width", "clamp(280px, 80%, 600px)")
                .set("text-align", "center");

        add(overlay);

    }
}