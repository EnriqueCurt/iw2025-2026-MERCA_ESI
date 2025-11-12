package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Ingrediente;
import com.example.iw20252026merca_esi.service.IngredienteService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Crear Ingrediente")
@AnonymousAllowed
@Route(value = "crear-ingrediente", layout = MainLayout.class)
@Menu(title = "crear ingrediente")
public class CrearIngredienteView extends VerticalLayout {

    private final IngredienteService ingredienteService;

    private TextField nombreField = new TextField("Nombre");
    private TextArea descripcionField = new TextArea("Descripción");
    private Checkbox estadoCheckbox = new Checkbox("Activo");

    public CrearIngredienteView(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;

        // Título principal
        H1 titulo = new H1("Crear Ingrediente");

        // Contenido principal
        HorizontalLayout mainContent = createMainContent();

        // Centrar el contenido
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(titulo, mainContent);
    }

    private HorizontalLayout createMainContent() {
        HorizontalLayout content = new HorizontalLayout();
        content.setJustifyContentMode(JustifyContentMode.CENTER);
        content.setWidthFull();

        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("400px");

        configurarCampos();

        Button guardarButton = new Button("Guardar", event -> guardarIngrediente());
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.getStyle().set("background-color", "#D32F2F");
        guardarButton.getStyle().set("color", "white");

        Button limpiarButton = new Button("Limpiar", event -> limpiarFormulario());
        limpiarButton.getStyle().set("background-color", "#D32F2F");
        limpiarButton.getStyle().set("color", "white");

        formLayout.add(
                nombreField,
                descripcionField,
                estadoCheckbox,
                guardarButton,
                limpiarButton
        );

        content.add(formLayout);
        return content;
    }

    private void configurarCampos() {
        nombreField.setRequired(true);
        nombreField.setMaxLength(100);
        nombreField.setPlaceholder("Introduce el nombre del ingrediente");
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

        estadoCheckbox.setValue(true);
        estadoCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");
    }

    private void guardarIngrediente() {
        if (validarFormulario()) {
            Ingrediente ingrediente = new Ingrediente();
            ingrediente.setNombre(nombreField.getValue());
            ingrediente.setDescripcion(descripcionField.getValue());
            ingrediente.setEstado(estadoCheckbox.getValue());

            try {
                ingredienteService.guardarIngrediente(ingrediente);
                Notification notification = Notification.show("Ingrediente creado correctamente");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                limpiarFormulario();
            } catch (Exception e) {
                Notification notification = Notification.show("Error al crear el ingrediente: " + e.getMessage());
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

    private boolean validarFormulario() {
        if (nombreField.isEmpty()) {
            Notification.show("El nombre es obligatorio").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        estadoCheckbox.setValue(true);
    }
}
