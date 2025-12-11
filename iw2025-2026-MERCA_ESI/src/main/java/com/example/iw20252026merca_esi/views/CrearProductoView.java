package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Categoria;
import com.example.iw20252026merca_esi.model.Ingrediente;
import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.service.CategoriaService;
import com.example.iw20252026merca_esi.service.IngredienteService;
import com.example.iw20252026merca_esi.service.ProductoIngredienteService;
import com.example.iw20252026merca_esi.service.ProductoService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@PageTitle("Crear Producto")
@RolesAllowed("ADMINISTRADOR,PROPIETARIO,MANAGER")
@Route(value = "crear-producto", layout = MainLayout.class)
@Menu(title = "crear producto")
public class CrearProductoView extends VerticalLayout {

    private final ProductoService productoService;
    private final IngredienteService ingredienteService;
    private final ProductoIngredienteService productoIngredienteService;
    private final CategoriaService categoriaService;

    private final TextField nombreField = new TextField("Nombre");
    private final TextArea descripcionField = new TextArea("Descripción");
    private final NumberField precioField = new NumberField("Precio");
    private final Checkbox esOfertaCheckbox = new Checkbox("Es oferta");
    private final Checkbox puntosCheckbox = new Checkbox("Puntos");
    private final Checkbox estadoCheckbox = new Checkbox("Activo");
    private Upload uploadImagen;
    private final Image imagenPreview = new Image();
    private byte[] imagenBytes;
    private ByteArrayOutputStream imageBuffer;
    
    // Componentes para ingredientes
    private final ComboBox<Ingrediente> ingredienteComboBox = new ComboBox<>("Seleccionar Ingrediente");
    private final NumberField cantidadField = new NumberField("Cantidad");
    private final Grid<IngredienteProductoDTO> gridIngredientes = new Grid<>(IngredienteProductoDTO.class, false);
    private final List<IngredienteProductoDTO> ingredientesSeleccionados = new ArrayList<>();

    // Componentes para categorias
    private final ComboBox<Categoria> categoriaComboBox = new ComboBox<>("Seleccionar Categoria");
    private final Grid<Categoria> gridCategorias = new Grid<>(Categoria.class, false);
    private final List<Categoria> categoriasSeleccionadas = new ArrayList<>();


    public CrearProductoView(ProductoService productoService, IngredienteService ingredienteService, 
                            ProductoIngredienteService productoIngredienteService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.ingredienteService = ingredienteService;
        this.productoIngredienteService = productoIngredienteService;
        this.categoriaService = categoriaService;

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

        // Sección de imagen
        VerticalLayout seccionImagen = new VerticalLayout();
        seccionImagen.setWidth("100%");
        seccionImagen.setPadding(false);
        H3 tituloImagen = new H3("Imagen del Producto");
        tituloImagen.getStyle().set("color", "#D32F2F");
        seccionImagen.add(tituloImagen, uploadImagen, imagenPreview);

        // Sección de ingredientes
        VerticalLayout seccionIngredientes = createSeccionIngredientes();

        // Sección de categorias
        VerticalLayout seccionCategorias = createSeccionCategorias();

        Button guardarButton = new Button("Guardar", event -> guardarProducto());
        guardarButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        guardarButton.getStyle().set("background-color", "#D32F2F");
        guardarButton.getStyle().set("color", "white");
        guardarButton.setWidthFull();

        Button limpiarButton = new Button("Limpiar", event -> limpiarFormulario());
        limpiarButton.getStyle().set("background-color", "#D32F2F");
        limpiarButton.getStyle().set("color", "white");
        limpiarButton.setWidthFull();

        mainLayout.add(formLayout, seccionImagen, seccionIngredientes, seccionCategorias, guardarButton, limpiarButton);
        content.add(mainLayout);
        return content;
    }


