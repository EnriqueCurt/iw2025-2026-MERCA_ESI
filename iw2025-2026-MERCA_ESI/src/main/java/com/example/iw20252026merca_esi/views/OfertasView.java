package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.ProductoService;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Ofertas")
@AnonymousAllowed
@Route(value = "ofertas", layout = MainLayout.class)
@Menu(title = "ofertas")
public class OfertasView extends VerticalLayout {

    private final ProductoService productoService;
    private final Div grid;
    private final Checkbox puntosFilter = new Checkbox("Solo Puntos");
    
    private List<Producto> todosLosProductos = new ArrayList<>();
    private List<Producto> productosFiltrados = new ArrayList<>();
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
    private static final String BORDER_RADIUS = "border-radius";

    public OfertasView(ProductoService productoService) {
        this.productoService = productoService;
        
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
                .set("width", "100%")
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

        H1 titulo = new H1("Nuestras Ofertas");
        titulo.getStyle()
                .set("margin", "0")
                .set(COLOR, COLOR_1_IS)
                .set("text-align", "center");

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
                .set(BORDER_RADIUS, "8px")
                .set("margin", "0 clamp(8px, 2vw, 12px)");
        
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
        todosLosProductos = productoService.listarProductosActivos().stream()
                .filter(Producto::getEsOferta)
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
            emptyState.setText("No hay ofertas disponibles en este momento.");
            emptyState.getStyle()
                    .set("text-align", "center")
                    .set(COLOR, "#666")
                    .set(PADDING, "40px")
                    .set(FONTSIZE, "1.1rem");
            grid.add(emptyState);
            actualizarPaginacion();
        } else {
            int inicio = paginaActual * PRODUCTOS_POR_PAGINA;
            int fin = Math.min(inicio + PRODUCTOS_POR_PAGINA, productosFiltrados.size());
            
            for (int i = inicio; i < fin; i++) {
                grid.add(createProductCard(productosFiltrados.get(i)));
            }
            
            actualizarPaginacion();
        }
    }

    private Div createProductCard(Producto producto) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set(BORDER_RADIUS, "12px")
                .set("box-shadow", "0 8px 24px rgba(255, 152, 0, 0.3)")
                .set("border", "3px solid #FF9800")
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
                .set("flex-direction", "column")
                .set("height", "100%")
                .set("max-width", "350px")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");
        
        // Efecto hover para ofertas
        card.getElement().addEventListener("mouseenter", e -> 
            card.getStyle()
                .set("transform", "scale(1.05) translateY(-5px)")
                .set("box-shadow", "0 12px 32px rgba(255, 152, 0, 0.5)")
        );
        card.getElement().addEventListener("mouseleave", e -> 
            card.getStyle()
                .set("transform", "scale(1)")
                .set("box-shadow", "0 8px 24px rgba(255, 152, 0, 0.3)")
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
                .set("line-height", "1.4")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("display", "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical");

        HorizontalLayout priceAndBadges = new HorizontalLayout();
        priceAndBadges.setWidthFull();
        priceAndBadges.setAlignItems(Alignment.CENTER);
        priceAndBadges.setSpacing(true);

        Span priceTag = new Span(String.format("%.2f €", producto.getPrecio()));
        priceTag.getStyle()
                .set(COLOR, COLOR_1_IS)
                .set(FONT_WEIGHT, "700")
                .set(FONTSIZE, "clamp(1rem, 2.2vw, 1.05rem)");

        HorizontalLayout badges = new HorizontalLayout();
        badges.setSpacing(true);
        badges.getStyle().set("margin-left", "auto");

        Span ofertaBadge = new Span("OFERTA");
        ofertaBadge.getStyle()
                .set(BACKGROUND_COLOR, "#FF9800")
                .set(COLOR, COLOR_2_IS)
                .set(PADDING, PADDING_IS)
                .set(BORDER_RADIUS, "4px")
                .set(FONTSIZE, FONTSIZE_IS)
                .set(FONT_WEIGHT, "bold");
        badges.add(ofertaBadge);

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

        priceAndBadges.add(priceTag, badges);

        Button pedirBtn = new Button("PEDIR");
        pedirBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pedirBtn.setWidthFull();
        pedirBtn.getStyle()
                .set(BACKGROUND_COLOR, COLOR_1_IS)
                .set(COLOR, COLOR_2_IS)
                .set("margin-top", "auto");
        pedirBtn.addClickListener(e -> {
            System.out.println("Añadido al carrito: " + producto.getNombre());
        });

        content.add(h3, desc, priceAndBadges, pedirBtn);
        card.add(image, content);
        return card;
    }
}
