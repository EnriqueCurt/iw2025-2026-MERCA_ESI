package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Categoria;
import com.example.iw20252026merca_esi.service.CategoriaService;

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

@PageTitle("Crear Categoría")
@AnonymousAllowed
@Route(value = "crear-categoria", layout = MainLayout.class)
@Menu(title = "crear categoria")
public class CrearCategoriaView extends VerticalLayout {

    private final CategoriaService categoriaService;

    private TextField nombreField = new TextField("Nombre");

    private Checkbox estadoCheckbox = new Checkbox("Activo");

    public CrearCategoriaView(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;

        // Título principal
        H1 titulo = new H1("Crear Categoria");

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

        Button guardarButton = new Button("Guardar", event -> guardarCategoria());
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.getStyle().set("background-color", "#D32F2F");
        guardarButton.getStyle().set("color", "white");

        Button limpiarButton = new Button("Limpiar", event -> limpiarFormulario());
        limpiarButton.getStyle().set("background-color", "#D32F2F");
        limpiarButton.getStyle().set("color", "white");

        formLayout.add(
                nombreField,
                guardarButton,
                limpiarButton
        );

        content.add(formLayout);
        return content;
    }

    private void configurarCampos() {
        nombreField.setRequired(true);
        nombreField.setMaxLength(100);
        nombreField.setPlaceholder("Introduce el nombre de la Categoria");
        nombreField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        estadoCheckbox.setValue(true);
        estadoCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");
    }

    private void guardarCategoria() {
        if (validarFormulario()) {
            Categoria Categoria = new Categoria();
            Categoria.setNombre(nombreField.getValue());


            try {
                categoriaService.guardarCategoria(Categoria);
                Notification notification = Notification.show("Categoria creada correctamente");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                limpiarFormulario();
            } catch (Exception e) {
                Notification notification = Notification.show("Error al crear la Categoria: " + e.getMessage());
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

        estadoCheckbox.setValue(true);
    }
}
