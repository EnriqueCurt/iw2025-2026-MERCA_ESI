package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.components.SeleccionIngredientesDialog;
import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.MenuService;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.example.iw20252026merca_esi.service.PedidoActualService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Ofertas")
@AnonymousAllowed
@Route(value = "ofertas", layout = MainLayout.class)
@com.vaadin.flow.router.Menu(title = "ofertas")
public class OfertasView extends VerticalLayout {

    private final ProductoService productoService;
    private final MenuService menuService;
    private final PedidoActualService pedidoActualService;
    private final Div grid;
    private final Checkbox puntosFilter = new Checkbox("Solo Puntos");
    
    private List<Producto> todosLosProductos = new ArrayList<>();
    private List<Producto> productosFiltrados = new ArrayList<>();
    private List<com.example.iw20252026merca_esi.model.Menu> menusOferta = new ArrayList<>();
    private int paginaActual = 0;
    private static final int PRODUCTOS_POR_PAGINA = 12;
    private HorizontalLayout paginacion;
    
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
    private static final String TEXT_ALIGN = "text-align";
    private static final String COLUMN = "column";
    private static final String FLEX_DIRECTION = "flex-direction";
    private static final String COLOR9 = "#9C27B0";
    private static final String COLOR8 = "#FF9800";
    private static final String BOX_SHADOW = "box-shadow";
    private static final String MARGIN = "margin";
    private  static final String WIDTH = "width";


    public OfertasView(ProductoService productoService, MenuService menuService, PedidoActualService pedidoActualService) {
        this.productoService = productoService;
        this.menuService = menuService;
        this.pedidoActualService = pedidoActualService;
        
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("box-sizing", "border-box")
                .set("min-height", "0");

        HorizontalLayout header = createHeader();
        HorizontalLayout filtros = createFiltros();

        grid = new Div();
        grid.getStyle()
                .set(DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(220px, 1fr))")
                .set("gap", "clamp(10px, 2vw, 15px)")
                .set(WIDTH, "100%")
                .set("align-content", "start")
                .set("max-width", "100%")
                .set(PADDING, "0 clamp(8px, 2vw, 12px) clamp(16px, 3vw, 30px)");
        
        paginacion = createPaginacion();

        add(header, filtros, grid, paginacion);
        setFlexGrow(1, grid);

        cargarProductos();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.CENTER);
        header.getStyle()
                .set(PADDING, "clamp(16px, 3vw, 24px)")
                .set("flex-wrap", "wrap");

        H1 titulo = new H1("Ofertas y Puntos");
        titulo.getStyle()
                .set(MARGIN, "0")
                .set(COLOR, COLOR_1_IS)
                .set(TEXT_ALIGN, "center");

