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
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "perfil", layout = MainLayout.class)
@PageTitle("Perfil de Usuario")
public class PerfilView extends VerticalLayout {

    private final SessionService sessionService;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    
    private VerticalLayout contenedorPrincipal;
    private boolean modoEdicion = false;

    private static final String BACKGROUND = "background";
    private static final String BORDER_RADIUS = "border-radius";
    private static final String MARGIN_TOP = "margin-top";
    private static final String EDITAR_PERFIL = "Editar Perfil";
    private static final String B_COLOR = "background-color";
    private static final String COLOR1 = "#D32F2F";
    private static final String COLOR3 = "white";
    private static final String PIXEL_TAM = "12px 24px";
    private static final String PADDING = "padding";
    private static final String COLOR = "color";
    private static final String COLOR2 = "#1976D2";
    private static final String BORDER_COLOR = "border-color";
    private static final String MARGIN_BOTTOM = "margin-bottom" ;
    private static final String CENTER = "center";
    private static final String FONT_SIZE = "font-size";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String MARGIN = "margin";
    private static final String COLOR7 = "0 0 20px 0";
    private static final String USUARIO = "Usuario";
    private static final String EMAIL = "Email";
    private static final String TELEFONO = "Teléfono";
    @Autowired
    public PerfilView(SessionService sessionService, ClienteService clienteService, EmpleadoService empleadoService) {
        this.sessionService = sessionService;
        this.clienteService = clienteService;
        this.empleadoService = empleadoService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        setPadding(false);
        setSpacing(false);
        
        // Wrapper con padding superior
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.getStyle().set("padding-top", "80px");
        
        // Contenedor principal
        contenedorPrincipal = new VerticalLayout();
        contenedorPrincipal.setWidth("800px");
        contenedorPrincipal.setPadding(true);
        contenedorPrincipal.setSpacing(true);
        contenedorPrincipal.getStyle()
                .set(BACKGROUND, "linear-gradient(135deg, #ffffff 0%, #f5f5f5 100%)")
                .set(BORDER_RADIUS, "16px")
                .set("box-shadow", "0 8px 32px rgba(0,0,0,0.1)");
        
        // Verificar usuario logueado
        Cliente cliente = sessionService.getCliente();
        Empleado empleado = sessionService.getEmpleado();
        
        if (cliente != null) {
            mostrarPerfilCliente(cliente);
        } else if (empleado != null) {
            mostrarPerfilEmpleado(empleado);
        } else {
            mostrarPantallaLogin();
        }
        
        wrapper.add(contenedorPrincipal);
        add(wrapper);
    }

    private void mostrarPerfilCliente(Cliente cliente) {
        contenedorPrincipal.removeAll();
        
        // Header con avatar e información básica
        VerticalLayout header = crearHeaderPerfil(cliente.getNombre(), "CLIENTE", cliente.getPuntos());
        
        // Vista de solo lectura
        VerticalLayout vistaLectura = crearVistaLecturaCliente(cliente);
        
        // Botones de acción
        HorizontalLayout botonesAccion = new HorizontalLayout();
        botonesAccion.setWidthFull();
        botonesAccion.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        botonesAccion.setSpacing(true);
        botonesAccion.getStyle().set(MARGIN_TOP, "20px");
        
        Button btnEditar = new Button(EDITAR_PERFIL, new Icon(VaadinIcon.EDIT));
        btnEditar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEditar.getStyle()
                .set(B_COLOR, COLOR1)
                .set(BORDER_RADIUS, "8px")
                .set(PADDING, PIXEL_TAM);
        btnEditar.addClickListener(e -> mostrarFormularioEdicionCliente(cliente));
        
        Button btnPrivacidad = new Button("Privacidad", new Icon(VaadinIcon.SHIELD));
        btnPrivacidad.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnPrivacidad.getStyle()
                .set(COLOR, COLOR2)
                .set(BORDER_COLOR, COLOR2)
                .set(BORDER_RADIUS, "8px")
                .set(PADDING, PIXEL_TAM);
        btnPrivacidad.addClickListener(e -> UI.getCurrent().navigate("privacidad"));
        
        Button btnCerrarSesion = new Button("Cerrar Sesión", new Icon(VaadinIcon.SIGN_OUT));
        btnCerrarSesion.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCerrarSesion.getStyle()
                .set(COLOR, COLOR1)
                .set(BORDER_RADIUS, "8px")
                .set(PADDING, PIXEL_TAM);
        btnCerrarSesion.addClickListener(e -> {
            sessionService.logout();
            UI.getCurrent().navigate("");
            UI.getCurrent().getPage().reload();
        });
        
        botonesAccion.add(btnEditar, btnPrivacidad, btnCerrarSesion);
        
        contenedorPrincipal.add(header, vistaLectura, botonesAccion);
    }

