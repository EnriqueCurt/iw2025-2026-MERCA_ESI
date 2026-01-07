package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.components.SeleccionIngredientesDialog;
import com.example.iw20252026merca_esi.model.Categoria;
import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.CategoriaService;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.example.iw20252026merca_esi.service.PedidoActualService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageTitle("Carta Completa")
@AnonymousAllowed
@Route(value = "carta", layout = MainLayout.class)
@Menu(title = "Carta")
public class CartaView extends VerticalLayout {
    
    private static final String BORDER_RADIUS = "border-radius";
    private static final String DISPLAY = "display";
    private static final String PADDING = "padding";
    private static final String COLOR = "color";
    private static final String COLOR1 = "white";
    private static final String COLOR2 = "#e30613";
    private static final String FONTSIZE = "font-size";
    private static final String BACKGROUND = "background";
    private static final String MARGIN = "margin";
    private static final String FONTWEIGHT = "font-weight";
    private static final String BOXSHADOW = "box-shadow";
    private static final String TRANSFORM = "transform";
    private static final String MARGINTOP = "margin-top";
    
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PedidoActualService pedidoActualService;

    public CartaView(ProductoService productoService, CategoriaService categoriaService, PedidoActualService pedidoActualService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.pedidoActualService = pedidoActualService;
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle()
                .set(BACKGROUND, "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)")
                .set("overflow-y", "auto");

        crearContenido();
    }

    private void crearContenido() {
        // Header minimalista - estilo igual a PizzaView
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.setJustifyContentMode(JustifyContentMode.CENTER);
        header.getStyle()
                .set(PADDING, "clamp(16px, 3vw, 24px)")
                .set("flex-wrap", "wrap");

        H1 titulo = new H1("Carta");
        titulo.getStyle()
                .set(MARGIN, "0")
                .set(COLOR, COLOR2)
                .set("text-align", "center");

        header.add(titulo);
        add(header);

        // Contenedor principal
        Div mainContainer = new Div();
        mainContainer.getStyle()
                .set("max-width", "1400px")
                .set(MARGIN, "0 auto")
                .set(PADDING, "30px 20px")
                .set("width", "100%");

        // Obtener productos activos agrupados por categoría (excluyendo ofertas y puntos)
        List<Producto> productosActivos = productoService.listarProductosConCategoriasEIngredientes().stream()
                .filter(Producto::getEstado)
                .filter(p -> !p.getEsOferta() && !p.getPuntos())
                .collect(Collectors.toList());
        Map<String, List<Producto>> productosPorCategoria = productosActivos.stream()
                .flatMap(p -> p.getCategorias().stream()
                        .map(c -> Map.entry(c.getNombre(), p)))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        // Mostrar cada categoría
        List<Categoria> categorias = categoriaService.listarCategorias();
        for (Categoria categoria : categorias) {
            List<Producto> productosCategoria = productosPorCategoria.get(categoria.getNombre());
            if (productosCategoria != null && !productosCategoria.isEmpty()) {
                mainContainer.add(crearSeccionCategoria(categoria.getNombre(), productosCategoria, false));
            }
        }

        add(mainContainer);
    }

    private Div crearSeccionCategoria(String nombreCategoria, List<Producto> productos, boolean esOferta) {
        Div seccion = new Div();
        seccion.getStyle()
                .set("margin-bottom", "50px");

        // Título de categoría
        H2 tituloCategoria = new H2(nombreCategoria);
        tituloCategoria.getStyle()
                .set(MARGIN, "0 0 20px 0")
                .set(FONTSIZE, "2rem")
                .set(FONTWEIGHT, "700")
                .set(COLOR, esOferta ? "#FF9800" : "#333")
                .set("text-align", "left")
                .set("border-bottom", esOferta ? "3px solid #FF9800" : "3px solid #e30613")
                .set("padding-bottom", "10px");

        seccion.add(tituloCategoria);

        // Grid de productos
        Div grid = new Div();
        grid.getStyle()
                .set(DISPLAY, "grid")
                .set("grid-template-columns", "repeat(auto-fill, minmax(220px, 1fr))")
                .set("gap", "20px")
                .set("width", "100%");

        for (Producto producto : productos) {
            grid.add(createProductCard(producto, esOferta));
        }

        seccion.add(grid);
        return seccion;
    }

