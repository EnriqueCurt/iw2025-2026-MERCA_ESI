package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.theme.lumo.LumoUtility;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Menu;

@PageTitle("la carta 2")
@AnonymousAllowed
@Route(value = "la-carta-2")
@Menu(title = "la carta 2")
public class LaCarta2 extends VerticalLayout {

    public LaCarta2() {
        // Company name
        Span companyName = new Span("<Nombre de empresa>");
        companyName.getStyle().set("font-size", "14px");
        add(companyName);
        // Title
        Span title = new Span("Ofertas");
        title.getStyle().set("font-size", "48px");
        title.getStyle().set("font-weight", "bold");
        add(title);
        // Offer 1
        HorizontalLayout offer1 = new HorizontalLayout();
        Span offer1Name = new Span("Pizita + 2 bebidas");
        offer1Name.getStyle().set("font-size", "24px");
        Div offer1Description = new Div(new Span("Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
        offer1Description.getStyle().set("border", "1px solid black");
        offer1Description.getStyle().set("padding", "10px");
        offer1Description.getStyle().set("width", "400px");
        Span offer1Price = new Span("14€");
        offer1Price.getStyle().set("font-size", "24px");
        offer1.add(offer1Name, offer1Description, offer1Price);
        offer1.setAlignItems(Alignment.CENTER);
        add(offer1);
        // Offer 2
        HorizontalLayout offer2 = new HorizontalLayout();
        Span offer2Name = new Span("3 x 2 medianas");
        offer2Name.getStyle().set("font-size", "24px");
        Div offer2Description = new Div(new Span("Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
        offer2Description.getStyle().set("border", "1px solid black");
        offer2Description.getStyle().set("padding", "10px");
        offer2Description.getStyle().set("width", "400px");
        Span offer2Price = new Span("20€");
        offer2Price.getStyle().set("font-size", "24px");
        offer2.add(offer2Name, offer2Description, offer2Price);
        offer2.setAlignItems(Alignment.CENTER);
        add(offer2);
        // Contact link
        Anchor contactLink = new Anchor("#", "Contáctanos");
        contactLink.getStyle().set("font-size", "14px");
        add(contactLink);
        // Back button
        Button backButton = new Button("Volver a inicio");
        add(backButton);
        //quiero que mi boton me lleve a la MainView
        backButton.addClickListener(e ->
                backButton.getUI().ifPresent(ui -> ui.navigate("/"))
        );

setSpacing(false);
Button button = new Button("Button");
add(button);
    }
}
