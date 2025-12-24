package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class SimpleLayout extends VerticalLayout implements RouterLayout {

    private final Div contentContainer;

    private static final String CENTER = "center";
    private static final String COLOR = "color";
    private static final String COLOR_IS = "white";
    private static final String BACKGROUND_COLOR = "background-color";

    public SimpleLayout() {
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

        contentContainer = new Div();
        contentContainer.setWidthFull();
        contentContainer.getStyle()
                .set("box-sizing", "border-box")
                .set(BACKGROUND_COLOR, "#f5f5f5")
                .set("padding", "clamp(8px, 2vw, 24px)")
                .set("overflow", "auto")
                .set("flex", "1 1 auto")
                .set("min-height", "0")
                .set("min-width", "0");

        Footer footer = createFooter();

        add(header, contentContainer, footer);
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
                .set(BACKGROUND_COLOR, "#e30613")
                .set(COLOR, COLOR_IS)
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div logoDiv = new Div();
        Span logo = new Span("MercaESI");
        logo.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set(COLOR, COLOR_IS)
                .set("cursor", "pointer");
        logo.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
        logo.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");

        logo.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        logoDiv.add(logo);

        header.add(logoDiv);
        return header;
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.setWidthFull();
        footer.getStyle()
                .set("box-sizing", "border-box")
                .set(BACKGROUND_COLOR, "#333")
                .set(COLOR, COLOR_IS)
                .set("padding", "clamp(12px, 2vw, 20px)")
                .set("margin", "0")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", CENTER)
                .set("align-items", CENTER)
                .set("gap", "8px")
                .set("text-align", CENTER);

        Span footerText = new Span("© 2025 MercaESI - Todos los derechos reservados");
        footer.add(footerText);
        return footer;
    }
}