    private void mostrarPerfilEmpleado(Empleado empleado) {
        contenedorPrincipal.removeAll();
        
        // Header con avatar e información básica
        String rolesTexto = empleado.getRoles() != null && !empleado.getRoles().isEmpty()
                ? empleado.getRoles().stream().map(r -> r.getNombre()).reduce((a, b) -> a + ", " + b).orElse("EMPLEADO")
                : "EMPLEADO";
        VerticalLayout header = crearHeaderPerfil(empleado.getNombre(), rolesTexto, null);
        
        // Vista de solo lectura
        VerticalLayout vistaLectura = crearVistaLecturaEmpleado(empleado);
        
        // Botones de acción
        HorizontalLayout botonesAccion = new HorizontalLayout();
        botonesAccion.setWidthFull();
        botonesAccion.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        botonesAccion.setSpacing(true);
        botonesAccion.getStyle().set(MARGIN_TOP, "20px");
        
        Button btnEditar = new Button(EDITAR_PERFIL, new Icon(VaadinIcon.EDIT));
        btnEditar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEditar.getStyle()
                .set(B_COLOR, COLOR1)
                .set(BORDER_RADIUS, "8px")
                .set(PADDING, PIXEL_TAM);
        btnEditar.addClickListener(e -> mostrarFormularioEdicionEmpleado(empleado));
        
        Button btnPrivacidad = new Button("Privacidad", new Icon(VaadinIcon.SHIELD));
        btnPrivacidad.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnPrivacidad.getStyle()
                .set(COLOR, COLOR2)
                .set(BORDER_COLOR, COLOR2)
                .set(BORDER_RADIUS, "8px")
                .set(PADDING, PIXEL_TAM);
        btnPrivacidad.addClickListener(e -> UI.getCurrent().navigate("privacidad"));
        
        Button btnCerrarSesion = new Button("Cerrar Sesión", new Icon(VaadinIcon.SIGN_OUT));
        btnCerrarSesion.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnCerrarSesion.getStyle()
                .set(COLOR, COLOR1)
                .set(BORDER_RADIUS, "8px")
                .set(PADDING, PIXEL_TAM);
        btnCerrarSesion.addClickListener(e -> {
            sessionService.logout();
            UI.getCurrent().navigate("");
            UI.getCurrent().getPage().reload();
        });
        
        botonesAccion.add(btnEditar, btnPrivacidad, btnCerrarSesion);
        
        contenedorPrincipal.add(header, vistaLectura, botonesAccion);
    }

