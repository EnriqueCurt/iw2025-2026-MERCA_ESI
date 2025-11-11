package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("crear-producto")
public class CrearProductoView extends VerticalLayout {

    private final ProductoService productoService;

    private TextField nombreField = new TextField("Nombre");
    private TextArea descripcionField = new TextArea("Descripción");
    private NumberField precioField = new NumberField("Precio");
    private Checkbox esOfertaCheckbox = new Checkbox("Es oferta");
    private Checkbox puntosCheckbox = new Checkbox("Puntos");
    private Checkbox estadoCheckbox = new Checkbox("Activo");

    public CrearProductoView(ProductoService productoService) {
        this.productoService = productoService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("400px");

        configurarCampos();

        Button guardarButton = new Button("Guardar", event -> guardarProducto());
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button limpiarButton = new Button("Limpiar", event -> limpiarFormulario());

        formLayout.add(
                nombreField,
                descripcionField,
                precioField,
                esOfertaCheckbox,
                puntosCheckbox,
                estadoCheckbox,
                guardarButton,
                limpiarButton
        );

        add(formLayout);
    }

    private void configurarCampos() {
        nombreField.setRequired(true);
        nombreField.setMaxLength(100);
        nombreField.setPlaceholder("Introduce el nombre del producto");

        descripcionField.setPlaceholder("Introduce una descripción");
        descripcionField.setMaxLength(1000);

        precioField.setRequired(true);
        precioField.setMin(0);
        precioField.setStep(0.01);
        precioField.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));

        estadoCheckbox.setValue(true);
        esOfertaCheckbox.setValue(false);
        puntosCheckbox.setValue(false);
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
                productoService.guardarProducto(producto);
                Notification notification = Notification.show("Producto creado correctamente");
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
    }
}
