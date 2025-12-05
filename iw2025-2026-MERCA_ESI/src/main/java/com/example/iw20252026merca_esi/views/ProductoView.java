package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.component.UI;
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

import java.util.List;

@PageTitle("Productos")
@AnonymousAllowed
@Route(value = "productos", layout = MainLayout.class)
@Menu(title = "Productos")
public class ProductoView extends VerticalLayout {

    private final ProductoService productoService;
    private final Div grid;

    public ProductoView(ProductoService productoService) {
        this.productoService = productoService;
        
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("box-sizing", "border-box")
                .set("min-height", "0");

        // Header con título y botones de administración
        HorizontalLayout header = createHeader();

        grid = new Div();
        grid.getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(min(200px, 100%), 1fr))")
                .set("gap", "clamp(10px, 2vw, 15px)")
                .set("width", "100%")
                .set("align-content", "start")
                .set("padding", "0 clamp(8px, 2vw, 12px) clamp(16px, 3vw, 30px)");

        add(header, grid);
        setFlexGrow(1, grid);

        cargarProductos();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding", "clamp(8px, 2vw, 12px)")
                .set("flex-wrap", "wrap")
                .set("gap", "10px");

        H1 titulo = new H1("Gestión de Productos");
        titulo.getStyle()
                .set("margin", "0")
                .set("color", "#e30613");

        HorizontalLayout botones = new HorizontalLayout();
        botones.setSpacing(true);
        botones.getStyle().set("flex-wrap", "wrap");

        Button crearProductoBtn = new Button("Crear Producto", new Icon(VaadinIcon.PLUS));
        crearProductoBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearProductoBtn.getStyle()
                .set("background-color", "#e30613")
                .set("color", "white");
        crearProductoBtn.addClickListener(e -> 
            UI.getCurrent().navigate("crear-producto")
        );

        Button crearIngredienteBtn = new Button("Crear Ingrediente", new Icon(VaadinIcon.PLUS_CIRCLE));
        crearIngredienteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearIngredienteBtn.getStyle()
                .set("background-color", "#D32F2F")
                .set("color", "white");
        crearIngredienteBtn.addClickListener(e -> 
            UI.getCurrent().navigate("crear-ingrediente")
        );

        botones.add(crearProductoBtn, crearIngredienteBtn);

        header.add(titulo, botones);
        return header;
    }

    private void cargarProductos() {
        grid.removeAll();
        List<Producto> productos = productoService.listarProductos();
        
        if (productos.isEmpty()) {
            Div emptyState = new Div();
            emptyState.setText("No hay productos disponibles. Crea uno nuevo usando el botón 'Crear Producto'.");
            emptyState.getStyle()
                    .set("text-align", "center")
                    .set("color", "#666")
                    .set("padding", "40px")
                    .set("font-size", "1.1rem");
            grid.add(emptyState);
        } else {
            for (Producto producto : productos) {
                grid.add(createProductCard(producto));
            }
        }
    }

    private Div createProductCard(Producto producto) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 14px rgba(0,0,0,0.10)")
                .set("overflow", "hidden")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100%");

        // Imagen placeholder (puedes agregar campo imagen en el modelo Producto más adelante)
        Image image = new Image("https://picsum.photos/seed/" + producto.getIdProducto() + "/400/250", 
                                producto.getNombre());
        image.setWidth("100%");
        image.setHeight("auto");
        image.getStyle()
                .set("object-fit", "cover")
                .set("aspect-ratio", "16/9");

        Div content = new Div();
        content.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "8px")
                .set("padding", "12px 14px 14px 14px")
                .set("flex", "1 1 auto");

        H3 h3 = new H3(producto.getNombre());
        h3.getStyle()
                .set("margin", "0")
                .set("font-size", "clamp(0.95rem, 2vw, 1.05rem)")
                .set("line-height", "1.2");

        Span desc = new Span(producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripción");
        desc.getStyle()
                .set("display", "block")
                .set("color", "#555")
                .set("font-size", "clamp(0.85rem, 1.8vw, 0.9rem)")
                .set("font-size", "clamp(0.9rem, 2vw, 0.95rem)");

        HorizontalLayout priceAndBadges = new HorizontalLayout();
        priceAndBadges.setWidthFull();
        priceAndBadges.setAlignItems(Alignment.CENTER);
        priceAndBadges.setSpacing(true);

        Span priceTag = new Span(String.format("%.2f €", producto.getPrecio()));
        priceTag.getStyle()
                .set("color", "#e30613")
                .set("font-weight", "700")
                .set("font-size", "clamp(1rem, 2.2vw, 1.05rem)");

        // Badges
        HorizontalLayout badges = new HorizontalLayout();
        badges.setSpacing(true);
        badges.getStyle().set("margin-left", "auto");

        if (producto.getEsOferta()) {
            Span ofertaBadge = new Span("OFERTA");
            ofertaBadge.getStyle()
                    .set("background-color", "#FF9800")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px")
                    .set("font-size", "0.75rem")
                    .set("font-weight", "bold");
            badges.add(ofertaBadge);
        }

        if (producto.getPuntos()) {
            Span puntosBadge = new Span("PUNTOS");
            puntosBadge.getStyle()
                    .set("background-color", "#4CAF50")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px")
                    .set("font-size", "0.75rem")
                    .set("font-weight", "bold");
            badges.add(puntosBadge);
        }

        if (!producto.getEstado()) {
            Span inactivoBadge = new Span("INACTIVO");
            inactivoBadge.getStyle()
                    .set("background-color", "#9E9E9E")
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px")
                    .set("font-size", "0.75rem")
                    .set("font-weight", "bold");
            badges.add(inactivoBadge);
        }

        priceAndBadges.add(priceTag, badges);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "auto");

        Button editBtn = new Button("Editar", new Icon(VaadinIcon.EDIT));
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editBtn.getStyle().set("color", "#e30613");

        Button deleteBtn = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        actions.add(editBtn, deleteBtn);

        content.add(h3, desc, priceAndBadges, actions);
        card.add(image, content);
        return card;
    }
}
