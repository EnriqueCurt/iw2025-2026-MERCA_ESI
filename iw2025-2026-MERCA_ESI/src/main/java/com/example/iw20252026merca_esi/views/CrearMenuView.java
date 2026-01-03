
package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.example.iw20252026merca_esi.service.MenuService;
import com.example.iw20252026merca_esi.service.SessionService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
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

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Crear Menu")
@RolesAllowed({"ADMINISTRADOR", "MANAGER"})
@Route(value = "crear-menu/:menuId?", layout = MainLayout.class)
@com.vaadin.flow.router.Menu(title = "crear menu")
public class CrearMenuView extends VerticalLayout implements BeforeEnterObserver {

    private final ProductoService productoService;
    private final MenuService menuService;
    private final SessionService sessionService;

    private TextField nombreField = new TextField("Nombre");
    private TextArea descripcionField = new TextArea("Descripción");
    private NumberField precioField = new NumberField("Precio del Menú");
    private Checkbox estadoCheckbox = new Checkbox("Activo");
    private Checkbox ofertaCheckbox = new Checkbox("Es Oferta");
    private Checkbox puntosCheckbox = new Checkbox("Es por Puntos");
    private ComboBox<Producto> productoComboBox = new ComboBox<>("Productos");
    private Grid<Producto> productoGrid = new Grid<>(Producto.class, false);
    private List<Producto> productosSeleccionados = new ArrayList<>();
    private Image imagenPreview = new Image();
    private Upload uploadImagen;
    private byte[] imagenBytes;
    private ByteArrayOutputStream imageBuffer;
    private Span precioReferenciaSpan = new Span();
    private Menu menuEnEdicion;
    private H1 titulo;

    public CrearMenuView(ProductoService productoService, MenuService menuService, SessionService sessionService) {
        this.productoService = productoService;
        this.menuService = menuService;
        this.sessionService = sessionService;

        titulo = new H1("Crear Menú");
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

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!sessionService.isLoggedIn()) {
            event.forwardTo("acceso");
            return;
        }
        
        var empleado = sessionService.getEmpleado();
        if (empleado == null || (!empleado.esAdministrador() && !empleado.esManager())) {
            Notification.show("No tienes permisos para acceder a esta página", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            event.forwardTo("");
            return;
        }
        
        // Verificar si hay un ID de menú para editar
        String menuIdStr = event.getRouteParameters().get("menuId").orElse(null);
        if (menuIdStr != null) {
            try {
                Integer menuId = Integer.parseInt(menuIdStr);
                cargarMenuParaEdicion(menuId);
            } catch (NumberFormatException e) {
                Notification.show("ID de menú inválido")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
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
                estadoCheckbox,
                ofertaCheckbox,
                puntosCheckbox
        );

        // Precio de referencia
        precioReferenciaSpan.getStyle()
                .set("color", "#666")
                .set("font-size", "0.9rem")
                .set("font-style", "italic")
                .set("margin-top", "5px");
        actualizarPrecioReferencia();

        mainLayout.add(formLayout,
                precioReferenciaSpan,
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

        ofertaCheckbox.setValue(false);
        ofertaCheckbox.getStyle()
                .set("--vaadin-checkbox-checkmark-color", "white")
                .set("--lumo-primary-color", "#D32F2F");

        puntosCheckbox.setValue(false);
        puntosCheckbox.getStyle()
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
                actualizarPrecioReferencia();
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
                actualizarPrecioReferencia();
            });
            return eliminar;
        }).setHeader("Acciones").setFlexGrow(1);

        productoGrid.setHeight("200px");
        productoGrid.setWidthFull();
    }

    private void guardarMenu() {
        if (validarFormulario()) {
            Menu menu = menuEnEdicion != null ? menuEnEdicion : new Menu();
            
            menu.setNombre(nombreField.getValue());
            menu.setDescripcion(descripcionField.getValue());
            menu.setPrecio(precioField.getValue().floatValue());
            menu.setEstado(estadoCheckbox.getValue());
            menu.setEsOferta(ofertaCheckbox.getValue());
            menu.setPuntos(puntosCheckbox.getValue());
            menu.setProductos(new java.util.HashSet<>(productosSeleccionados));
            if (imagenBytes != null) {
                menu.setImagen(imagenBytes);
            }
            
            menuService.guardarMenu(menu);
            
            String mensaje = menuEnEdicion != null ? "Menú actualizado exitosamente" : "Menú creado exitosamente";
            Notification.show(mensaje + " con " + productosSeleccionados.size() + " producto(s)")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            getUI().ifPresent(ui -> ui.navigate("menus-gestion"));
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
        ofertaCheckbox.setValue(false);
        puntosCheckbox.setValue(false);
        productosSeleccionados.clear();
        productoGrid.setItems(productosSeleccionados);
        productoComboBox.clear();
        actualizarPrecioReferencia();

        if (uploadImagen != null) {
            uploadImagen.clearFileList();
        }
        imagenPreview.setSrc("");
        imagenBytes = null;
        imageBuffer = null;
    }

    private void actualizarPrecioReferencia() {
        if (productosSeleccionados.isEmpty()) {
            precioReferenciaSpan.setText("");
        } else {
            float total = 0f;
            for (Producto p : productosSeleccionados) {
                total += p.getPrecio();
            }
            precioReferenciaSpan.setText(String.format("Precio de referencia (suma de productos): %.2f €", total));
        }
    }
    
    private void cargarMenuParaEdicion(Integer menuId) {
        Menu menu = menuService.listarMenus().stream()
                .filter(m -> m.getIdMenu().equals(menuId))
                .findFirst()
                .orElse(null);
        
        if (menu != null) {
            menuEnEdicion = menu;
            titulo.setText("Editar Menú");
            
            // Cargar datos básicos
            nombreField.setValue(menu.getNombre());
            if (menu.getDescripcion() != null) {
                descripcionField.setValue(menu.getDescripcion());
            }
            precioField.setValue((double) menu.getPrecio());
            estadoCheckbox.setValue(menu.getEstado() != null ? menu.getEstado() : true);
            ofertaCheckbox.setValue(menu.getEsOferta() != null ? menu.getEsOferta() : false);
            puntosCheckbox.setValue(menu.getPuntos() != null ? menu.getPuntos() : false);
            
            // Cargar imagen si existe
            if (menu.getImagen() != null && menu.getImagen().length > 0) {
                imagenBytes = menu.getImagen();
                String base64 = java.util.Base64.getEncoder().encodeToString(imagenBytes);
                imagenPreview.setSrc("data:image/jpeg;base64," + base64);
            }
            
            // Cargar productos
            if (menu.getProductos() != null && !menu.getProductos().isEmpty()) {
                productosSeleccionados.clear();
                productosSeleccionados.addAll(menu.getProductos());
                productoGrid.setItems(productosSeleccionados);
                actualizarPrecioReferencia();
            }
        } else {
            Notification.show("Menú no encontrado")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

}