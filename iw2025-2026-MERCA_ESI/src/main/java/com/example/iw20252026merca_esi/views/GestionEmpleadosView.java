package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.Rol;
import com.example.iw20252026merca_esi.service.EmpleadoService;
import com.example.iw20252026merca_esi.service.RolService;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.stream.Collectors;

@Route(value = "empleados", layout = MainLayout.class)
@PageTitle("Gestión de Empleados - MercaESI")
@RolesAllowed("ADMINISTRADOR")
public class GestionEmpleadosView extends VerticalLayout implements BeforeEnterObserver {

    private final EmpleadoService empleadoService;
    private final RolService rolService;
    private final SessionService sessionService;
    private Grid<Empleado> grid;
    private TextField searchField;

    private static final String COLOR2 = "#D32F2F";
    private static final String BACKGROUND_COLOR = "background-color";
    private static final String NOMBRE = "Nombre";
    private static final String USUARIO = "Usuario";
    private static final String EMAIL = "Email";
    private static final String TELEFONO = "Teléfono";
    private static final String ROLES = "Roles";
    private static final String ERROR = "Error: ";
    private static final String CANCELAR = "Cancelar";

    @Autowired
    public GestionEmpleadosView(EmpleadoService empleadoService, RolService rolService, SessionService sessionService) {
        this.empleadoService = empleadoService;
        this.rolService = rolService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Título
        H2 titulo = new H2("Gestión de Empleados");
        titulo.getStyle().set("color", COLOR2);

        // Botón para crear nuevo empleado
        Button btnNuevo = new Button("Nuevo Empleado", new Icon(VaadinIcon.PLUS));
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNuevo.getStyle().set(BACKGROUND_COLOR, COLOR2);
        btnNuevo.addClickListener(e -> abrirDialogoNuevo());

        HorizontalLayout headerLayout = new HorizontalLayout(titulo, btnNuevo);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);

        // Buscador
        searchField = new TextField();
        searchField.setPlaceholder("Buscar por nombre, usuario o email...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidthFull();
        searchField.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> actualizarGrid());

        // Configurar Grid
        configurarGrid();

        add(headerLayout, searchField, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar que el usuario esté autenticado
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            event.rerouteTo("");
            Notification.show("Acceso denegado.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configurarGrid() {
        grid = new Grid<>(Empleado.class, false);
        grid.setHeight("600px");
        grid.setWidthFull();

        // Columnas
        grid.addColumn(Empleado::getIdEmpleado).setHeader("ID").setWidth("80px").setFlexGrow(0).setSortProperty("idEmpleado");
        grid.addColumn(Empleado::getNombre).setHeader(NOMBRE).setSortable(true).setSortProperty(NOMBRE);
        grid.addColumn(Empleado::getUsername).setHeader(USUARIO).setSortable(true).setSortProperty("username");
        grid.addColumn(Empleado::getEmail).setHeader(EMAIL).setSortable(true).setSortProperty(EMAIL);
        grid.addColumn(Empleado::getTelefono).setHeader(TELEFONO);
        grid.addColumn(empleado -> {
            if (empleado.getRoles() != null && !empleado.getRoles().isEmpty()) {
                return empleado.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.joining(", "));
            }
            return "Sin roles";
        }).setHeader(ROLES).setSortable(false);

        // Columna de acciones
        grid.addComponentColumn(empleado -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button btnEditar = new Button(new Icon(VaadinIcon.EDIT));
            btnEditar.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            btnEditar.addClickListener(e -> abrirDialogoEditar(empleado));

            Button btnEliminar = new Button(new Icon(VaadinIcon.TRASH));
            btnEliminar.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            btnEliminar.addClickListener(e -> confirmarEliminar(empleado));

            actions.add(btnEditar, btnEliminar);
            return actions;
        }).setHeader("Acciones").setWidth("150px").setFlexGrow(0);

