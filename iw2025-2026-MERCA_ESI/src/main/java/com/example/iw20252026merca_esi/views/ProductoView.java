package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.example.iw20252026merca_esi.service.SessionService;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Productos")
@RolesAllowed("ADMINISTRADOR")
@Route(value = "productos", layout = MainLayout.class)
@Menu(title = "Productos")
public class ProductoView extends VerticalLayout implements BeforeEnterObserver {

    private final ProductoService productoService;
    private final SessionService sessionService;
    private final Div grid;
    
    private static final String DISPLAY = "display";
    private static final String PADDING = "padding";
    private static final String PADDING_IS = "2px 8px";
    private static final String COLOR = "color";
    private static final String COLOR_1_IS = "#e30613";
    private static final String COLOR_2_IS = "white";
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String FONTSIZE = "font-size";
    private static final String FONTSIZE_IS = "0.75rem";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String BORDER_RADIUS = "border-radius";
    

    public ProductoView(ProductoService productoService, SessionService sessionService) {
        this.productoService = productoService;
        this.sessionService = sessionService;
        
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
                .set(DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(min(200px, 100%), 1fr))")
                .set("gap", "clamp(10px, 2vw, 15px)")
                .set("width", "100%")
                .set("align-content", "start")
                .set(PADDING, "0 clamp(8px, 2vw, 12px) clamp(16px, 3vw, 30px)");

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
                .set(PADDING, "clamp(8px, 2vw, 12px)")
                .set("flex-wrap", "wrap")
                .set("gap", "10px");

        H1 titulo = new H1("Gestión de Productos");
        titulo.getStyle()
                .set("margin", "0")
                .set(COLOR, COLOR_1_IS);

        HorizontalLayout botones = new HorizontalLayout();
        botones.setSpacing(true);
        botones.getStyle().set("flex-wrap", "wrap");

        Button crearProductoBtn = new Button("Crear Producto", new Icon(VaadinIcon.PLUS));
        crearProductoBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearProductoBtn.getStyle()
                .set(BACKGROUND_COLOR, COLOR_1_IS)
                .set(COLOR, COLOR_2_IS);
        crearProductoBtn.addClickListener(e -> 
            UI.getCurrent().navigate("crear-producto")
        );

        Button crearIngredienteBtn = new Button("Crear Ingrediente", new Icon(VaadinIcon.PLUS_CIRCLE));
        crearIngredienteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearIngredienteBtn.getStyle()
                .set(BACKGROUND_COLOR, "#D32F2F")
                .set(COLOR, COLOR_2_IS);
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
                    .set(COLOR, "#666")
                    .set(PADDING, "40px")
                    .set(FONTSIZE, "1.1rem");
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
                .set(BORDER_RADIUS, "12px")
                .set("box-shadow", "0 4px 14px rgba(0,0,0,0.10)")
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
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
                .set(DISPLAY, "flex")
                .set("flex-direction", "column")
                .set("gap", "8px")
                .set(PADDING, "12px 14px 14px 14px")
                .set("flex", "1 1 auto");

        H3 h3 = new H3(producto.getNombre());
        h3.getStyle()
                .set("margin", "0")
                .set(FONTSIZE, "clamp(0.95rem, 2vw, 1.05rem)")
                .set("line-height", "1.2");

        Span desc = new Span(producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripción");
        desc.getStyle()
                .set(DISPLAY, "block")
                .set(COLOR, "#555")
                .set(FONTSIZE, "clamp(0.85rem, 1.8vw, 0.9rem)")
                .set(FONTSIZE, "clamp(0.9rem, 2vw, 0.95rem)");

        HorizontalLayout priceAndBadges = new HorizontalLayout();
        priceAndBadges.setWidthFull();
        priceAndBadges.setAlignItems(Alignment.CENTER);
        priceAndBadges.setSpacing(true);

        Span priceTag = new Span(String.format("%.2f €", producto.getPrecio()));
        priceTag.getStyle()
                .set(COLOR, COLOR_1_IS)
                .set(FONT_WEIGHT, "700")
                .set(FONTSIZE, "clamp(1rem, 2.2vw, 1.05rem)");

        // Badges
        HorizontalLayout badges = new HorizontalLayout();
        badges.setSpacing(true);
        badges.getStyle().set("margin-left", "auto");

        if (producto.getEsOferta()) {
            Span ofertaBadge = new Span("OFERTA");
            ofertaBadge.getStyle()
                    .set(BACKGROUND_COLOR, "#FF9800")
                    .set(COLOR, COLOR_2_IS)
                    .set(PADDING, PADDING_IS)
                    .set(BORDER_RADIUS, "4px")
                    .set(FONTSIZE, FONTSIZE_IS)
                    .set(FONT_WEIGHT, "bold");
            badges.add(ofertaBadge);
        }

        if (producto.getPuntos()) {
            Span puntosBadge = new Span("PUNTOS");
            puntosBadge.getStyle()
                    .set(BACKGROUND_COLOR, "#4CAF50")
                    .set(COLOR, COLOR_2_IS)
                    .set(PADDING, PADDING_IS)
                    .set(BORDER_RADIUS, "4px")
                    .set(FONTSIZE, FONTSIZE_IS)
                    .set(FONT_WEIGHT, "bold");
            badges.add(puntosBadge);
        }

        if (!producto.getEstado()) {
            Span inactivoBadge = new Span("INACTIVO");
            inactivoBadge.getStyle()
                    .set(BACKGROUND_COLOR, "#9E9E9E")
                    .set(COLOR, COLOR_2_IS)
                    .set(PADDING, PADDING_IS)
                    .set(BORDER_RADIUS, "4px")
                    .set(FONTSIZE, FONTSIZE_IS)
                    .set(FONT_WEIGHT, "bold");
            badges.add(inactivoBadge);
        }

        priceAndBadges.add(priceTag, badges);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);
        actions.getStyle().set("margin-top", "auto");

        Button editBtn = new Button("Editar", new Icon(VaadinIcon.EDIT));
        editBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editBtn.getStyle().set(COLOR, COLOR_1_IS);

        Button deleteBtn = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        actions.add(editBtn, deleteBtn);

        content.add(h3, desc, priceAndBadges, actions);
        card.add(image, content);
        return card;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            event.rerouteTo("");
            Notification.show("Acceso denegado.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
