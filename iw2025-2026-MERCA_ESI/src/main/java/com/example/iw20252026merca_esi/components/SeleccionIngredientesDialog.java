package com.example.iw20252026merca_esi.components;

import com.example.iw20252026merca_esi.model.Ingrediente;
import com.example.iw20252026merca_esi.model.ItemPedido;
import com.example.iw20252026merca_esi.model.Producto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Dialog para seleccionar ingredientes a excluir de productos o menús.
 * Permite desmarcar ingredientes que NO se desean en el pedido.
 */
public class SeleccionIngredientesDialog extends Dialog {
    
    private final ItemPedido item;
    private final Consumer<ItemPedido> onConfirmar;

    private static final String COLOR = "color";
    private static final String MARGIN_BOTTOM = "margin-bottom";
    private static final String ID_PRODUCTO = "idProducto";
    
    /**
     * Constructor para producto individual.
     */
    public SeleccionIngredientesDialog(Producto producto, Consumer<ItemPedido> onConfirmar) {
        this.item = ItemPedido.fromProducto(producto);
        this.onConfirmar = onConfirmar;
        
        setHeaderTitle("Personaliza tu " + producto.getNombre());
        setWidth("500px");
        
        crearContenidoProducto(producto);
        crearBotones();
    }
    
    /**
     * Constructor para menú completo.
     */
    public SeleccionIngredientesDialog(
            com.example.iw20252026merca_esi.model.Menu menu, 
            Consumer<ItemPedido> onConfirmar) {
        this.item = ItemPedido.fromMenu(menu);
        this.onConfirmar = onConfirmar;
        
        setHeaderTitle("Personaliza tu " + menu.getNombre());
        setWidth("600px");
        setHeight("80vh");
        
        crearContenidoMenu(menu);
        crearBotones();
    }
    
    private void crearContenidoProducto(Producto producto) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        
        List<Ingrediente> ingredientes = producto.getProductoIngredientes() != null 
            ? producto.getProductoIngredientes().stream()
                .map(pi -> pi.getIngrediente())
                .toList()
            : new ArrayList<>();
        
        if (ingredientes.isEmpty()) {
            Paragraph sinIngredientes = new Paragraph("Este producto no tiene ingredientes personalizables.");
            sinIngredientes.getStyle()
                .set(COLOR, "#666")
                .set("font-style", "italic")
                .set("text-align", "center")
                .set("padding", "20px");
            layout.add(sinIngredientes);
        } else {
            Paragraph instrucciones = new Paragraph("Desmarca los ingredientes que NO quieres:");
            instrucciones.getStyle()
                .set(COLOR, "#666")
                .set(MARGIN_BOTTOM, "10px");
            
            layout.add(instrucciones);
            layout.add(crearSeccionIngredientes(ingredientes, producto.getIdProducto()));
        }
        