    private Div createProductCard(Producto producto, boolean destacada) {
        Div card = new Div();
        card.getStyle()
                .set(BACKGROUND, "#ffffff")
                .set(BORDER_RADIUS, "12px")
                .set(BOXSHADOW, destacada ? "0 8px 24px rgba(255, 152, 0, 0.3)" : "0 4px 14px rgba(0,0,0,0.10)")
                .set("border", destacada ? "3px solid #FF9800" : "none")
                .set("overflow", "hidden")
                .set(DISPLAY, "flex")
                .set("flex-direction", "column")
                .set("height", "100%")
                .set("max-width", "350px")
                .set("transition", "all 0.3s ease")
                .set("cursor", "pointer");

        if (destacada) {
            card.getStyle().set(TRANSFORM, "scale(1.02)");
        }

        // Efecto hover
        if (destacada) {
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
            card.getElement().addEventListener("mouseenter", e ->
                    card.getStyle()
                            .set(TRANSFORM, "translateY(-8px)")
                            .set(BOXSHADOW, "0 12px 24px rgba(0,0,0,0.20)")
            );
            card.getElement().addEventListener("mouseleave", e ->
                    card.getStyle()
                            .set(TRANSFORM, "translateY(0)")
                            .set(BOXSHADOW, "0 4px 14px rgba(0,0,0,0.10)")
            );
        }

        // Imagen
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
                .set(MARGIN, "0")
                .set(FONTSIZE, "clamp(0.95rem, 2vw, 1.05rem)")
                .set("line-height", "1.2");

        Span desc = new Span(producto.getDescripcion() != null ? producto.getDescripcion() : "Sin descripción");
        desc.getStyle()
                .set(DISPLAY, "block")
                .set(COLOR, "#555")
                .set(FONTSIZE, "0.85rem")
                .set("line-height", "1.4")
                .set(MARGIN, "4px 0")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set(DISPLAY, "-webkit-box")
                .set("-webkit-line-clamp", "2")
                .set("-webkit-box-orient", "vertical");

        // Badges
        Div badges = new Div();
        badges.getStyle()
                .set(DISPLAY, "flex")
                .set("gap", "6px")
                .set("flex-wrap", "wrap")
                .set(MARGINTOP, "auto");

        if (Boolean.TRUE.equals(producto.getEsOferta())) {
            Span ofertaBadge = new Span("🔥 OFERTA");
            ofertaBadge.getStyle()
                    .set(BACKGROUND, "linear-gradient(135deg, #FF9800 0%, #F57C00 100%)")
                    .set(COLOR, COLOR1)
                    .set(PADDING, "4px 10px")
                    .set(BORDER_RADIUS, "20px")
                    .set(FONTSIZE, "0.75rem")
                    .set(FONTWEIGHT, "600")
                    .set(BOXSHADOW, "0 2px 6px rgba(255, 152, 0, 0.3)");
            badges.add(ofertaBadge);
        }

        if (Boolean.TRUE.equals(producto.getPuntos())) {
            Span puntosBadge = new Span("⭐ PUNTOS");
            puntosBadge.getStyle()
                    .set(BACKGROUND, "linear-gradient(135deg, #9C27B0 0%, #7B1FA2 100%)")
                    .set(COLOR, COLOR1)
                    .set(PADDING, "4px 10px")
                    .set(BORDER_RADIUS, "20px")
                    .set(FONTSIZE, "0.75rem")
                    .set(FONTWEIGHT, "600")
                    .set(BOXSHADOW, "0 2px 6px rgba(156, 39, 176, 0.3)");
            badges.add(puntosBadge);
        }

        // Precio o Puntos
        String precioTexto;
        if (Boolean.TRUE.equals(producto.getPuntos())) {
            precioTexto = String.format("%d puntos", producto.getPrecio().intValue());
        } else {
            precioTexto = String.format("%.2f€", producto.getPrecio());
        }
        Span precio = new Span(precioTexto);
        precio.getStyle()
                .set(DISPLAY, "block")
                .set(FONTSIZE, "1.3rem")
                .set(FONTWEIGHT, "700")
                .set(COLOR, COLOR2)
                .set(MARGINTOP, "8px");

        // Botón Pedir
        Button pedirBtn = new Button("PEDIR");
        pedirBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pedirBtn.setWidthFull();
        pedirBtn.getStyle()
                .set("background-color", COLOR2)
                .set(COLOR, COLOR1)
                .set(MARGINTOP, "auto")
                .set(BORDER_RADIUS, "8px")
                .set(FONTWEIGHT, "600");
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

        content.add(h3, desc, badges, precio, pedirBtn);
        card.add(image, content);

        return card;
    }
}
