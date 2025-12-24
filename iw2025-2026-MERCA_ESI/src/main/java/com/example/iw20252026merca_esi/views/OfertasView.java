// java
package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Ofertas")
@AnonymousAllowed
@Route(value = "ofertas", layout = MainLayout.class)
@Menu(title = "ofertas")
public class OfertasView extends VerticalLayout {

    private final Div grid;

    public OfertasView() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("box-sizing", "border-box")
                .set("min-height", "0"); // Evita desbordes dentro del contenedor

        H1 titulo = new H1("Ofertas");
        titulo.getStyle()
                .set("margin", "0 0 clamp(8px, 2vw, 20px) 0")
                .set("color", "#e30613")
                .set("padding", "0 clamp(8px, 2vw, 12px)");

        grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                // Se adapta a cualquier ancho de pantalla sin scroll horizontal
                .set("grid-template-columns", "repeat(auto-fit, minmax(min(280px, 100%), 1fr))")
                .set("gap", "clamp(10px, 2vw, 20px)")
                .set("width", "100%")
                .set("align-content", "start")
                .set("padding", "0 clamp(8px, 2vw, 12px) clamp(16px, 3vw, 30px)");

        add(titulo, grid);
        setFlexGrow(1, grid);

        renderPlaceholderOffers();
    }

    private void addOffer(String titulo, String descripcion, String precio, String imageUrl) {
        grid.add(createOfferCard(titulo, descripcion, precio, imageUrl));
    }

    private Div createOfferCard(String title, String description, String price, String imageUrl) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 14px rgba(0,0,0,0.10)")
                .set("overflow", "hidden")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100%");

        Image image = new Image(imageUrl, title);
        image.setWidth("100%");
        image.setHeight("auto");
        image.getStyle()
                .set("object-fit", "cover")
                .set("aspect-ratio", "16/9"); // Imagen responsive

        Div content = new Div();
        content.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "10px")
                .set("padding", "14px 16px 16px 16px")
                .set("flex", "1 1 auto");

        H3 h3 = new H3(title);
        h3.getStyle()
                .set("margin", "0")
                .set("font-size", "clamp(1rem, 2.2vw, 1.15rem)")
                .set("line-height", "1.2");

        Span desc = new Span(description);
        desc.getStyle()
                .set("display", "block")
                .set("color", "#555")
                .set("font-size", "clamp(0.9rem, 2vw, 0.95rem)");

        Span priceTag = new Span(price);
        priceTag.getStyle()
                .set("color", "#e30613")
                .set("font-weight", "700")
                .set("font-size", "clamp(1rem, 2.2vw, 1.05rem)");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "auto"); // Ancla botones abajo

        Button detailsBtn = new Button("Detalles", new Icon(VaadinIcon.SEARCH));
        detailsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button addBtn = new Button("Añadir", new Icon(VaadinIcon.CART));
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        actions.add(detailsBtn, addBtn);

        content.add(h3, desc, priceTag, actions);
        card.add(image, content);
        return card;
    }

    private void renderPlaceholderOffers() {
        addOffer("2x1 en Pizzas", "Llévate 2 por el precio de 1 en selección clásica.", "9,99 €", "https://picsum.photos/seed/pizza/600/400");
        addOffer("Menú Burger", "Burger + patatas + bebida a un precio especial.", "7,50 €", "https://picsum.photos/seed/burger/600/400");
        addOffer("Entrantes Mix", "Combo de entrantes para compartir.", "5,95 €", "https://picsum.photos/seed/starters/600/400");
        addOffer("Postre del Día", "Dulce sorpresa para cerrar el menú.", "3,20 €", "https://picsum.photos/seed/dessert/600/400");
        addOffer("Bebidas 3x2", "Paga 2 y llévate 3 en bebidas seleccionadas.", "2,40 €", "https://picsum.photos/seed/drink/600/400");
        addOffer("Menú Familiar", "Para 4 personas con ahorro garantizado.", "19,90 €", "https://picsum.photos/seed/family/600/400");
    }
}