        header.add(titulo);
        return header;
    }

    private HorizontalLayout createFiltros() {
        HorizontalLayout filtrosLayout = new HorizontalLayout();
        filtrosLayout.setWidthFull();
        filtrosLayout.setAlignItems(Alignment.CENTER);
        filtrosLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        filtrosLayout.setSpacing(true);
        filtrosLayout.getStyle()
                .set(PADDING, "0 clamp(8px, 2vw, 12px)")
                .set("flex-wrap", "wrap")
                .set("gap", "15px")
                .set(BACKGROUND_COLOR, "#f5f5f5")
                .set(TEXT_ALIGN, "8px")
                .set(MARGIN, "0 clamp(8px, 2vw, 12px)");
        
        puntosFilter.getStyle()
                .set("--vaadin-checkbox-checkmark-color", COLOR_2_IS)
                .set("--lumo-primary-color", COLOR_1_IS);
        puntosFilter.addValueChangeListener(e -> aplicarFiltros());
        
        Button limpiarFiltrosBtn = new Button("Limpiar Filtros", new Icon(VaadinIcon.CLOSE_CIRCLE));
        limpiarFiltrosBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        limpiarFiltrosBtn.getStyle().set(COLOR, COLOR_1_IS);
        limpiarFiltrosBtn.addClickListener(e -> {
            puntosFilter.setValue(false);
            aplicarFiltros();
        });
        
        filtrosLayout.add(puntosFilter, limpiarFiltrosBtn);
        return filtrosLayout;
    }

    private HorizontalLayout createPaginacion() {
        HorizontalLayout paginacionLayout = new HorizontalLayout();
        paginacionLayout.setWidthFull();
        paginacionLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        paginacionLayout.setAlignItems(Alignment.CENTER);
        paginacionLayout.setSpacing(true);
        paginacionLayout.getStyle()
                .set(PADDING, "clamp(8px, 2vw, 12px)")
                .set("gap", "10px");
        
        return paginacionLayout;
    }

    private void actualizarPaginacion() {
        paginacion.removeAll();
        
        int totalPaginas = (int) Math.ceil((double) productosFiltrados.size() / PRODUCTOS_POR_PAGINA);
        
        if (totalPaginas <= 1) {
            return;
        }
        
        Button btnPrimera = new Button("Primera", new Icon(VaadinIcon.ANGLE_DOUBLE_LEFT));
        btnPrimera.setEnabled(paginaActual > 0);
        btnPrimera.addClickListener(e -> {
            paginaActual = 0;
            mostrarPagina();
        });
        
        Button btnAnterior = new Button("Anterior", new Icon(VaadinIcon.ANGLE_LEFT));
        btnAnterior.setEnabled(paginaActual > 0);
        btnAnterior.addClickListener(e -> {
            paginaActual--;
            mostrarPagina();
        });
        
        Span infoPagina = new Span("Página " + (paginaActual + 1) + " de " + totalPaginas);
        infoPagina.getStyle()
                .set(FONT_WEIGHT, "600")
                .set(COLOR, COLOR_1_IS);
        
        Button btnSiguiente = new Button("Siguiente", new Icon(VaadinIcon.ANGLE_RIGHT));
        btnSiguiente.setIconAfterText(true);
        btnSiguiente.setEnabled(paginaActual < totalPaginas - 1);
        btnSiguiente.addClickListener(e -> {
            paginaActual++;
            mostrarPagina();
        });
        
        Button btnUltima = new Button("Última", new Icon(VaadinIcon.ANGLE_DOUBLE_RIGHT));
        btnUltima.setIconAfterText(true);
        btnUltima.setEnabled(paginaActual < totalPaginas - 1);
        btnUltima.addClickListener(e -> {
            paginaActual = totalPaginas - 1;
            mostrarPagina();
        });
        
        paginacion.add(btnPrimera, btnAnterior, infoPagina, btnSiguiente, btnUltima);
    }

    private void cargarProductos() {
        todosLosProductos = productoService.listarProductosConCategoriasEIngredientes().stream()
                .filter(p -> p.getEsOferta() || p.getPuntos())
                .collect(Collectors.toList());
        paginaActual = 0;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        productosFiltrados = new ArrayList<>(todosLosProductos);
        
        if (puntosFilter.getValue()) {
            productosFiltrados = productosFiltrados.stream()
                    .filter(Producto::getPuntos)
                    .collect(Collectors.toList());
        }
        
        paginaActual = 0;
        mostrarPagina();
    }

    private void mostrarPagina() {
        grid.removeAll();
        
        if (productosFiltrados.isEmpty()) {
            Div emptyState = new Div();
            emptyState.setText("No hay ofertas ni productos por puntos disponibles.");
            emptyState.getStyle()
                    .set(TEXT_ALIGN, "center")
                    .set(COLOR, "#666")
                    .set(PADDING, "40px")
                    .set(FONTSIZE, "1.1rem");
            grid.add(emptyState);
            actualizarPaginacion();
        } else {
            // Cambiar el grid a layout vertical para mostrar secciones
            grid.getStyle()
                    .set(DISPLAY, "flex")
                    .set(FLEX_DIRECTION, COLUMN)
                    .set("gap", "30px");
            
            // Separar ofertas de productos por puntos
            List<Producto> ofertas = productosFiltrados.stream()
                    .filter(Producto::getEsOferta)
                    .collect(Collectors.toList());
            List<Producto> productosPuntos = productosFiltrados.stream()
                    .filter(Producto::getPuntos)
                    .collect(Collectors.toList());
            
            // Sección de Productos por Puntos PRIMERO
            if (!productosPuntos.isEmpty()) {
                Div seccionPuntos = crearSeccion("⭐ PRODUCTOS POR PUNTOS", productosPuntos, false);
                grid.add(seccionPuntos);
            }
            
            // Cargar y mostrar menús ofertas
            menusOferta = menuService.listarMenusOferta();
            if (!menusOferta.isEmpty()) {
                Div seccionMenus = crearSeccionMenus("🍽️ MENÚS EN OFERTA", menusOferta);
                grid.add(seccionMenus);
            }

            // Sección de Ofertas DESPUÉS
            if (!ofertas.isEmpty()) {
                Div seccionOfertas = crearSeccion("🔥 OFERTAS ESPECIALES", ofertas, true);
                grid.add(seccionOfertas);
            }
            
            actualizarPaginacion();
        }
    }
    
    private Div crearSeccion(String titulo, List<Producto> productos, boolean esOferta) {
        Div seccion = new Div();
        seccion.getStyle()
                .set(WIDTH, "100%")
                .set("margin-bottom", "40px");
        
        // Título de la sección
        com.vaadin.flow.component.html.H2 tituloSeccion = new com.vaadin.flow.component.html.H2(titulo);
        tituloSeccion.getStyle()
                .set(MARGIN, "0 0 20px 0")
                .set(FONTSIZE, "2rem")
                .set(FONT_WEIGHT, "700")
                .set(COLOR, esOferta ? COLOR8 : COLOR9)
                .set(TEXT_ALIGN, "left")
                .set("border-bottom", esOferta ? "3px solid #FF9800" : "3px solid #9C27B0")
                .set("padding-bottom", "10px");
        
        // Grid de productos
        Div gridProductos = new Div();
        gridProductos.getStyle()
                .set(DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(220px, 1fr))")
                .set("gap", "clamp(10px, 2vw, 15px)")
                .set(WIDTH, "100%");
        
        for (Producto producto : productos) {
            gridProductos.add(createProductCard(producto, esOferta));
        }
        
        seccion.add(tituloSeccion, gridProductos);
        return seccion;
    }

    private Div createProductCard(Producto producto, boolean esOferta) {
        String colorBorde = esOferta ? COLOR8 : COLOR9;
        String colorSombra = esOferta ? "rgba(255, 152, 0, 0.3)" : "rgba(156, 39, 176, 0.3)";
        String colorSombraHover = esOferta ? "rgba(255, 152, 0, 0.5)" : "rgba(156, 39, 176, 0.5)";
        
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set(TEXT_ALIGN, "12px")
                .set(BOX_SHADOW, "0 8px 24px " + colorSombra)
                .set("border", "3px solid " + colorBorde)
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
                .set(FLEX_DIRECTION, COLUMN)
                .set("height", "100%")
                .set("max-width", "350px")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");
        
        // Efecto hover diferenciado
        card.getElement().addEventListener("mouseenter", e -> 
            card.getStyle()
                .set("transform", "scale(1.05) translateY(-5px)")
                .set(BOX_SHADOW, "0 12px 32px " + colorSombraHover)
        );
        card.getElement().addEventListener("mouseleave", e -> 
            card.getStyle()
                .set("transform", "scale(1)")
                .set(BOX_SHADOW, "0 8px 24px " + colorSombra)
        );

        Image image;
        if (producto.getImagen() != null && producto.getImagen().length > 0) {
            image = new Image();
            String base64 = java.util.Base64.getEncoder().encodeToString(producto.getImagen());
            image.setSrc("data:image/jpeg;base64," + base64);
            image.setAlt(producto.getNombre());
        } else {
            image = new Image("https://picsum.photos/seed/" + producto.getIdProducto() + "/400/250", 
                            producto.getNombre());
        }
        image.setWidth("100%");
        image.setHeight("auto");
        image.getStyle()
                .set("object-fit", "cover")
                .set("aspect-ratio", "16/9");

        Div content = new Div();
        content.getStyle()
                .set(DISPLAY, "flex")
                .set(FLEX_DIRECTION, COLUMN)
                .set("gap", "8px")
                .set(PADDING, "12px 14px 14px 14px")
                .set("flex", "1 1 auto");

        H3 h3 = new H3(producto.getNombre());
        h3.getStyle()
                .set(MARGIN, "0")
                .set(FONTSIZE, "clamp(0.95rem, 2vw, 1.05rem)")
                .set("line-height", "1.2");

        Span desc = new Span(producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripción");
        desc.getStyle()
                .set(DISPLAY, "block")
                .set(COLOR, "#555")
                .set(FONTSIZE, "clamp(0.85rem, 1.8vw, 0.9rem)")
                .set("line-height", "1.4")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set(DISPLAY, "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical");

        HorizontalLayout priceAndBadges = new HorizontalLayout();
        priceAndBadges.setWidthFull();
        priceAndBadges.setAlignItems(Alignment.CENTER);
        priceAndBadges.setSpacing(true);

        String precioTexto;
        if (producto.getPuntos()) {
            precioTexto = String.format("%d puntos", producto.getPrecio().intValue());
        } else {
            precioTexto = String.format("%.2f €", producto.getPrecio());
        }
        Span priceTag = new Span(precioTexto);
        priceTag.getStyle()
                .set(COLOR, COLOR_1_IS)
                .set(FONT_WEIGHT, "700")
                .set(FONTSIZE, "clamp(1rem, 2.2vw, 1.05rem)");

        HorizontalLayout badges = new HorizontalLayout();
        badges.setSpacing(true);
        badges.getStyle().set("margin-left", "auto");

        if (esOferta && producto.getEsOferta()) {
            Span ofertaBadge = new Span("OFERTA");
            ofertaBadge.getStyle()
                    .set(BACKGROUND_COLOR, COLOR8)
                    .set(COLOR, COLOR_2_IS)
                    .set(PADDING, PADDING_IS)
                    .set(TEXT_ALIGN, "4px")
                    .set(FONTSIZE, FONTSIZE_IS)
                    .set(FONT_WEIGHT, "bold");
            badges.add(ofertaBadge);
        }

        if (!esOferta && producto.getPuntos()) {
            Span puntosBadge = new Span("PUNTOS");
            puntosBadge.getStyle()
                    .set(BACKGROUND_COLOR, COLOR9)
                    .set(COLOR, COLOR_2_IS)
                    .set(PADDING, PADDING_IS)
                    .set(TEXT_ALIGN, "4px")
                    .set(FONTSIZE, FONTSIZE_IS)
                    .set(FONT_WEIGHT, "bold");
            badges.add(puntosBadge);
        }

        priceAndBadges.add(priceTag, badges);

        Button pedirBtn = new Button("PEDIR");
        pedirBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pedirBtn.setWidthFull();
        pedirBtn.getStyle()
                .set(BACKGROUND_COLOR, COLOR_1_IS)
                .set(COLOR, COLOR_2_IS)
                .set("margin-top", "auto");
        pedirBtn.addClickListener(e -> {
            SeleccionIngredientesDialog dialog = new SeleccionIngredientesDialog(
                producto,
                itemPedido -> {
                    // Agregar el item completo con sus exclusiones
                    pedidoActualService.agregarItem(itemPedido);
                    
                    String mensaje = "✓ " + producto.getNombre() + " añadido al pedido";
                    if (itemPedido.tieneExclusiones()) {
                        mensaje += " (personalizado)";
                    }
                    
                    Notification notification = Notification.show(
                        mensaje,
                        2000,
                        Notification.Position.BOTTOM_END
                    );
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }
            );
            dialog.open();
        });

        content.add(h3, desc, priceAndBadges, pedirBtn);
        card.add(image, content);
        return card;
    }

    private Div crearSeccionMenus(String titulo, List<com.example.iw20252026merca_esi.model.Menu> menus) {
        Div seccion = new Div();
        seccion.getStyle()
                .set(WIDTH, "100%")
                .set("margin-bottom", "40px");

        // Título de la sección
        com.vaadin.flow.component.html.H2 tituloSeccion = new com.vaadin.flow.component.html.H2(titulo);
        tituloSeccion.getStyle()
                .set(MARGIN, "0 0 20px 0")
                .set(FONTSIZE, "2rem")
                .set(FONT_WEIGHT, "700")
                .set(COLOR, COLOR8)
                .set(TEXT_ALIGN, "left")
                .set("border-bottom", "3px solid #FF9800")
                .set("padding-bottom", "10px");

        // Grid de menús
        Div gridMenus = new Div();
        gridMenus.getStyle()
                .set(DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(220px, 1fr))")
                .set("gap", "clamp(10px, 2vw, 15px)")
                .set(WIDTH, "100%");

        for (com.example.iw20252026merca_esi.model.Menu menu : menus) {
            gridMenus.add(createMenuCard(menu));
        }

        seccion.add(tituloSeccion, gridMenus);
        return seccion;
    }

    private Div createMenuCard(com.example.iw20252026merca_esi.model.Menu menu) {
        String colorBorde = COLOR8;
        String colorSombra = "rgba(255, 152, 0, 0.3)";
        String colorSombraHover = "rgba(255, 152, 0, 0.5)";

        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set(TEXT_ALIGN, "12px")
                .set(BOX_SHADOW, "0 8px 24px " + colorSombra)
                .set("border", "3px solid " + colorBorde)
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
                .set(FLEX_DIRECTION, COLUMN)
                .set("height", "100%")
                .set("max-width", "350px")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");

        card.getElement().addEventListener("mouseenter", e ->
            card.getStyle()
                .set("transform", "scale(1.05) translateY(-5px)")
                .set(BOX_SHADOW, "0 12px 32px " + colorSombraHover)
        );
        card.getElement().addEventListener("mouseleave", e ->
            card.getStyle()
                .set("transform", "scale(1)")
                .set(BOX_SHADOW, "0 8px 24px " + colorSombra)
        );

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
                .set(FLEX_DIRECTION, COLUMN)
                .set("gap", "8px")
                .set(PADDING, "12px 14px 14px 14px")
                .set("flex", "1 1 auto");

        H3 h3 = new H3(menu.getNombre());
        h3.getStyle()
                .set(MARGIN, "0")
                .set(FONTSIZE, "clamp(0.95rem, 2vw, 1.05rem)")
                .set("line-height", "1.2");

        Span desc = new Span(menu.getDescripcion() != null ? menu.getDescripcion() : "Sin descripción");
        desc.getStyle()
                .set(DISPLAY, "block")
                .set(COLOR, "#555")
                .set(FONTSIZE, "clamp(0.85rem, 1.8vw, 0.9rem)")
                .set("line-height", "1.4")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set(DISPLAY, "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical");

        Span priceTag = new Span(String.format("%.2f €", menu.getPrecio()));
        priceTag.getStyle()
                .set(COLOR, COLOR_1_IS)
                .set(FONT_WEIGHT, "700")
                .set(FONTSIZE, "clamp(1rem, 2.2vw, 1.05rem)");

        Span ofertaBadge = new Span("OFERTA");
        ofertaBadge.getStyle()
                .set(BACKGROUND_COLOR, COLOR8)
                .set(COLOR, COLOR_2_IS)
                .set(PADDING, PADDING_IS)
                .set(TEXT_ALIGN, "4px")
                .set(FONTSIZE, FONTSIZE_IS)
                .set(FONT_WEIGHT, "bold")
                .set("margin-top", "8px");

        Button pedirBtn = new Button("PEDIR MENÚ");
        pedirBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pedirBtn.setWidthFull();
        pedirBtn.getStyle()
                .set(BACKGROUND_COLOR, COLOR_1_IS)
                .set(COLOR, COLOR_2_IS)
                .set("margin-top", "auto");
        pedirBtn.addClickListener(e -> {
            // Agregar productos del menú al pedido
            for (Producto producto : menu.getProductos()) {
                pedidoActualService.agregarProducto(producto);
            }

            Notification notification = Notification.show(
                "✓ Menú " + menu.getNombre() + " añadido al pedido",
                2000,
                Notification.Position.BOTTOM_END
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        content.add(h3, desc, priceTag, ofertaBadge, pedirBtn);
        card.add(image, content);
        return card;
    }
}
