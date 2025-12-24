package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Menu;

@PageTitle("oficina")
@AnonymousAllowed
@Route(value = "oficina")
@Menu(title = "oficina")
public class Oficina extends VerticalLayout {

    public Oficina() {
        // Header
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.getStyle().set("background-color", "#b51c1c");
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        Image logo = new Image("https://via.placeholder.com/150x50", "logo");
        headerLayout.add(logo);
        HorizontalLayout navLinks = new HorizontalLayout();
        navLinks.add(new Span("OFERTAS"), new Span("MENÚS"), new Span("PIZZAS"), new Span("BURGERS"), new Span("ENTRANTES"), new Span("BEBIDAS"), new Span("POSTRES"), new Span("MITELEPI"));
        headerLayout.add(navLinks);
        // Main Content
        VerticalLayout mainContentLayout = new VerticalLayout();
        mainContentLayout.getStyle().set("background", "url('https://via.placeholder.com/1684x768') no-repeat center center");
        mainContentLayout.setWidthFull();
        mainContentLayout.setHeight("768px");
        mainContentLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mainContentLayout.setAlignItems(Alignment.CENTER);
        Span promoText = new Span("UNA MEDIANA POR SOLO 8'95€ A DOMICILIO WEB/APP");
        promoText.getStyle().set("color", "white");
        promoText.getStyle().set("font-size", "40px");
        promoText.getStyle().set("font-weight", "bold");
        mainContentLayout.add(promoText);
        // Order Section
        HorizontalLayout orderLayout = new HorizontalLayout();
        orderLayout.getStyle().set("background-color", "white");
        orderLayout.getStyle().set("padding", "10px");
        orderLayout.getStyle().set("border-radius", "10px");
        Span orderPrompt = new Span("¿Dónde quieres tu pedido?");
        orderPrompt.getStyle().set("font-weight", "bold");
        Button homeDeliveryButton = new Button("A DOMICILIO");
        Button pickupButton = new Button("A RECOGER");
        TextField addressField = new TextField();
        addressField.setPlaceholder("Introduce tu calle y número");
        Button searchButton = new Button("Buscar");
        orderLayout.add(orderPrompt, homeDeliveryButton, pickupButton, addressField, searchButton);
        mainContentLayout.add(orderLayout);
        add(headerLayout, mainContentLayout);
    }
}