    private VerticalLayout crearHeaderPerfil(String nombre, String tipo, Integer puntos) {
        VerticalLayout header = new VerticalLayout();
        header.setAlignItems(Alignment.CENTER);
        header.setSpacing(false);
        header.setPadding(false);
        header.getStyle()
                .set(BACKGROUND, "linear-gradient(135deg, #D32F2F 0%, #B71C1C 100%)")
                .set(COLOR, COLOR3)
                .set(BORDER_RADIUS, "12px")
                .set(PADDING, "30px")
                .set(MARGIN_BOTTOM, "20px")
                .set("width", "100%");
        
        // Avatar círculo con inicial
        Div avatar = new Div();
        avatar.setText(nombre.substring(0, 1).toUpperCase());
        avatar.getStyle()
                .set("width", "80px")
                .set("height", "80px")
                .set(BORDER_RADIUS, "50%")
                .set(B_COLOR, "rgba(255,255,255,0.3)")
                .set("display", "flex")
                .set("align-items", CENTER)
                .set("justify-content", CENTER)
                .set(FONT_SIZE, "36px")
                .set(FONT_WEIGHT, "bold")
                .set(MARGIN_BOTTOM, "15px");
        
        H2 nombreTitle = new H2(nombre);
        nombreTitle.getStyle()
                .set(MARGIN, "0")
                .set(COLOR, COLOR3)
                .set(FONT_SIZE, "28px");
        
        Span tipoSpan = new Span(tipo);
        tipoSpan.getStyle()
                .set(BACKGROUND, "rgba(255,255,255,0.2)")
                .set(PADDING, "5px 15px")
                .set(BORDER_RADIUS, "20px")
                .set(FONT_SIZE, "14px")
                .set(MARGIN_TOP, "10px");
        
        header.add(avatar, nombreTitle, tipoSpan);
        
        if (puntos != null) {
            Div puntosDiv = new Div();
            Icon starIcon = new Icon(VaadinIcon.STAR);
            starIcon.setColor("#FFD700");
            starIcon.getStyle().set("margin-right", "5px");
            Span puntosSpan = new Span(puntos + " Puntos");
            puntosSpan.getStyle().set(FONT_SIZE, "18px").set(FONT_WEIGHT, "bold");
            
            HorizontalLayout puntosLayout = new HorizontalLayout(starIcon, puntosSpan);
            puntosLayout.setAlignItems(Alignment.CENTER);
            puntosLayout.getStyle()
                    .set(BACKGROUND, "rgba(255,215,0,0.2)")
                    .set(PADDING, "10px 20px")
                    .set(BORDER_RADIUS, "25px")
                    .set(MARGIN_TOP, "15px");
            
            header.add(puntosLayout);
        }
        
        return header;
    }

    private VerticalLayout crearVistaLecturaCliente(Cliente cliente) {
        VerticalLayout vista = new VerticalLayout();
        vista.setSpacing(false);
        vista.setPadding(false);
        vista.setWidthFull();
        
        H3 titulo = new H3("Información Personal");
        titulo.getStyle()
                .set(COLOR, "#333")
                .set(MARGIN, COLOR7)
                .set(FONT_SIZE, "20px");
        
        vista.add(titulo);
        vista.add(crearCampoInfo(USUARIO, cliente.getUsername(), VaadinIcon.USER));
        vista.add(crearCampoInfo(EMAIL, cliente.getEmail(), VaadinIcon.ENVELOPE));
        vista.add(crearCampoInfo(TELEFONO, cliente.getTelefono() != null ? cliente.getTelefono() : "No especificado", VaadinIcon.PHONE));
        
        return vista;
    }

    private VerticalLayout crearVistaLecturaEmpleado(Empleado empleado) {
        VerticalLayout vista = new VerticalLayout();
        vista.setSpacing(false);
        vista.setPadding(false);
        vista.setWidthFull();
        
        H3 titulo = new H3("Información Personal");
        titulo.getStyle()
                .set(COLOR, "#333")
                .set(MARGIN, COLOR7)
                .set(FONT_SIZE, "20px");
        
        vista.add(titulo);
        vista.add(crearCampoInfo(USUARIO, empleado.getUsername(), VaadinIcon.USER));
        vista.add(crearCampoInfo(EMAIL, empleado.getEmail(), VaadinIcon.ENVELOPE));
        vista.add(crearCampoInfo(TELEFONO, empleado.getTelefono() != null ? empleado.getTelefono() : "No especificado", VaadinIcon.PHONE));
        
        return vista;
    }

