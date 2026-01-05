package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.service.MenuService;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Gestión de Menús")
@RolesAllowed({"ADMINISTRADOR", "MANAGER"})
@Route(value = "menus-gestion", layout = MainLayout.class)
public class MenuView extends VerticalLayout implements BeforeEnterObserver {

    private final MenuService menuService;
    private final SessionService sessionService;
    private final Div grid;
    
    private static final String BORDER_RADIUS = "border-radius";
    private static final String DISPLAY = "display";
    private static final String PADDING = "padding";
    private static final String PADDING1 = "4px 10px";
    private static final String COLOR = "color";
    private static final String COLOR1 = "white";
    private static final String COLOR2 = "#e30613";
    private static final String FONTSIZE = "font-size";
    private static final String FONTSIZE1 = "0.85rem";
    private static final String FONTSIZE2 = "0.75rem";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String BACKGROUNDCOLOR = "background-color";
    private static final String BOXSHADOW = "box-shadow";
    private static final String COLUMN = "column";
    private static final String FLEXDIR = "flex-direction";

    public MenuView(MenuService menuService, SessionService sessionService) {
        this.menuService = menuService;
        this.sessionService = sessionService;
        
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        
        // Header
        HorizontalLayout header = createHeader();
        
        grid = new Div();
        grid.getStyle()
                .set(DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
                .set("gap", "20px")
                .set("width", "100%")
                .set(PADDING, "20px");
        
        add(header, grid);
        cargarMenus();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionService.isLoggedIn()) {
            event.forwardTo("acceso");
            return;
        }
        
        var empleado = sessionService.getEmpleado();
        if (empleado == null || (!empleado.esAdministrador() && !empleado.esManager())) {
            Notification.show("No tienes permisos para acceder a esta página", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo("");
        }
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set(PADDING, "20px")
                .set("background", "#f5f5f5");

        H1 titulo = new H1("Gestión de Menús");
        titulo.getStyle()
                .set("margin", "0")
                .set(COLOR, COLOR2);

        Button crearButton = new Button("Crear Nuevo Menú", new Icon(VaadinIcon.PLUS));
        crearButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        crearButton.getStyle()
                .set(BACKGROUNDCOLOR, COLOR2)
                .set(COLOR, COLOR1);
        crearButton.addClickListener(e -> UI.getCurrent().navigate("crear-menu"));

        header.add(titulo, crearButton);
        return header;
    }

    private void cargarMenus() {
        grid.removeAll();
        List<Menu> menus = menuService.listarMenus();
        
        if (menus.isEmpty()) {
            Div emptyState = new Div();
            emptyState.setText("No hay menús creados. Haz clic en 'Crear Nuevo Menú' para comenzar.");
            emptyState.getStyle()
                    .set("text-align", "center")
                    .set(COLOR, "#666")
                    .set(PADDING, "40px")
                    .set(FONTSIZE, "1.1rem");
            grid.add(emptyState);
        } else {
            menus.forEach(menu -> grid.add(createMenuCard(menu)));
        }
    }

    private Div createMenuCard(Menu menu) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set(BORDER_RADIUS, "12px")
                .set(BOXSHADOW, "0 4px 14px rgba(0,0,0,0.10)")
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
                .set(FLEXDIR, COLUMN)
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");

        // Efecto hover
        card.getElement().addEventListener("mouseenter", e ->
                card.getStyle()
                        .set("transform", "translateY(-8px)")
                        .set(BOXSHADOW, "0 12px 24px rgba(0,0,0,0.20)")
        );
        card.getElement().addEventListener("mouseleave", e ->
                card.getStyle()
                        .set("transform", "translateY(0)")
                        .set(BOXSHADOW, "0 4px 14px rgba(0,0,0,0.10)")
        );

        // Imagen
        Image image;
        if (menu.getImagen() != null && menu.getImagen().length > 0) {
            image = new Image();
            String base64 = java.util.Base64.getEncoder().encodeToString(menu.getImagen());
            image.setSrc("data:image/jpeg;base64," + base64);
            image.setAlt(menu.getNombre());
        } else {
            image = new Image("https://picsum.photos/seed/menu" + menu.getIdMenu() + "/400/250",
                    menu.getNombre());
        }
        image.setWidth("100%");
        image.setHeight("auto");
        image.getStyle()
                .set("object-fit", "cover")
                .set("aspect-ratio", "16/9");

        Div content = new Div();
        content.getStyle()
                .set(DISPLAY, "flex")
                .set(FLEXDIR, COLUMN)
                .set("gap", "12px")
                .set(PADDING, "16px")
                .set("flex", "1");

        H3 nombre = new H3(menu.getNombre());
        nombre.getStyle()
                .set("margin", "0")
                .set(FONTSIZE, "1.3rem")
                .set(FONT_WEIGHT, "700");

        Span descripcion = new Span(menu.getDescripcion() != null ? menu.getDescripcion() : "Sin descripción");
        descripcion.getStyle()
                .set(COLOR, "#555")
                .set(FONTSIZE, "0.9rem")
                .set("line-height", "1.4");

        // Productos incluidos
        Div productosDiv = new Div();
        productosDiv.getStyle()
                .set(DISPLAY, "flex")
                .set(FLEXDIR, COLUMN)
                .set("gap", "4px");
        
        Span productosLabel = new Span("Productos incluidos:");
        productosLabel.getStyle()
                .set(FONT_WEIGHT, "600")
                .set(FONTSIZE, FONTSIZE1)
                .set(COLOR, "#333");
        
        productosDiv.add(productosLabel);
        
        if (menu.getProductos() != null && !menu.getProductos().isEmpty()) {
            menu.getProductos().forEach(producto -> {
                Span productoSpan = new Span("• " + producto.getNombre());
                productoSpan.getStyle()
                        .set(FONTSIZE, FONTSIZE1)
                        .set(COLOR, "#666");
                productosDiv.add(productoSpan);
            });
        } else {
            Span sinProductos = new Span("Sin productos asignados");
            sinProductos.getStyle()
                    .set(FONTSIZE, FONTSIZE1)
                    .set(COLOR, "#999")
                    .set("font-style", "italic");
            productosDiv.add(sinProductos);
        }

        // Badges y precio
        HorizontalLayout bottomSection = new HorizontalLayout();
        bottomSection.setWidthFull();
        bottomSection.setJustifyContentMode(JustifyContentMode.BETWEEN);
        bottomSection.setAlignItems(Alignment.CENTER);

        HorizontalLayout badges = new HorizontalLayout();
        badges.setSpacing(true);

        if (Boolean.TRUE.equals(menu.getEsOferta())) {
            Span ofertaBadge = new Span("OFERTA");
            ofertaBadge.getStyle()
                    .set(BACKGROUNDCOLOR, "#FF9800")
                    .set(COLOR, COLOR1)
                    .set(PADDING, PADDING1)
                    .set(BORDER_RADIUS, "20px")
                    .set(FONTSIZE, FONTSIZE2)
                    .set(FONT_WEIGHT, "bold");
            badges.add(ofertaBadge);
        }

        if (Boolean.TRUE.equals(menu.getPuntos())) {
            Span puntosBadge = new Span("PUNTOS");
            puntosBadge.getStyle()
                    .set(BACKGROUNDCOLOR, "#9C27B0")
                    .set(COLOR, COLOR1)
                    .set(PADDING, PADDING1)
                    .set(BORDER_RADIUS, "20px")
                    .set(FONTSIZE, FONTSIZE2)
                    .set(FONT_WEIGHT, "bold");
            badges.add(puntosBadge);
        }

        if (!Boolean.TRUE.equals(menu.getEstado())) {
            Span inactivoBadge = new Span("INACTIVO");
            inactivoBadge.getStyle()
                    .set(BACKGROUNDCOLOR, "#999")
                    .set(COLOR, COLOR1)
                    .set(PADDING, PADDING1)
                    .set(BORDER_RADIUS, "20px")
                    .set(FONTSIZE, FONTSIZE2)
                    .set(FONT_WEIGHT, "bold");
            badges.add(inactivoBadge);
        }

        String precioTexto;
        if (Boolean.TRUE.equals(menu.getPuntos())) {
            precioTexto = String.format("%d puntos", menu.getPrecio().intValue());
        } else {
            precioTexto = String.format("%.2f €", menu.getPrecio());
        }
        Span precio = new Span(precioTexto);
        precio.getStyle()
                .set(COLOR, COLOR2)
                .set(FONT_WEIGHT, "700")
                .set(FONTSIZE, "1.3rem");

        bottomSection.add(badges, precio);

        // Botones de acción
        HorizontalLayout botonesLayout = new HorizontalLayout();
        botonesLayout.setWidthFull();
        botonesLayout.setSpacing(true);

        Button editarBtn = new Button("Editar", new Icon(VaadinIcon.EDIT));
        editarBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        editarBtn.getStyle().set(COLOR, COLOR2);
        editarBtn.addClickListener(e -> 
            UI.getCurrent().navigate("crear-menu/" + menu.getIdMenu())
        );

        Button eliminarBtn = new Button("Eliminar", new Icon(VaadinIcon.TRASH));
        eliminarBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
        eliminarBtn.addClickListener(e -> eliminarMenu(menu));

        botonesLayout.add(editarBtn, eliminarBtn);

        content.add(nombre, descripcion, productosDiv, bottomSection, botonesLayout);
        card.add(image, content);

        return card;
    }

    private void eliminarMenu(Menu menu) {
        try {
            menuService.eliminarMenu(menu.getIdMenu());
            Notification notification = Notification.show("Menú eliminado correctamente", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            cargarMenus();
        } catch (Exception e) {
            Notification notification = Notification.show("Error al eliminar el menú: " + e.getMessage(), 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
