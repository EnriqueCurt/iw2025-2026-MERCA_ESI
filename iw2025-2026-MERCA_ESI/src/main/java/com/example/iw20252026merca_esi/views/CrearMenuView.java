
package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.example.iw20252026merca_esi.service.MenuService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.upload.Upload;

import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.RolesAllowed;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Crear Menu")
@RolesAllowed("ADMINISTRADOR,PROPIETARIO,MANAGER")
@Route(value = "crear-menu", layout = MainLayout.class)
@com.vaadin.flow.router.Menu(title = "crear menu")
public class CrearMenuView extends VerticalLayout {

    private final ProductoService productoService;
    private final MenuService menuService;

    private TextField nombreField = new TextField("Nombre");
    private TextArea descripcionField = new TextArea("Descripción");
    private NumberField precioField = new NumberField("Precio");
    private Checkbox estadoCheckbox = new Checkbox("Activo");
    private ComboBox<Producto> productoComboBox = new ComboBox<>("Productos");
    private Grid<Producto> productoGrid = new Grid<>(Producto.class, false);
    private List<Producto> productosSeleccionados = new ArrayList<>();
    private Image imagenPreview = new Image();
    private Upload uploadImagen;
    private byte[] imagenBytes;
    private ByteArrayOutputStream imageBuffer;

    public CrearMenuView(ProductoService productoService, MenuService menuService) {
        this.productoService = productoService;
        this.menuService = menuService;

        H1 titulo = new H1("Crear Menú");
        titulo.getStyle()
                .set("margin-top", "0")
                .set("margin-bottom", "20px")
                .set("color", "#D32F2F");

        HorizontalLayout mainContent = createMainContent();

        // Centrar el contenido
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setPadding(false);
        setSpacing(false);

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

        // Sección imagen
        VerticalLayout seccionImagen = new VerticalLayout();
        seccionImagen.setWidth("100%");
        seccionImagen.setPadding(false);
        H3 tituloImagen = new H3("Imagen del Menú");
        tituloImagen.getStyle().set("color", "#D32F2F");
        seccionImagen.add(tituloImagen, uploadImagen, imagenPreview);

        Button guardarButton = new Button("Guardar", event -> guardarMenu());
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.getStyle().set("background-color", "#D32F2F");
        guardarButton.getStyle().set("color", "white");
        guardarButton.setWidthFull();

        Button limpiarButton = new Button("Limpiar", event -> limpiarFormulario());
        limpiarButton.getStyle().set("background-color", "#D32F2F");
        limpiarButton.getStyle().set("color", "white");
        limpiarButton.setWidthFull();

        formLayout.add(
                nombreField,
                descripcionField,
                precioField,
                estadoCheckbox
        );

        mainLayout.add(formLayout,
                seccionImagen,
                productoComboBox,
                productoGrid,
                guardarButton,
                limpiarButton);
        content.add(mainLayout);
        return content;
    }

    private void configurarCampos() {
        nombreField.setRequired(true);
        nombreField.setMaxLength(100);
        nombreField.setPlaceholder("Introduce el nombre del menú");
        nombreField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        descripcionField.setMaxLength(500);
        descripcionField.setPlaceholder("Introduce una descripción");
        descripcionField.setHeight("150px");
        descripcionField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        precioField.setRequired(true);
        precioField.setMin(0.0);
        precioField.setPrefixComponent(new com.vaadin.flow.component.html.Span("€"));
        precioField.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        estadoCheckbox.setValue(true);
        estadoCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");

        // ComboBox productos
        List<Producto> productos = productoService.listarProductosActivos();
        productoComboBox.setItems(productos);
        productoComboBox.setItemLabelGenerator(Producto::getNombre);
        productoComboBox.setPlaceholder("Selecciona un producto");
        productoComboBox.setWidthFull();
        productoComboBox.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        productoComboBox.addValueChangeListener(event -> {
            Producto seleccionado = event.getValue();
            if (seleccionado != null && !productosSeleccionados.contains(seleccionado)) {
                productosSeleccionados.add(seleccionado);
                productoGrid.setItems(productosSeleccionados);
            }
            productoComboBox.clear();
        });

        configurarGrid();
        configurarUploadImagen();
    }