    private HorizontalLayout crearCampoInfo(String etiqueta, String valor, VaadinIcon iconoEnum) {
        HorizontalLayout campo = new HorizontalLayout();
        campo.setWidthFull();
        campo.setAlignItems(Alignment.CENTER);
        campo.setSpacing(true);
        campo.getStyle()
                .set(BACKGROUND, COLOR3)
                .set(PADDING, "15px 20px")
                .set(BORDER_RADIUS, "8px")
                .set(MARGIN_BOTTOM, "10px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)");
        
        Icon icono = new Icon(iconoEnum);
        icono.setColor(COLOR1);
        icono.getStyle()
                .set("min-width", "24px")
                .set("margin-right", "10px");
        
        VerticalLayout textos = new VerticalLayout();
        textos.setSpacing(false);
        textos.setPadding(false);
        
        Span labelSpan = new Span(etiqueta);
        labelSpan.getStyle()
                .set(FONT_SIZE, "12px")
                .set(COLOR, "#666")
                .set(FONT_WEIGHT, "500");
        
        Span valorSpan = new Span(valor);
        valorSpan.getStyle()
                .set(FONT_SIZE, "16px")
                .set(COLOR, "#333")
                .set(FONT_WEIGHT, "600");
        
        textos.add(labelSpan, valorSpan);
        campo.add(icono, textos);
        
        return campo;
    }