        // Cargar datos con lazy loading
        configurarLazyDataProvider();
    }

    private void configurarLazyDataProvider() {
        String filtro = searchField.getValue();
        
        CallbackDataProvider<Empleado, Void> dataProvider = DataProvider.fromCallbacks(
            // Fetch callback: carga solo los datos necesarios para la página actual
            query -> {
                int page = query.getPage();
                int size = query.getPageSize();
                
                // Construir ordenamiento dinámico desde el Grid
                Sort sort = Sort.by("idEmpleado").descending(); // Default
                if (!query.getSortOrders().isEmpty()) {
                    var sortOrder = query.getSortOrders().get(0);
                    String property = sortOrder.getSorted();
                    Sort.Direction direction = sortOrder.getDirection() == com.vaadin.flow.data.provider.SortDirection.ASCENDING 
                        ? Sort.Direction.ASC 
                        : Sort.Direction.DESC;
                    sort = Sort.by(direction, property);
                }
                
                PageRequest pageRequest = PageRequest.of(page, size, sort);
                
                if (filtro == null || filtro.trim().isEmpty()) {
                    return empleadoService.listarEmpleadosPaginados(pageRequest).stream();
                } else {
                    // Para búsqueda, seguimos usando el método anterior (ya optimizado con índices)
                    String filtroLower = filtro.toLowerCase();
                    return empleadoService.listarEmpleados().stream()
                        .filter(empleado -> 
                            empleado.getNombre().toLowerCase().contains(filtroLower) ||
                            empleado.getUsername().toLowerCase().contains(filtroLower) ||
                            (empleado.getEmail() != null && empleado.getEmail().toLowerCase().contains(filtroLower))
                        )
                        .skip(query.getOffset())
                        .limit(query.getLimit());
                }
            },
            // Count callback: cuenta el total de registros
            query -> {
                if (filtro == null || filtro.trim().isEmpty()) {
                    return (int) empleadoService.contarEmpleados();
                } else {
                    String filtroLower = filtro.toLowerCase();
                    return (int) empleadoService.listarEmpleados().stream()
                        .filter(empleado -> 
                            empleado.getNombre().toLowerCase().contains(filtroLower) ||
                            empleado.getUsername().toLowerCase().contains(filtroLower) ||
                            (empleado.getEmail() != null && empleado.getEmail().toLowerCase().contains(filtroLower))
                        )
                        .count();
                }
            }
        );
        
        grid.setDataProvider(dataProvider);
    }

    private void actualizarGrid() {
        // Reconfigurar el data provider con el filtro actualizado
        configurarLazyDataProvider();
    }

    private void abrirDialogoNuevo() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nuevo Empleado");
        dialog.setWidth("500px");

        // Formulario
        TextField nombreField = new TextField(NOMBRE);
        nombreField.setWidthFull();
        nombreField.setRequired(true);

        TextField usernameField = new TextField(USUARIO);
        usernameField.setWidthFull();
        usernameField.setRequired(true);

        TextField emailField = new TextField(EMAIL);
        emailField.setWidthFull();
        emailField.setRequired(true);

        TextField telefonoField = new TextField(TELEFONO);
        telefonoField.setWidthFull();

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setWidthFull();
        passwordField.setRequired(true);

        MultiSelectComboBox<String> rolesCombo = new MultiSelectComboBox<>(ROLES);
        rolesCombo.setWidthFull();
        rolesCombo.setItems(rolService.listarRoles().stream()
                .map(Rol::getNombre)
                .toList());
        rolesCombo.setHelperText("Seleccione uno o más roles");

        FormLayout formLayout = new FormLayout(nombreField, usernameField, emailField, telefonoField, passwordField, rolesCombo);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Botones
        Button btnGuardar = new Button("Guardar", e -> {
            try {
                Empleado nuevoEmpleado = new Empleado();
                nuevoEmpleado.setNombre(nombreField.getValue());
                nuevoEmpleado.setUsername(usernameField.getValue());
                nuevoEmpleado.setEmail(emailField.getValue());
                nuevoEmpleado.setTelefono(telefonoField.getValue().isEmpty() ? null : telefonoField.getValue());
                nuevoEmpleado.setContrasena(passwordField.getValue());

                empleadoService.registrarEmpleado(nuevoEmpleado);

                // Asignar roles si se especificaron
                if (!rolesCombo.getValue().isEmpty()) {
                    for (String rol : rolesCombo.getValue()) {
                        try {
                            empleadoService.asignarRolAEmpleado(nuevoEmpleado.getIdEmpleado(), rol.toUpperCase());
                        } catch (Exception ex) {
                            // Ignorar si el rol no existe
                        }
                    }
                }

                actualizarGrid();
                dialog.close();
                Notification.show("Empleado creado exitosamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show(ERROR + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set(BACKGROUND_COLOR, COLOR2);

        Button btnCancelar = new Button(CANCELAR, e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(btnGuardar, btnCancelar);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void abrirDialogoEditar(Empleado empleado) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Empleado");
        dialog.setWidth("500px");

        // Formulario
        TextField nombreField = new TextField(NOMBRE);
        nombreField.setValue(empleado.getNombre());
        nombreField.setWidthFull();

        TextField usernameField = new TextField(USUARIO);
        usernameField.setValue(empleado.getUsername());
        usernameField.setWidthFull();

        TextField emailField = new TextField(EMAIL);
        emailField.setValue(empleado.getEmail());
        emailField.setWidthFull();

        TextField telefonoField = new TextField(TELEFONO);
        telefonoField.setValue(empleado.getTelefono() != null ? empleado.getTelefono() : "");
        telefonoField.setWidthFull();

        PasswordField passwordField = new PasswordField("Nueva Contraseña");
        passwordField.setWidthFull();
        passwordField.setHelperText("Dejar en blanco para mantener la actual");

        MultiSelectComboBox<String> rolesCombo = new MultiSelectComboBox<>(ROLES);
        rolesCombo.setWidthFull();
        rolesCombo.setItems(rolService.listarRoles().stream()
                .map(Rol::getNombre)
                .toList());
        if (empleado.getRoles() != null && !empleado.getRoles().isEmpty()) {
            rolesCombo.setValue(empleado.getRoles().stream()
                    .map(Rol::getNombre)
                    .collect(Collectors.toSet()));
        }
        rolesCombo.setHelperText("Seleccione uno o más roles");

        FormLayout formLayout = new FormLayout(nombreField, usernameField, emailField, telefonoField, passwordField, rolesCombo);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Botones
        Button btnGuardar = new Button("Guardar", e -> {
            try {
                Empleado empleadoActualizado = new Empleado();
                empleadoActualizado.setNombre(nombreField.getValue());
                empleadoActualizado.setUsername(usernameField.getValue());
                empleadoActualizado.setEmail(emailField.getValue());
                empleadoActualizado.setTelefono(telefonoField.getValue().isEmpty() ? null : telefonoField.getValue());
                
                if (!passwordField.getValue().isEmpty()) {
                    empleadoActualizado.setContrasena(passwordField.getValue());
                }

                empleadoService.actualizarEmpleado(empleado.getIdEmpleado(), empleadoActualizado);

                // Actualizar roles
                if (!rolesCombo.getValue().isEmpty()) {
                    java.util.Set<String> rolesSet = new java.util.HashSet<>(rolesCombo.getValue());
                    empleadoService.establecerRolesDeEmpleado(empleado.getIdEmpleado(), rolesSet);
                }

                actualizarGrid();
                dialog.close();
                Notification.show("Empleado actualizado exitosamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show(ERROR + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set(BACKGROUND_COLOR, COLOR2);

        Button btnCancelar = new Button(CANCELAR, e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(btnGuardar, btnCancelar);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(formLayout, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void confirmarEliminar(Empleado empleado) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirmar eliminación");
        
        Div contenido = new Div();
        contenido.setText("¿Está seguro que desea eliminar al empleado '" + empleado.getNombre() + "'?");
        contenido.getStyle().set("padding", "20px");

        Button btnConfirmar = new Button("Eliminar", e -> {
            try {
                empleadoService.eliminarEmpleado(empleado.getIdEmpleado());
                actualizarGrid();
                dialog.close();
                Notification.show("Empleado eliminado exitosamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show(ERROR + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCancelar = new Button(CANCELAR, e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(btnConfirmar, btnCancelar);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(contenido, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }
}