        add(layout);
    }
    
    private void crearContenidoMenu(com.example.iw20252026merca_esi.model.Menu menu) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);
        
        Paragraph instrucciones = new Paragraph("Personaliza cada producto del menú:");
        instrucciones.getStyle()
            .set(COLOR, "#666")
            .set(MARGIN_BOTTOM, "15px")
            .set("font-weight", "600");
        layout.add(instrucciones);
        
        // Crear sección para cada producto del menú
        if (item.getProductosDelMenu() != null) {
            for (Producto producto : item.getProductosDelMenu()) {
                List<Ingrediente> ingredientes = producto.getProductoIngredientes() != null 
                    ? producto.getProductoIngredientes().stream()
                        .map(pi -> pi.getIngrediente())
                        .toList()
                    : new ArrayList<>();
                
                if (!ingredientes.isEmpty()) {
                    Div seccionProducto = new Div();
                    seccionProducto.getStyle()
                        .set("background", "#f8f9fa")
                        .set("border-radius", "8px")
                        .set("padding", "15px")
                        .set(MARGIN_BOTTOM, "15px");
                    
                    H4 tituloProducto = new H4(producto.getNombre());
                    tituloProducto.getStyle()
                        .set("margin", "0 0 10px 0")
                        .set(COLOR, "#e30613");
                    
                    seccionProducto.add(tituloProducto);
                    seccionProducto.add(crearSeccionIngredientes(ingredientes, producto.getIdProducto()));
                    
                    layout.add(seccionProducto);
                }
            }
        }
        
        add(layout);
    }
    
    private Div crearSeccionIngredientes(List<Ingrediente> ingredientes, Integer idProducto) {
        Div contenedor = new Div();
        
        CheckboxGroup<Ingrediente> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setLabel("Ingredientes");
        checkboxGroup.setItems(ingredientes);
        checkboxGroup.setItemLabelGenerator(Ingrediente::getNombre);
        
        // Por defecto, todos los ingredientes están seleccionados (se incluyen)
        checkboxGroup.setValue(new HashSet<>(ingredientes));
        
        checkboxGroup.getStyle()
            .set("display", "grid")
            .set("grid-template-columns", "repeat(auto-fill, minmax(150px, 1fr))")
            .set("gap", "10px");
        
        // Guardar referencia para recuperar después
        contenedor.getElement().setProperty(ID_PRODUCTO, idProducto.toString());
        contenedor.add(checkboxGroup);
        
        return contenedor;
    }
    
    private void crearBotones() {
        Button cancelarBtn = new Button("Cancelar", e -> close());
        
        Button confirmarBtn = new Button("Añadir al Pedido", e -> {
            procesarExclusiones();
            onConfirmar.accept(item);
            close();
        });
        confirmarBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmarBtn.getStyle().set("background-color", "#e30613");
        
        HorizontalLayout botonesLayout = new HorizontalLayout(cancelarBtn, confirmarBtn);
        botonesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        botonesLayout.getStyle()
            .set("padding-top", "20px")
            .set("border-top", "1px solid #e0e0e0");
        
        getFooter().add(botonesLayout);
    }
    
    private void procesarExclusiones() {
        // Obtener todos los contenedores de ingredientes
        getChildren()
            .filter(c -> c instanceof VerticalLayout)
            .findFirst()
            .ifPresent(layout -> {
                layout.getChildren()
                    .forEach(component -> {
                        if (component instanceof Div) {
                            Div div = (Div) component;
                            String idProd = div.getElement().getProperty(ID_PRODUCTO);
                            
                            // Si este Div tiene idProducto, procesarlo directamente
                            if (idProd != null) {
                                procesarSeccion(div);
                            } else {
                                // Para menús: buscar contenedores con idProducto en hijos
                                div.getChildren()
                                    .filter(c -> c instanceof Div)
                                    .forEach(hijo -> {
                                        String idProdHijo = hijo.getElement().getProperty(ID_PRODUCTO);
                                        if (idProdHijo != null) {
                                            procesarSeccion((Div) hijo);
                                        }
                                    });
                            }
                        }
                    });
            });
    }
    
    private void procesarSeccion(Div seccion) {
        String idProductoStr = seccion.getElement().getProperty(ID_PRODUCTO);
        if (idProductoStr == null) return;
        
        Integer idProducto = Integer.parseInt(idProductoStr);
        
        seccion.getChildren()
            .filter(c -> c instanceof CheckboxGroup)
            .findFirst()
            .ifPresent(component -> {
                @SuppressWarnings("unchecked")
                CheckboxGroup<Ingrediente> checkboxGroup = (CheckboxGroup<Ingrediente>) component;
                
                Set<Ingrediente> seleccionados = checkboxGroup.getValue();
                List<Ingrediente> todosIngredientes = new ArrayList<>(checkboxGroup.getListDataView().getItems().toList());
                
                // Los ingredientes EXCLUIDOS son los que NO están seleccionados
                List<Ingrediente> excluidos = todosIngredientes.stream()
                    .filter(ing -> !seleccionados.contains(ing))
                    .toList();
                
                item.actualizarExclusiones(idProducto, excluidos);
            });
    }
}
