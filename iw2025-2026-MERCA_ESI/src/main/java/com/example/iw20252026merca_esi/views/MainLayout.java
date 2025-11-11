package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLayout;

@Layout
public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div contentContainer;

    public MainLayout() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        setWidthFull();
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100vh")
                .set("min-height", "0")
                .set("min-width", "0");

        HorizontalLayout header = createHeader();
        header.setWidthFull();

        HorizontalLayout menuNavigation = createNavigationMenu();
        menuNavigation.setWidthFull();

        contentContainer = new Div();
        contentContainer.setWidthFull();
        contentContainer.getStyle()
                .set("box-sizing", "border-box")
                .set("background-color", "#f5f5f5")
                .set("padding", "clamp(8px, 2vw, 24px)")
                .set("overflow", "auto")
                .set("flex", "1 1 auto")
                .set("min-height", "0")
                .set("min-width", "0");

        Footer footer = createFooter();

        add(header, menuNavigation, contentContainer, footer);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contentContainer.getElement().removeAllChildren();
        if (content != null) {
            contentContainer.getElement().appendChild(content.getElement());
        }
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.getStyle()
                .set("background-color", "#e30613")
                .set("color", "white")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div logoDiv = new Div();
        Span logo = new Span("MercaESI");
        logo.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "white")
                .set("cursor", "pointer");
        logo.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        logoDiv.add(logo);

        Div spacer = new Div();
        header.setFlexGrow(1, spacer);

        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setSpacing(true);
        rightSection.setAlignItems(Alignment.CENTER);

        Button cartButton = new Button("CESTA", new Icon(VaadinIcon.CART));
        cartButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cartButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("carrito")));
        cartButton.getStyle()
                .set("color", "white")
                .set("border-radius","50px")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                .set("transition", "background-color 0.3s")
                .set("cursor", "pointer");

        // Efecto similar a los botones del menú: cambio sutil de fondo al pasar el ratón
        cartButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
        cartButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");





        Button loginButton = new Button("ACCESO", new Icon(VaadinIcon.USER));
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        loginButton.getStyle()
                .set("color", "white")
                .set("border-radius","50px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.3)")
                .set("transition", "background-color 0.3s")
                .set("cursor", "pointer");

        // Efecto similar a los botones del menú: cambio sutil de fondo al pasar el ratón
        loginButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
        loginButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");


        rightSection.add(cartButton, loginButton);
        header.add(logoDiv, spacer, rightSection);
        return header;
    }

    private HorizontalLayout createNavigationMenu() {
        HorizontalLayout menuBar = new HorizontalLayout();
        menuBar.setWidthFull();
        menuBar.setPadding(true);
        menuBar.setJustifyContentMode(JustifyContentMode.CENTER);
        menuBar.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "6px")
                .set("background-color", "white")
                .set("border-radius", "50px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.35)")
                .set("margin", "0")
                .set("padding", "8px clamp(10px, 2vw, 20px)");

        String[] menuItems = {"OFERTAS", "MENÚS", "PIZZAS", "BURGERS", "ENTRANTES", "BEBIDAS", "POSTRES"};

        for (String item : menuItems) {
            Button menuButton = new Button(item);
            menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            menuButton.getStyle()
                    .set("color", "#333")
                    .set("font-weight", "500")
                    .set("padding", "10px 15px")
                    .set("border-radius", "25px")
                    .set("transition", "background-color 0.3s")
                    .set("cursor", "pointer");
            menuButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(item.toLowerCase())));
            menuBar.add(menuButton);
        }
        return menuBar;
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.setWidthFull();
        footer.getStyle()
                .set("box-sizing", "border-box")
                .set("background-color", "#333")
                .set("color", "white")
                .set("padding", "clamp(12px, 2vw, 20px)")
                .set("margin", "0")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("text-align", "center");

        Span footerText = new Span("© 2025 MercaESI - Todos los derechos reservados");
        footer.add(footerText);
        return footer;
    }
}