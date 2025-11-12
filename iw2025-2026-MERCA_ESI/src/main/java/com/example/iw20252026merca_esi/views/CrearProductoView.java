package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Ingrediente;
import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.IngredienteService;
import com.example.iw20252026merca_esi.service.ProductoIngredienteService;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Crear Producto")
@AnonymousAllowed
@Route(value = "crear-producto", layout = MainLayout.class)
@Menu(title = "crear producto")
public class CrearProductoView extends VerticalLayout {

    private final ProductoService productoService;
    private final IngredienteService ingredienteService;
    private final ProductoIngredienteService productoIngredienteService;

    private TextField nombreField = new TextField("Nombre");
    private TextArea descripcionField = new TextArea("Descripción");
    private NumberField precioField = new NumberField("Precio");
    private Checkbox esOfertaCheckbox = new Checkbox("Es oferta");
    private Checkbox puntosCheckbox = new Checkbox("Puntos");
    private Checkbox estadoCheckbox = new Checkbox("Activo");
    
    // Componentes para ingredientes
    private ComboBox<Ingrediente> ingredienteComboBox = new ComboBox<>("Seleccionar Ingrediente");
    private NumberField cantidadField = new NumberField("Cantidad");
    private Grid<IngredienteProductoDTO> gridIngredientes = new Grid<>(IngredienteProductoDTO.class, false);
    private List<IngredienteProductoDTO> ingredientesSeleccionados = new ArrayList<>();

    public CrearProductoView(ProductoService productoService, IngredienteService ingredienteService, 
                            ProductoIngredienteService productoIngredienteService) {
        this.productoService = productoService;
        this.ingredienteService = ingredienteService;
        this.productoIngredienteService = productoIngredienteService;

        // Título principal con espacio arriba
        H1 titulo = new H1("Crear Producto");
        titulo.getStyle()
                .set("margin-top", "0")
                .set("margin-bottom", "20px")
                .set("color", "#D32F2F");

        // Contenido principal
        HorizontalLayout mainContent = createMainContent();

        // Centrar el contenido
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setPadding(false);
        setSpacing(false);
        
        // Crear un contenedor con padding controlado
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.getStyle().set("padding-top", "0");
        
        wrapper.add(titulo, mainContent);
        add(wrapper);
    }

