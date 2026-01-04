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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CategoriaProductosView extends VerticalLayout {

    protected final ProductoService productoService;
    protected final Div grid;
    protected final Checkbox ofertaFilter = new Checkbox("Solo Ofertas");
    protected final Checkbox puntosFilter = new Checkbox("Solo Puntos");
    
    protected List<Producto> todosLosProductos = new ArrayList<>();
    protected List<Producto> productosFiltrados = new ArrayList<>();
    protected int paginaActual = 0;
    protected static final int PRODUCTOS_POR_PAGINA = 12;
    protected HorizontalLayout paginacion;
    
    private static final String DISPLAY = "display";
    private static final String PADDING = "padding";
    private static final String PADDING_IS = "2px 8px";
    private static final String COLOR = "color";
    private static final String COLOR2 = "#e30613";
    private static final String COLOR1 = "white";
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String FONTSIZE = "font-size";
    private static final String FONTSIZE_IS = "0.75rem";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String BORDER_RADIUS = "border-radius";
    private static final String MARGIN = "margin";
    private static final String CENTER = "center";
    private static final String TEXTALIGN = "text-align";
    private static final String GRIDCOLUMN = "grid-column";
    private static final String GRIDCOLUMN1 = "1 / -1";
    private static final String BOXSHADOW = "box-shadow";
    private static final String TRANSFORM = "transform";

    protected CategoriaProductosView(ProductoService productoService, String titulo, String nombreCategoria) {
        this.productoService = productoService;
        
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("box-sizing", "border-box")
                .set("min-height", "0");

        // Header con título
        HorizontalLayout header = createHeader(titulo);
        
        // Filtros
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

        cargarProductos(nombreCategoria);
    }

    private HorizontalLayout createHeader(String titulo) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.CENTER);
        header.getStyle()
                .set(PADDING, "clamp(16px, 3vw, 24px)")
                .set("flex-wrap", "wrap");

        H1 tituloH1 = new H1(titulo);
        tituloH1.getStyle()
                .set(MARGIN, "0")
                .set(COLOR, COLOR2)
                .set(TEXTALIGN, CENTER);

        header.add(tituloH1);
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
                .set(MARGIN, "0 clamp(8px, 2vw, 12px)");
        
        // Configurar Checkbox de ofertas
        ofertaFilter.getStyle()
                .set("--vaadin-checkbox-checkmark-color", COLOR1)
                .set("--lumo-primary-color", COLOR2);
        ofertaFilter.addValueChangeListener(e -> aplicarFiltros());
        
        // Configurar Checkbox de puntos
        puntosFilter.getStyle()
                .set("--vaadin-checkbox-checkmark-color", COLOR1)
                .set("--lumo-primary-color", COLOR2);
        puntosFilter.addValueChangeListener(e -> aplicarFiltros());
        
        // Botón para limpiar filtros
        Button limpiarFiltrosBtn = new Button("Limpiar Filtros", new Icon(VaadinIcon.CLOSE_CIRCLE));
        limpiarFiltrosBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        limpiarFiltrosBtn.getStyle().set(COLOR, COLOR2);
        limpiarFiltrosBtn.addClickListener(e -> {
            ofertaFilter.setValue(false);
            puntosFilter.setValue(false);
            aplicarFiltros();
        });
        
        filtrosLayout.add(ofertaFilter, puntosFilter, limpiarFiltrosBtn);
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
            return; // No mostrar paginación si solo hay una página
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
                .set(COLOR, COLOR2);
        
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

    private void cargarProductos(String nombreCategoria) {
        // Cargar solo productos activos de la categoría
        todosLosProductos = productoService.findByCategoriaNombre(nombreCategoria);
        paginaActual = 0;
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        productosFiltrados = new ArrayList<>(todosLosProductos);
        
        // Aplicar filtro de ofertas
        if (ofertaFilter.getValue()) {
            productosFiltrados = productosFiltrados.stream()
                    .filter(Producto::getEsOferta)
                    .collect(Collectors.toList());
        }
        
        // Aplicar filtro de puntos
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
            emptyState.setText("No hay productos disponibles en esta categoría.");
            emptyState.getStyle()
                    .set(TEXTALIGN, CENTER)
                    .set(COLOR, "#666")
                    .set(PADDING, "40px")
                    .set(FONTSIZE, "1.1rem");
            grid.add(emptyState);
            actualizarPaginacion();
        } else {
            // Separar ofertas de productos normales
            List<Producto> ofertas = productosFiltrados.stream()
                    .filter(Producto::getEsOferta)
                    .collect(Collectors.toList());
            List<Producto> productosNormales = productosFiltrados.stream()
                    .filter(p -> !p.getEsOferta())
                    .collect(Collectors.toList());
            
            // Si hay ofertas, mostrar sección destacada
            if (!ofertas.isEmpty()) {
                // Título de sección de ofertas
                Div seccionOfertas = new Div();
                seccionOfertas.getStyle()
                        .set(GRIDCOLUMN, GRIDCOLUMN1)
                        .set("margin-bottom", "10px");
                
                com.vaadin.flow.component.html.H2 tituloOfertas = new com.vaadin.flow.component.html.H2("¡OFERTAS ESPECIALES!");
                tituloOfertas.getStyle()
                        .set(MARGIN, "20px 0 10px 0")
                        .set(COLOR, "#FF9800")
                        .set(FONT_WEIGHT, "800")
                        .set(TEXTALIGN, CENTER)
                        .set(FONTSIZE, "1.5rem")
                        .set("text-transform", "uppercase");
                
                seccionOfertas.add(tituloOfertas);
                grid.add(seccionOfertas);
                
                // Mostrar ofertas
                for (Producto oferta : ofertas) {
                    grid.add(createProductCard(oferta, true));
                }
                
                // Separador si también hay productos normales
                if (!productosNormales.isEmpty()) {
                    Div separador = new Div();
                    separador.getStyle()
                            .set(GRIDCOLUMN, GRIDCOLUMN1)
                            .set("height", "2px")
                            .set(BACKGROUND_COLOR, "#e0e0e0")
                            .set(MARGIN, "20px 0");
                    grid.add(separador);
                    
                    // Título de productos normales
                    Div seccionNormales = new Div();
                    seccionNormales.getStyle()
                            .set(GRIDCOLUMN, GRIDCOLUMN1)
                            .set("margin-bottom", "10px");
                    
                    com.vaadin.flow.component.html.H2 tituloNormales = new com.vaadin.flow.component.html.H2("Otros Productos");
                    tituloNormales.getStyle()
                            .set(MARGIN, "10px 0")
                            .set(COLOR, "#666")
                            .set(FONT_WEIGHT, "600")
                            .set(TEXTALIGN, CENTER)
                            .set(FONTSIZE, "1.2rem");
                    
                    seccionNormales.add(tituloNormales);
                    grid.add(seccionNormales);
                }
            }
            
            // Mostrar productos normales (aplicando paginación)
            int inicio = paginaActual * PRODUCTOS_POR_PAGINA;
            int totalProductosMostrados = ofertas.size();
            
            // Ajustar inicio considerando las ofertas ya mostradas
            int inicioNormales = Math.max(0, inicio - totalProductosMostrados);
            int finNormales = Math.min(inicioNormales + (PRODUCTOS_POR_PAGINA - Math.min(ofertas.size(), PRODUCTOS_POR_PAGINA)), productosNormales.size());
            
            for (int i = inicioNormales; i < finNormales; i++) {
                grid.add(createProductCard(productosNormales.get(i), false));
            }
            
            actualizarPaginacion();
        }
    }
    
    private Div createProductCard(Producto producto) {
        return createProductCard(producto, false);
    }

    private Div createProductCard(Producto producto, boolean destacada) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#ffffff")
                .set(BORDER_RADIUS, "12px")
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
                .set("flex-direction", "column")
                .set("height", "100%")
                .set("max-width", "350px")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");
        
        // Si es oferta destacada, añadir borde dorado y sombra más pronunciada
        if (destacada) {
            card.getStyle()
                    .set("border", "3px solid #FF9800")
                    .set(BOXSHADOW, "0 8px 24px rgba(255, 152, 0, 0.3)")
                    .set(TRANSFORM, "scale(1.02)");
            
            // Efecto hover para ofertas destacadas
            card.getElement().addEventListener("mouseenter", e -> 
                card.getStyle()
                    .set(TRANSFORM, "scale(1.05) translateY(-5px)")
                    .set(BOXSHADOW, "0 12px 32px rgba(255, 152, 0, 0.5)")
            );
            card.getElement().addEventListener("mouseleave", e -> 
                card.getStyle()
                    .set(TRANSFORM, "scale(1.02)")
                    .set(BOXSHADOW, "0 8px 24px rgba(255, 152, 0, 0.3)")
            );
        } else {
            card.getStyle()
                    .set(BOXSHADOW, "0 4px 14px rgba(0,0,0,0.10)");
            
            // Efecto hover para productos normales
            card.getElement().addEventListener("mouseenter", e -> 
                card.getStyle()
                    .set(TRANSFORM, "translateY(-8px)")
                    .set(BOXSHADOW, "0 12px 28px rgba(0,0,0,0.20)")
            );
            card.getElement().addEventListener("mouseleave", e -> 
                card.getStyle()
                    .set(TRANSFORM, "translateY(0)")
                    .set(BOXSHADOW, "0 4px 14px rgba(0,0,0,0.10)")
            );
        }

        // Imagen
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
                .set(COLOR, COLOR2)
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
                    .set(COLOR, COLOR1)
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
                    .set(COLOR, COLOR1)
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
                .set(BACKGROUND_COLOR, COLOR2)
                .set(COLOR, COLOR1)
                .set("margin-top", "auto");
        pedirBtn.addClickListener(e -> {
            // Aquí iría la lógica para añadir al carrito
            System.out.println("Añadido al carrito: " + producto.getNombre());
        });

        content.add(h3, desc, priceAndBadges, pedirBtn);
        card.add(image, content);
        return card;
    }
}