    private VerticalLayout createSeccionIngredientes() {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setWidth("100%");
        seccion.setPadding(false);
        
        // Header con título y botón para crear ingrediente
        HorizontalLayout headerIngredientes = new HorizontalLayout();
        headerIngredientes.setWidthFull();
        headerIngredientes.setAlignItems(Alignment.CENTER);
        headerIngredientes.setJustifyContentMode(JustifyContentMode.BETWEEN);
        
        H3 titulo = new H3("Ingredientes del Producto");
        titulo.getStyle().set("color", "#D32F2F");
        titulo.getStyle().set("margin", "20px 0 10px 0");
        
        Button crearIngredienteBtn = new Button("Nuevo Ingrediente", new Icon(VaadinIcon.PLUS_CIRCLE));
        crearIngredienteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        crearIngredienteBtn.getStyle()
                .set("color", "#D32F2F")
                .set("font-size", "0.9rem");
        crearIngredienteBtn.addClickListener(e -> 
            UI.getCurrent().navigate("crear-ingrediente")
        );
        
        headerIngredientes.add(titulo, crearIngredienteBtn);
        
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

        
        
        seccion.add(headerIngredientes, ingredienteComboBox, cantidadField, agregarButton, gridIngredientes);
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

    private VerticalLayout createSeccionCategorias() {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setWidth("100%");
        seccion.setPadding(false);

        // Header con título y botón para crear categoria
        HorizontalLayout headerCategorias = new HorizontalLayout();
        headerCategorias.setWidthFull();
        headerCategorias.setAlignItems(Alignment.CENTER);
        headerCategorias.setJustifyContentMode(JustifyContentMode.BETWEEN);

        H3 titulo = new H3("Categorías del Producto");
        titulo.getStyle().set("color", "#D32F2F");
        titulo.getStyle().set("margin", "20px 0 10px 0");

        Button crearCategoriaBtn = new Button("Nueva Categoría", new Icon(VaadinIcon.PLUS_CIRCLE));
        crearCategoriaBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        crearCategoriaBtn.getStyle()
                .set("color", "#D32F2F")
                .set("font-size", "0.9rem");
        crearCategoriaBtn.addClickListener(e ->
            UI.getCurrent().navigate("crear-categoria")
        );

        headerCategorias.add(titulo, crearCategoriaBtn);

        // Configurar ComboBox de categorias (todas las categorías)
        categoriaComboBox.setItems(categoriaService.listarCategorias());
        categoriaComboBox.setItemLabelGenerator(Categoria::getNombre);
        categoriaComboBox.setPlaceholder("Selecciona una categoría");
        categoriaComboBox.setWidthFull();
        categoriaComboBox.getStyle()
                .set("--lumo-primary-color", "#D32F2F")
                .set("--vaadin-input-field-label-color", "#D32F2F")
                .set("--vaadin-input-field-focused-label-color", "#D32F2F");

        Button agregarCategoriaButton = new Button("Agregar Categoría", event -> agregarCategoria());
        agregarCategoriaButton.getStyle().set("background-color", "#D32F2F");
        agregarCategoriaButton.getStyle().set("color", "white");
        agregarCategoriaButton.setWidthFull();

        // Configurar Grid
        configurarGridCategorias();

        seccion.add(headerCategorias, categoriaComboBox, agregarCategoriaButton, gridCategorias);
        return seccion;
    }

    private void configurarGridCategorias() {
        gridCategorias.addColumn(Categoria::getNombre).setHeader("Categoría").setFlexGrow(2);

        gridCategorias.addComponentColumn(categoria -> {
            Button eliminarButton = new Button("Eliminar");
            eliminarButton.getStyle().set("color", "#D32F2F");
            eliminarButton.addClickListener(event -> {
                categoriasSeleccionadas.remove(categoria);
                gridCategorias.setItems(categoriasSeleccionadas);
            });
            return eliminarButton;
        }).setHeader("Acciones").setFlexGrow(1);

        gridCategorias.setHeight("200px");
        gridCategorias.setWidthFull();
    }

    private void agregarCategoria() {
        Categoria categoria = categoriaComboBox.getValue();

        if (categoria == null) {
            Notification.show("Selecciona una categoría").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        // Verificar si la categoría ya está agregada
        boolean yaExiste = categoriasSeleccionadas.stream()
                .anyMatch(cat -> cat.getIdCategoria().equals(categoria.getIdCategoria()));

        if (yaExiste) {
            Notification.show("Esta categoría ya ha sido agregada").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        categoriasSeleccionadas.add(categoria);
        gridCategorias.setItems(categoriasSeleccionadas);

        // Limpiar campos
        categoriaComboBox.clear();

        Notification.show("Categoría agregada").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

        // Configurar upload de imagen
        configurarUploadImagen();
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

            // Agregar la imagen si existe
            if (imagenBytes != null) {
                producto.setImagen(imagenBytes);
            }

            try {
                // Primero guardamos el producto
                Producto productoGuardado = productoService.guardarProducto(producto);

                // Luego asignamos los ingredientes
                for (IngredienteProductoDTO dto : ingredientesSeleccionados) {
                    productoIngredienteService.agregarIngredienteAProducto(
                            productoGuardado,
                            dto.ingrediente,
                            dto.cantidad
                    );
                }

                // Finalmente asignamos las categorias y persistimos la relación
                productoGuardado.setCategorias(new HashSet<>(categoriasSeleccionadas));
                productoService.guardarProducto(productoGuardado);

                Notification notification = Notification.show(
                        "Producto creado correctamente"
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
        categoriasSeleccionadas.clear();
        gridCategorias.setItems(categoriasSeleccionadas);

        if (uploadImagen != null) {
            uploadImagen.clearFileList();
        }
        imagenPreview.setSrc("");
        imagenBytes = null;
        imageBuffer = null;
    }
    
    // Clase interna para manejar ingredientes seleccionados
    private static class IngredienteProductoDTO {
        private final Ingrediente ingrediente;
        private final Float cantidad;
        
        public IngredienteProductoDTO(Ingrediente ingrediente, float cantidad) {
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

    private void configurarUploadImagen() {
        // Configuración de preview
        imagenPreview.setAlt("Vista previa de la imagen");
        imagenPreview.setWidth("200px");
        imagenPreview.setHeight("200px");
        imagenPreview.getStyle().set("object-fit", "cover");
        imagenPreview.getStyle().set("border-radius", "8px");

        // Configuración del componente Upload
        uploadImagen = new Upload();
        uploadImagen.setAcceptedFileTypes("image/jpeg", "image/png", "image/jpg");
        uploadImagen.setMaxFiles(1);
        uploadImagen.setMaxFileSize(5 * 1024 * 1024); // 5MB
        uploadImagen.setDropLabel(new com.vaadin.flow.component.html.Span("Arrastra la imagen aquí"));
        uploadImagen.setWidthFull();

        // Usar Receiver (InputStream -> OutputStream)
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

                Notification.show("Imagen cargada correctamente")
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception e) {
                Notification.show("Error al procesar la imagen: " + e.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        uploadImagen.addFileRejectedListener(event -> {
            Notification.show("Archivo rechazado: " + event.getErrorMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        uploadImagen.addFailedListener(event -> {
            String reason = event.getReason() != null ? event.getReason().getMessage() : "desconocida";
            Notification.show("Error al subir la imagen: " + reason)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
    }


}
