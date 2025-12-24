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
import com.vaadin.flow.component.grid.editor.Editor;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@Route(value = "empleados", layout = MainLayout.class)
@PageTitle("Gestión de Empleados - MercaESI")
public class GestionEmpleadosView extends VerticalLayout implements BeforeEnterObserver {

    private final EmpleadoService empleadoService;
    private final RolService rolService;
    private final SessionService sessionService;
    private Grid<Empleado> grid;

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
        titulo.getStyle().set("color", "#D32F2F");

        // Botón para crear nuevo empleado
        Button btnNuevo = new Button("Nuevo Empleado", new Icon(VaadinIcon.PLUS));
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNuevo.getStyle().set("background-color", "#D32F2F");
        btnNuevo.addClickListener(e -> abrirDialogoNuevo());

        HorizontalLayout headerLayout = new HorizontalLayout(titulo, btnNuevo);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);

        // Configurar Grid
        configurarGrid();

        add(headerLayout, grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar que el usuario sea administrador
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null || !empleado.esAdministrador()) {
            event.rerouteTo("");
            Notification.show("Acceso denegado. Solo administradores.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configurarGrid() {
        grid = new Grid<>(Empleado.class, false);
        grid.setHeight("600px");
        grid.setWidthFull();

        // Columnas
        grid.addColumn(Empleado::getIdEmpleado).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Empleado::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Empleado::getUsername).setHeader("Usuario").setSortable(true);
        grid.addColumn(Empleado::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(Empleado::getTelefono).setHeader("Teléfono");
        grid.addColumn(empleado -> {
            if (empleado.getRoles() != null && !empleado.getRoles().isEmpty()) {
                return empleado.getRoles().stream()
                        .map(Rol::getNombre)
                        .collect(Collectors.joining(", "));
            }
            return "Sin roles";
        }).setHeader("Roles").setSortable(false);

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

        // Cargar datos
        actualizarGrid();
    }

    private void actualizarGrid() {
        grid.setItems(empleadoService.listarEmpleados());
    }

    private void abrirDialogoNuevo() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Nuevo Empleado");
        dialog.setWidth("500px");

        // Formulario
        TextField nombreField = new TextField("Nombre");
        nombreField.setWidthFull();
        nombreField.setRequired(true);

        TextField usernameField = new TextField("Usuario");
        usernameField.setWidthFull();
        usernameField.setRequired(true);

        TextField emailField = new TextField("Email");
        emailField.setWidthFull();
        emailField.setRequired(true);

        TextField telefonoField = new TextField("Teléfono");
        telefonoField.setWidthFull();

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setWidthFull();
        passwordField.setRequired(true);

        MultiSelectComboBox<String> rolesCombo = new MultiSelectComboBox<>("Roles");
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
                Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set("background-color", "#D32F2F");

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

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
        TextField nombreField = new TextField("Nombre");
        nombreField.setValue(empleado.getNombre());
        nombreField.setWidthFull();

        TextField usernameField = new TextField("Usuario");
        usernameField.setValue(empleado.getUsername());
        usernameField.setWidthFull();

        TextField emailField = new TextField("Email");
        emailField.setValue(empleado.getEmail());
        emailField.setWidthFull();

        TextField telefonoField = new TextField("Teléfono");
        telefonoField.setValue(empleado.getTelefono() != null ? empleado.getTelefono() : "");
        telefonoField.setWidthFull();

        PasswordField passwordField = new PasswordField("Nueva Contraseña");
        passwordField.setWidthFull();
        passwordField.setHelperText("Dejar en blanco para mantener la actual");

        MultiSelectComboBox<String> rolesCombo = new MultiSelectComboBox<>("Roles");
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
                Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set("background-color", "#D32F2F");

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

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
                Notification.show("Error: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnConfirmar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCancelar = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(btnConfirmar, btnCancelar);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(contenido, buttonLayout);
        dialog.add(dialogLayout);
        dialog.open();
    }
}