    private void mostrarFormularioEdicionCliente(Cliente cliente) {
        contenedorPrincipal.removeAll();
        
        H2 titulo = new H2(EDITAR_PERFIL);
        titulo.getStyle()
                .set(COLOR, COLOR1)
                .set(MARGIN, COLOR7);
        
        // Formulario de datos personales
        H3 subtituloDatos = new H3("Datos Personales");
        subtituloDatos.getStyle().set(COLOR, "#666").set(MARGIN_TOP, "0");
        
        TextField nombreField = new TextField("Nombre Completo");
        nombreField.setValue(cliente.getNombre() != null ? cliente.getNombre() : "");
        nombreField.setWidthFull();
        nombreField.setPrefixComponent(new Icon(VaadinIcon.USER));
        
        TextField usernameField = new TextField(USUARIO);
        usernameField.setValue(cliente.getUsername() != null ? cliente.getUsername() : "");
        usernameField.setWidthFull();
        usernameField.setPrefixComponent(new Icon(VaadinIcon.USER_CARD));
        
        TextField emailField = new TextField(EMAIL);
        emailField.setValue(cliente.getEmail() != null ? cliente.getEmail() : "");
        emailField.setWidthFull();
        emailField.setPrefixComponent(new Icon(VaadinIcon.ENVELOPE));
        
        TextField telefonoField = new TextField(TELEFONO);
        telefonoField.setValue(cliente.getTelefono() != null ? cliente.getTelefono() : "");
        telefonoField.setWidthFull();
        telefonoField.setPrefixComponent(new Icon(VaadinIcon.PHONE));
        
        FormLayout formDatos = new FormLayout(nombreField, usernameField, emailField, telefonoField);
        formDatos.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        
        // Formulario de cambio de contraseña
        H3 subtituloPassword = new H3("Cambiar Contraseña (Opcional)");
        subtituloPassword.getStyle().set(COLOR, "#666").set(MARGIN_TOP, "20px");
        
        Paragraph infoPassword = new Paragraph("Para cambiar tu contraseña, primero debes ingresar la actual.");
        infoPassword.getStyle()
                .set(COLOR, "#666")
                .set(FONT_SIZE, "14px")
                .set(MARGIN, "0 0 15px 0");
        
        PasswordField contrasenaActualField = new PasswordField("Contraseña Actual");
        contrasenaActualField.setWidthFull();
        contrasenaActualField.setPrefixComponent(new Icon(VaadinIcon.LOCK));
        
        PasswordField contrasenaNuevaField = new PasswordField("Nueva Contraseña");
        contrasenaNuevaField.setWidthFull();
        contrasenaNuevaField.setPrefixComponent(new Icon(VaadinIcon.KEY));
        contrasenaNuevaField.setHelperText("Mínimo 6 caracteres");
        
        PasswordField contrasenaConfirmarField = new PasswordField("Confirmar Nueva Contraseña");
        contrasenaConfirmarField.setWidthFull();
        contrasenaConfirmarField.setPrefixComponent(new Icon(VaadinIcon.KEY));
        
        FormLayout formPassword = new FormLayout(contrasenaActualField, contrasenaNuevaField, contrasenaConfirmarField);
        formPassword.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        
        // Botones de acción
        HorizontalLayout botonesLayout = new HorizontalLayout();
        botonesLayout.setWidthFull();
        botonesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        botonesLayout.setSpacing(true);
        botonesLayout.getStyle().set(MARGIN_TOP, "30px");
        
        Button btnGuardar = new Button("Guardar Cambios", new Icon(VaadinIcon.CHECK), e -> {
            try {
                if (nombreField.getValue().trim().isEmpty()) {
                    Notification.show("El nombre es requerido").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                
                boolean cambiarPassword = !contrasenaNuevaField.getValue().isEmpty();
                
                if (cambiarPassword) {
                    if (contrasenaActualField.getValue().isEmpty()) {
                        Notification.show("Debe ingresar su contraseña actual para cambiarla")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    if (!clienteService.autenticar(cliente.getUsername(), contrasenaActualField.getValue()).isPresent()) {
                        Notification.show("La contraseña actual es incorrecta")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    if (!contrasenaNuevaField.getValue().equals(contrasenaConfirmarField.getValue())) {
                        Notification.show("Las contraseñas nuevas no coinciden")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    if (contrasenaNuevaField.getValue().length() < 6) {
                        Notification.show("La contraseña debe tener al menos 6 caracteres")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                }
                
                Cliente clienteActualizado = new Cliente();
                clienteActualizado.setNombre(nombreField.getValue());
                clienteActualizado.setUsername(usernameField.getValue());
                clienteActualizado.setEmail(emailField.getValue());
                clienteActualizado.setTelefono(telefonoField.getValue().isEmpty() ? null : telefonoField.getValue());
                
                if (cambiarPassword) {
                    clienteActualizado.setContrasena(contrasenaNuevaField.getValue());
                }
                
                Cliente actualizado = clienteService.actualizarCliente(cliente.getIdCliente(), clienteActualizado);
                sessionService.setCliente(actualizado);
                
                Notification.show("¡Perfil actualizado correctamente!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                UI.getCurrent().getPage().reload();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnGuardar.getStyle()
                .set(B_COLOR, "#4CAF50")
                .set(BORDER_RADIUS, "8px");
        
        Button btnCancelar = new Button("Cancelar", new Icon(VaadinIcon.CLOSE), e -> {
            mostrarPerfilCliente(cliente);
        });
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        btnCancelar.getStyle()
                .set(COLOR, "#666")
                .set(BORDER_RADIUS, "8px");
        
        botonesLayout.add(btnGuardar, btnCancelar);
        
        contenedorPrincipal.add(titulo, subtituloDatos, formDatos, subtituloPassword, infoPassword, formPassword, botonesLayout);
    }

    private void mostrarFormularioEdicionEmpleado(Empleado empleado) {
        contenedorPrincipal.removeAll();
        
        H2 titulo = new H2(EDITAR_PERFIL);
        titulo.getStyle()
                .set(COLOR, COLOR1)
                .set(MARGIN, COLOR7);
        
        // Formulario de datos personales
        H3 subtituloDatos = new H3("Datos Personales");
        subtituloDatos.getStyle().set(COLOR, "#666").set(MARGIN_TOP, "0");
        
        TextField nombreField = new TextField("Nombre Completo");
        nombreField.setValue(empleado.getNombre() != null ? empleado.getNombre() : "");
        nombreField.setWidthFull();
        nombreField.setPrefixComponent(new Icon(VaadinIcon.USER));
        
        TextField usernameField = new TextField(USUARIO);
        usernameField.setValue(empleado.getUsername() != null ? empleado.getUsername() : "");
        usernameField.setWidthFull();
        usernameField.setPrefixComponent(new Icon(VaadinIcon.USER_CARD));
        
        TextField emailField = new TextField(EMAIL);
        emailField.setValue(empleado.getEmail() != null ? empleado.getEmail() : "");
        emailField.setWidthFull();
        emailField.setPrefixComponent(new Icon(VaadinIcon.ENVELOPE));
        
        TextField telefonoField = new TextField(TELEFONO);
        telefonoField.setValue(empleado.getTelefono() != null ? empleado.getTelefono() : "");
        telefonoField.setWidthFull();
        telefonoField.setPrefixComponent(new Icon(VaadinIcon.PHONE));
        
        FormLayout formDatos = new FormLayout(nombreField, usernameField, emailField, telefonoField);
        formDatos.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        
        // Formulario de cambio de contraseña
        H3 subtituloPassword = new H3("Cambiar Contraseña (Opcional)");
        subtituloPassword.getStyle().set(COLOR, "#666").set(MARGIN_TOP, "20px");
        
        Paragraph infoPassword = new Paragraph("Para cambiar tu contraseña, primero debes ingresar la actual.");
        infoPassword.getStyle()
                .set(COLOR, "#666")
                .set(FONT_SIZE, "14px")
                .set(MARGIN, "0 0 15px 0");
        
        PasswordField contrasenaActualField = new PasswordField("Contraseña Actual");
        contrasenaActualField.setWidthFull();
        contrasenaActualField.setPrefixComponent(new Icon(VaadinIcon.LOCK));
        
        PasswordField contrasenaNuevaField = new PasswordField("Nueva Contraseña");
        contrasenaNuevaField.setWidthFull();
        contrasenaNuevaField.setPrefixComponent(new Icon(VaadinIcon.KEY));
        contrasenaNuevaField.setHelperText("Mínimo 6 caracteres");
        
        PasswordField contrasenaConfirmarField = new PasswordField("Confirmar Nueva Contraseña");
        contrasenaConfirmarField.setWidthFull();
        contrasenaConfirmarField.setPrefixComponent(new Icon(VaadinIcon.KEY));
        
        FormLayout formPassword = new FormLayout(contrasenaActualField, contrasenaNuevaField, contrasenaConfirmarField);
        formPassword.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        
        // Botones de acción
        HorizontalLayout botonesLayout = new HorizontalLayout();
        botonesLayout.setWidthFull();
        botonesLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        botonesLayout.setSpacing(true);
        botonesLayout.getStyle().set(MARGIN_TOP, "30px");
        
        Button btnGuardar = new Button("Guardar Cambios", new Icon(VaadinIcon.CHECK), e -> {
            try {
                if (nombreField.getValue().trim().isEmpty()) {
                    Notification.show("El nombre es requerido").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }
                
                boolean cambiarPassword = !contrasenaNuevaField.getValue().isEmpty();
                
                if (cambiarPassword) {
                    if (contrasenaActualField.getValue().isEmpty()) {
                        Notification.show("Debe ingresar su contraseña actual para cambiarla")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    if (!empleadoService.autenticar(empleado.getUsername(), contrasenaActualField.getValue()).isPresent()) {
                        Notification.show("La contraseña actual es incorrecta")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    if (!contrasenaNuevaField.getValue().equals(contrasenaConfirmarField.getValue())) {
                        Notification.show("Las contraseñas nuevas no coinciden")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                    
                    if (contrasenaNuevaField.getValue().length() < 6) {
                        Notification.show("La contraseña debe tener al menos 6 caracteres")
                                .addThemeVariants(NotificationVariant.LUMO_ERROR);
                        return;
                    }
                }
                
                Empleado empleadoActualizado = new Empleado();
                empleadoActualizado.setNombre(nombreField.getValue());
                empleadoActualizado.setUsername(usernameField.getValue());
                empleadoActualizado.setEmail(emailField.getValue());
                empleadoActualizado.setTelefono(telefonoField.getValue().isEmpty() ? null : telefonoField.getValue());
                
                if (cambiarPassword) {
                    empleadoActualizado.setContrasena(contrasenaNuevaField.getValue());
                }
                
                Empleado actualizado = empleadoService.actualizarEmpleado(empleado.getIdEmpleado(), empleadoActualizado);
                sessionService.setEmpleado(actualizado);
                
                Notification.show("¡Perfil actualizado correctamente!", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                UI.getCurrent().getPage().reload();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnGuardar.getStyle()
                .set(B_COLOR, "#4CAF50")
                .set(BORDER_RADIUS, "8px");
        
        Button btnCancelar = new Button("Cancelar", new Icon(VaadinIcon.CLOSE), e -> {
            mostrarPerfilEmpleado(empleado);
        });
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        btnCancelar.getStyle()
                .set(COLOR, "#666")
                .set(BORDER_RADIUS, "8px");
        
        botonesLayout.add(btnGuardar, btnCancelar);
        
        contenedorPrincipal.add(titulo, subtituloDatos, formDatos, subtituloPassword, infoPassword, formPassword, botonesLayout);
    }

    private void mostrarPantallaLogin() {
        contenedorPrincipal.removeAll();
        
        Icon lockIcon = new Icon(VaadinIcon.LOCK);
        lockIcon.setSize("64px");
        lockIcon.setColor(COLOR1);
        lockIcon.getStyle().set(MARGIN_BOTTOM, "20px");
        
        H2 titulo = new H2("Acceso Restringido");
        titulo.getStyle()
                .set(COLOR, "#333")
                .set(MARGIN, "0 0 10px 0");
        
        Paragraph mensaje = new Paragraph("Para ver tu perfil, primero debes iniciar sesión.");
        mensaje.getStyle()
                .set(COLOR, "#666")
                .set(FONT_SIZE, "16px")
                .set("text-align", CENTER)
                .set(MARGIN, "0 0 30px 0");
        
        Button loginButton = new Button("Iniciar Sesión", new Icon(VaadinIcon.SIGN_IN));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.setWidthFull();
        loginButton.getStyle()
                .set(B_COLOR, COLOR1)
                .set("max-width", "300px")
                .set(BORDER_RADIUS, "8px");
        loginButton.addClickListener(e -> UI.getCurrent().navigate("acceso"));
        
        Button registerButton = new Button("Crear Cuenta", new Icon(VaadinIcon.USER_CARD));
        registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        registerButton.setWidthFull();
        registerButton.getStyle()
                .set(COLOR, COLOR1)
                .set(BORDER_COLOR, COLOR1)
                .set("max-width", "300px")
                .set(BORDER_RADIUS, "8px")
                .set(MARGIN_TOP, "10px");
        registerButton.addClickListener(e -> UI.getCurrent().navigate("acceso"));
        
        VerticalLayout centerLayout = new VerticalLayout(lockIcon, titulo, mensaje, loginButton, registerButton);
        centerLayout.setAlignItems(Alignment.CENTER);
        centerLayout.setPadding(true);
        
        contenedorPrincipal.add(centerLayout);
        contenedorPrincipal.setAlignItems(Alignment.CENTER);
    }
}
