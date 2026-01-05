package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.service.ClienteService;
import com.example.iw20252026merca_esi.service.EmpleadoService;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "acceso", layout = SimpleLayout.class)
@PageTitle("Acceso - Login/Registro")
public class LoginRegisterView extends VerticalLayout {

    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final SessionService sessionService;
    
    private VerticalLayout loginPanel;
    private VerticalLayout registerPanel;

    private static final String COLOR6 = "--vaadin-input-field-focused-label-color";
    private static final String COLOR7 = "--vaadin-input-field-label-color";
    private static final String COLOR8 = "--lumo-primary-color";
    private static final String COLOR = "color";
    private static final String COLOR3 = "#D32F2F";

    @Autowired
    public LoginRegisterView(ClienteService clienteService, EmpleadoService empleadoService, SessionService sessionService) {
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
        this.sessionService = sessionService;
        
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setPadding(false);
        setSpacing(false);
        
        // Título principal con margen superior cero
        H2 titulo = new H2("Acceso de Usuario");
        titulo.getStyle()
                .set("margin-top", "0")
                .set("margin-bottom", "20px")
                .set(COLOR, COLOR3);
        
        // Crear las pestañas
        Tab loginTab = new Tab("Iniciar Sesión");
        Tab registerTab = new Tab("Registrarse");
        Tabs tabs = new Tabs(loginTab, registerTab);
        tabs.setWidthFull();
        
        // Crear los paneles
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        registerPanel.setVisible(false);
        
        // Cambiar entre paneles al cambiar de pestaña
        tabs.addSelectedChangeListener(event -> {
            if (event.getSelectedTab() == loginTab) {
                loginPanel.setVisible(true);
                registerPanel.setVisible(false);
            } else {
                loginPanel.setVisible(false);
                registerPanel.setVisible(true);
            }
        });
        
        // Wrapper para controlar el padding superior
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
    // Añadimos padding superior para que el header no solape el contenido
    wrapper.getStyle().set("padding-top", "80px");
        
        // Contenedor con ancho máximo
        VerticalLayout container = new VerticalLayout();
        container.setWidth("500px");
        container.setPadding(true);
        container.setSpacing(true);
        
        container.add(titulo, tabs, loginPanel, registerPanel);
        wrapper.add(container);
        
        add(wrapper);
    }
    
    private VerticalLayout createLoginPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(false);
        
        H2 titulo = new H2("Iniciar Sesión");
        titulo.getStyle().set(COLOR, COLOR3);
        
        TextField usernameField = new TextField("Usuario");
        usernameField.setWidthFull();
        usernameField.setRequired(true);
        usernameField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setWidthFull();
        passwordField.setRequired(true);
        passwordField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        Button loginButton = new Button("Entrar");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        loginButton.getStyle()
                .set("background-color", COLOR3)
                .set(COLOR, "white");
        
        loginButton.addClickListener(e -> {
            String username = usernameField.getValue().trim();
            String password = passwordField.getValue();
            
            if (username.isEmpty() || password.isEmpty()) {
                Notification.show("Por favor, complete todos los campos")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            
            // Intentar autenticar primero como cliente
            Optional<Cliente> clienteOpt = clienteService.autenticar(username, password);
            
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                
                // Guardar el cliente en la sesión
                sessionService.setCliente(cliente);
                
                Notification.show("¡Bienvenido, " + cliente.getNombre() + "!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                // Redirigir a la página principal
                UI.getCurrent().navigate("");
                return;
            }
            
            // Si no es cliente, intentar autenticar como empleado
            Optional<Empleado> empleadoOpt = empleadoService.autenticar(username, password);
            
            if (empleadoOpt.isPresent()) {
                Empleado empleado = empleadoOpt.get();
                
                // Guardar el empleado en la sesión
                sessionService.setEmpleado(empleado);
                
                Notification.show("¡Bienvenido, " + empleado.getNombre() + "!")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                // Redirigir a la página principal
                UI.getCurrent().navigate("");
            } else {
                Notification.show("Usuario o contraseña incorrectos")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        
        FormLayout form = new FormLayout();
        form.add(usernameField, passwordField);
        form.setWidthFull();
        
        layout.add(titulo, form, loginButton);
        layout.setWidthFull();
        
        return layout;
    }
    
    private VerticalLayout createRegisterPanel() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(false);
        
        H2 titulo = new H2("Crear Cuenta");
        titulo.getStyle().set(COLOR, COLOR3);
        
        TextField nombreField = new TextField("Nombre Completo");
        nombreField.setWidthFull();
        nombreField.setRequired(true);
        nombreField.setHelperText("Incluye nombre y apellidos");
        nombreField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        TextField emailField = new TextField("Email");
        emailField.setWidthFull();
        emailField.setRequired(true);
        emailField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        TextField telefonoField = new TextField("Teléfono");
        telefonoField.setWidthFull();
        telefonoField.setPlaceholder("Opcional");
        telefonoField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        TextField usernameField = new TextField("Usuario");
        usernameField.setWidthFull();
        usernameField.setRequired(true);
        usernameField.setHelperText("Mínimo 3 caracteres");
        usernameField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setWidthFull();
        passwordField.setRequired(true);
        passwordField.setHelperText("Mínimo 6 caracteres");
        passwordField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        PasswordField confirmPasswordField = new PasswordField("Confirmar Contraseña");
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequired(true);
        confirmPasswordField.getStyle()
                .set(COLOR8, COLOR3)
                .set(COLOR7, COLOR3)
                .set(COLOR6, COLOR3);
        
        Button registerButton = new Button("Registrarse");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.getStyle()
                .set("background-color", COLOR3)
                .set(COLOR, "white");
        
        registerButton.addClickListener(e -> {
            String nombre = nombreField.getValue().trim();
            String email = emailField.getValue().trim();
            String telefono = telefonoField.getValue().trim();
            String username = usernameField.getValue().trim();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();
            
            // Validaciones
            if (nombre.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Notification.show("Por favor, complete todos los campos obligatorios")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            
            if (username.length() < 3) {
                Notification.show("El usuario debe tener al menos 3 caracteres")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            
            if (password.length() < 6) {
                Notification.show("La contraseña debe tener al menos 6 caracteres")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Notification.show("Las contraseñas no coinciden")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            
            try {
                // Crear el objeto Cliente
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNombre(nombre);
                nuevoCliente.setEmail(email);
                nuevoCliente.setTelefono(telefono.isEmpty() ? null : telefono);
                nuevoCliente.setUsername(username);
                nuevoCliente.setContrasena(password);
                nuevoCliente.setPuntos(0); // Iniciar con 0 puntos
                
                // Registrar el cliente
                clienteService.registrarCliente(nuevoCliente);
                
                Notification notification = Notification.show(
                    "¡Registro exitoso! Ahora puedes iniciar sesión");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setDuration(3000);
                
                // Limpiar campos
                nombreField.clear();
                emailField.clear();
                telefonoField.clear();
                usernameField.clear();
                passwordField.clear();
                confirmPasswordField.clear();
                
            } catch (RuntimeException ex) {
                Notification.show(ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        
        FormLayout form = new FormLayout();
        form.add(nombreField, emailField, telefonoField, 
                usernameField, passwordField, confirmPasswordField);
        form.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1)
        );
        form.setWidthFull();
        
        layout.add(titulo, form, registerButton);
        layout.setWidthFull();
        
        return layout;
    }
}