    private void configurarUploadImagen() {
        // Preview
        imagenPreview.setAlt("Vista previa de la imagen");
        imagenPreview.setWidth("200px");
        imagenPreview.setHeight("200px");
        imagenPreview.getStyle().set("object-fit", "cover");
        imagenPreview.getStyle().set("border-radius", "8px");

        uploadImagen = new Upload();
        uploadImagen.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");
        uploadImagen.setMaxFiles(1);
        uploadImagen.setMaxFileSize(5 * 1024 * 1024);
        uploadImagen.setDropLabel(new com.vaadin.flow.component.html.Span("Arrastra la imagen aquí"));
        uploadImagen.setWidthFull();

        uploadImagen.setReceiver((fileName, mimeType) -> {
            imageBuffer = new ByteArrayOutputStream();
            return imageBuffer;
        });

        uploadImagen.addSucceededListener(event -> {
            try {
                imagenBytes = imageBuffer.toByteArray();
                String finalMimeType = (event.getMIMEType() != null && !event.getMIMEType().isEmpty())
                        ? event.getMIMEType()
                        : "image/jpeg";
                String base64 = java.util.Base64.getEncoder().encodeToString(imagenBytes);
                imagenPreview.setSrc("data:" + finalMimeType + ";base64," + base64);
                Notification.show("Imagen cargada correctamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                Notification.show("Error al procesar la imagen: " + e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        uploadImagen.addFileRejectedListener(event ->
                Notification.show("Archivo rechazado: " + event.getErrorMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR)
        );

        uploadImagen.addFailedListener(event -> {
            String reason = event.getReason() != null ? event.getReason().getMessage() : "desconocida";
            Notification.show("Error al subir la imagen: " + reason).addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
    }

    private void configurarGrid() {
        productoGrid.removeAllColumns();
        productoGrid.addColumn(Producto::getNombre).setHeader("Nombre").setFlexGrow(2);
        productoGrid.addColumn(Producto::getDescripcion).setHeader("Descripción").setFlexGrow(3);
        productoGrid.addColumn(Producto::getPrecio).setHeader("Precio").setFlexGrow(1);
        productoGrid.addComponentColumn(prod -> {
            Button eliminar = new Button("Eliminar");
            eliminar.getStyle().set("color", "#D32F2F");
            eliminar.addClickListener(e -> {
                productosSeleccionados.remove(prod);
                productoGrid.setItems(productosSeleccionados);
            });
            return eliminar;
        }).setHeader("Acciones").setFlexGrow(1);

        productoGrid.setHeight("200px");
        productoGrid.setWidthFull();
    }

    private void guardarMenu() {
        if (validarFormulario()) {
            // Lógica mínima de ejemplo (a adaptar al servicio real)
            Notification n = Notification.show(
                    "Menú guardado exitosamente con " + productosSeleccionados.size() + " producto(s)"
            );
            n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            Menu nuevoMenu = new Menu();
            nuevoMenu.setNombre(nombreField.getValue());
            nuevoMenu.setDescripcion(descripcionField.getValue());
            nuevoMenu.setPrecio(precioField.getValue().floatValue());
            nuevoMenu.setEstado(estadoCheckbox.getValue());
            nuevoMenu.setProductos(new java.util.HashSet<>(productosSeleccionados));
            if (imagenBytes != null) {
                nuevoMenu.setImagen(imagenBytes);
            }
            menuService.guardarMenu(nuevoMenu);
            limpiarFormulario();
        }
    }

    private boolean validarFormulario() {
        if (nombreField.isEmpty()) {
            Notification.show("El nombre es obligatorio").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (precioField.isEmpty() || precioField.getValue() <= 0) {
            Notification.show("El precio debe ser mayor que cero").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        if (productosSeleccionados.isEmpty()) {
            Notification.show("Debe seleccionar al menos un producto").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        precioField.clear();
        estadoCheckbox.setValue(true);
        productosSeleccionados.clear();
        productoGrid.setItems(productosSeleccionados);
        productoComboBox.clear();

        if (uploadImagen != null) {
            uploadImagen.clearFileList();
        }
        imagenPreview.setSrc("");
        imagenBytes = null;
        imageBuffer = null;
    }

}