    private HorizontalLayout createMainContent() {
        HorizontalLayout content = new HorizontalLayout();
        content.setJustifyContentMode(JustifyContentMode.CENTER);
        content.setWidthFull();

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("500px");
        mainLayout.setAlignItems(Alignment.CENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("100%");

        configurarCampos();

        formLayout.add(
                nombreField,
                descripcionField,
                precioField,
                esOfertaCheckbox,
                puntosCheckbox,
                estadoCheckbox
        );

        // Sección de ingredientes
        VerticalLayout seccionIngredientes = createSeccionIngredientes();

        Button guardarButton = new Button("Guardar", event -> guardarProducto());
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.getStyle().set("background-color", "#D32F2F");
        guardarButton.getStyle().set("color", "white");
        guardarButton.setWidthFull();

        Button limpiarButton = new Button("Limpiar", event -> limpiarFormulario());
        limpiarButton.getStyle().set("background-color", "#D32F2F");
        limpiarButton.getStyle().set("color", "white");
        limpiarButton.setWidthFull();

        mainLayout.add(formLayout, seccionIngredientes, guardarButton, limpiarButton);
        content.add(mainLayout);
        return content;
    }

    private VerticalLayout createSeccionIngredientes() {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setWidth("100%");
        seccion.setPadding(false);
        
        H3 titulo = new H3("Ingredientes del Producto");
        titulo.getStyle().set("color", "#D32F2F");
        titulo.getStyle().set("margin-top", "20px");
        
        // Configurar ComboBox de ingredientes
        ingredienteComboBox.setItems(ingredienteService.listarIngredientesActivos());
        ingredienteComboBox.setItemLabelGenerator(Ingrediente::getNombre);
        ingredienteComboBox.setPlaceholder("Selecciona un ingrediente");
        ingredienteComboBox.setWidthFull();
        ingredienteComboBox.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");
        
        // Configurar campo de cantidad
        cantidadField.setPlaceholder("Cantidad");
        cantidadField.setMin(0.01);
        cantidadField.setStep(0.01);
        cantidadField.setWidthFull();
        cantidadField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");
        
        Button agregarButton = new Button("Agregar", event -> agregarIngrediente());
        agregarButton.getStyle().set("background-color", "#D32F2F");
        agregarButton.getStyle().set("color", "white");
        agregarButton.setWidthFull();
        
        // Configurar Grid
        configurarGrid();
        
        seccion.add(titulo, ingredienteComboBox, cantidadField, agregarButton, gridIngredientes);
        return seccion;
    }

    private void configurarGrid() {
        gridIngredientes.addColumn(dto -> dto.ingrediente.getNombre()).setHeader("Ingrediente").setFlexGrow(2);
        gridIngredientes.addColumn(IngredienteProductoDTO::getCantidad).setHeader("Cantidad").setFlexGrow(1);
        
        gridIngredientes.addComponentColumn(dto -> {
            Button eliminarButton = new Button("Eliminar");
            eliminarButton.getStyle().set("color", "#D32F2F");
            eliminarButton.addClickListener(event -> {
                ingredientesSeleccionados.remove(dto);
                gridIngredientes.setItems(ingredientesSeleccionados);
            });
            return eliminarButton;
        }).setHeader("Acciones").setFlexGrow(1);
        
        gridIngredientes.setHeight("200px");
        gridIngredientes.setWidthFull();
    }

    private void agregarIngrediente() {
        Ingrediente ingrediente = ingredienteComboBox.getValue();
        Double cantidad = cantidadField.getValue();
        
        if (ingrediente == null) {
            Notification.show("Selecciona un ingrediente").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        if (cantidad == null || cantidad <= 0) {
            Notification.show("La cantidad debe ser mayor que 0").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        // Verificar si el ingrediente ya está agregado
        boolean yaExiste = ingredientesSeleccionados.stream()
                .anyMatch(dto -> dto.ingrediente.getIdIngrediente().equals(ingrediente.getIdIngrediente()));
        
        if (yaExiste) {
            Notification.show("Este ingrediente ya ha sido agregado").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }
        
        ingredientesSeleccionados.add(new IngredienteProductoDTO(ingrediente, cantidad.floatValue()));
        gridIngredientes.setItems(ingredientesSeleccionados);
        
        // Limpiar campos
        ingredienteComboBox.clear();
        cantidadField.clear();
        
        Notification.show("Ingrediente agregado").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void configurarCampos() {
        nombreField.setRequired(true);
        nombreField.setMaxLength(100);
        nombreField.setPlaceholder("Introduce el nombre del producto");
        nombreField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        descripcionField.setPlaceholder("Introduce una descripción");
        descripcionField.setMaxLength(1000);
        descripcionField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        precioField.setRequired(true);
        precioField.setMin(0);
        precioField.setStep(0.01);
        precioField.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));
        precioField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        estadoCheckbox.setValue(true);
        estadoCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");

        esOfertaCheckbox.setValue(false);
        esOfertaCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");

        puntosCheckbox.setValue(false);
        puntosCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");
    }





    private void guardarProducto() {
        if (validarFormulario()) {
            Producto producto = new Producto();
            producto.setNombre(nombreField.getValue());
            producto.setDescripcion(descripcionField.getValue());
            producto.setPrecio(precioField.getValue().floatValue());
            producto.setEsOferta(esOfertaCheckbox.getValue());
            producto.setPuntos(puntosCheckbox.getValue());
            producto.setEstado(estadoCheckbox.getValue());

            try {
                // Guardar el producto primero
                Producto productoGuardado = productoService.guardarProducto(producto);
                
                // Luego guardar los ingredientes asociados
                for (IngredienteProductoDTO dto : ingredientesSeleccionados) {
                    productoIngredienteService.agregarIngredienteAProducto(
                        productoGuardado, 
                        dto.ingrediente, 
                        dto.cantidad
                    );
                }
                
                Notification notification = Notification.show(
                    "Producto creado correctamente con " + ingredientesSeleccionados.size() + " ingrediente(s)"
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                limpiarFormulario();
            } catch (Exception e) {
                Notification notification = Notification.show("Error al crear el producto: " + e.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private boolean validarFormulario() {
        if (nombreField.isEmpty()) {
            Notification.show("El nombre es obligatorio").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (precioField.isEmpty() || precioField.getValue() <= 0) {
            Notification.show("El precio debe ser mayor que 0").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        precioField.clear();
        esOfertaCheckbox.setValue(false);
        puntosCheckbox.setValue(false);
        estadoCheckbox.setValue(true);
        ingredienteComboBox.clear();
        cantidadField.clear();
        ingredientesSeleccionados.clear();
        gridIngredientes.setItems(ingredientesSeleccionados);
    }
    
    // Clase interna para manejar ingredientes seleccionados
    private static class IngredienteProductoDTO {
        private final Ingrediente ingrediente;
        private final Float cantidad;
        
        public IngredienteProductoDTO(Ingrediente ingrediente, Float cantidad) {
            this.ingrediente = ingrediente;
            this.cantidad = cantidad;
        }
        
        public Ingrediente getIngrediente() {
            return ingrediente;
        }
        
        public Float getCantidad() {
            return cantidad;
        }
    }
}
