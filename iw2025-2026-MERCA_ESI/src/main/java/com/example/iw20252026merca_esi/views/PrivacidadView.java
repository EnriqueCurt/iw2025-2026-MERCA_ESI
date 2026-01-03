package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.service.ClienteService;
import com.example.iw20252026merca_esi.service.EmpleadoService;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "privacidad", layout = MainLayout.class)
@PageTitle("Privacidad y Protección de Datos")
public class PrivacidadView extends VerticalLayout {

    private final SessionService sessionService;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;

    @Autowired
    public PrivacidadView(SessionService sessionService, ClienteService clienteService, EmpleadoService empleadoService) {
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
        VerticalLayout container = new VerticalLayout();
        container.setWidth("900px");
        container.setPadding(true);
        container.setSpacing(true);
        container.getStyle()
                .set("background", "white")
                .set("border-radius", "16px")
                .set("box-shadow", "0 8px 32px rgba(0,0,0,0.1)");

        // Verificar usuario logueado
        Cliente cliente = sessionService.getCliente();
        Empleado empleado = sessionService.getEmpleado();

        if (cliente != null || empleado != null) {
            // Header
            HorizontalLayout header = new HorizontalLayout();
            header.setWidthFull();
            header.setAlignItems(Alignment.CENTER);
            header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            header.getStyle()
                    .set("background", "linear-gradient(135deg, #1976D2 0%, #1565C0 100%)")
                    .set("color", "white")
                    .set("padding", "30px")
                    .set("border-radius", "12px")
                    .set("margin-bottom", "20px");

            Icon shieldIcon = new Icon(VaadinIcon.SHIELD);
            shieldIcon.setSize("48px");

            VerticalLayout headerText = new VerticalLayout();
            headerText.setSpacing(false);
            headerText.setPadding(false);

            H2 titulo = new H2("Privacidad y Protección de Datos");
            titulo.getStyle()
                    .set("margin", "0")
                    .set("color", "white")
                    .set("font-size", "28px");

            Span subtitulo = new Span("Gestiona tus datos personales según RGPD");
            subtitulo.getStyle()
                    .set("font-size", "14px")
                    .set("opacity", "0.9");

            headerText.add(titulo, subtitulo);

            Button btnVolver = new Button("Volver al Perfil", new Icon(VaadinIcon.ARROW_LEFT));
            btnVolver.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            btnVolver.getStyle()
                    .set("color", "white")
                    .set("border-color", "white");
            btnVolver.addClickListener(e -> UI.getCurrent().navigate("perfil"));

            HorizontalLayout headerLeft = new HorizontalLayout(shieldIcon, headerText);
            headerLeft.setAlignItems(Alignment.CENTER);
            headerLeft.setSpacing(true);

            header.add(headerLeft, btnVolver);

            // Sección: Información sobre uso de datos
            VerticalLayout seccionInfo = crearSeccionInformacion();

            // Sección: Tus derechos
            VerticalLayout seccionDerechos = crearSeccionDerechos(cliente, empleado);

            // Sección: Acciones sobre tus datos
            VerticalLayout seccionAcciones = crearSeccionAcciones(cliente, empleado);

            container.add(header, seccionInfo, seccionDerechos, seccionAcciones);
        } else {
            // No hay usuario logueado
            container.add(crearMensajeNoAutenticado());
        }

        wrapper.add(container);
        add(wrapper);
    }

    private VerticalLayout crearSeccionInformacion() {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setPadding(true);
        seccion.setSpacing(true);
        seccion.getStyle()
                .set("background", "#f8f9fa")
                .set("border-radius", "8px")
                .set("margin-bottom", "20px");

        H3 titulo = new H3("Información sobre el uso de tus datos");
        titulo.getStyle().set("color", "#333").set("margin-top", "0");

        Paragraph intro = new Paragraph(
                "En MercaESI valoramos tu privacidad y nos comprometemos a proteger tus datos personales " +
                "de acuerdo con el Reglamento General de Protección de Datos (RGPD)."
        );
        intro.getStyle().set("color", "#666");

        H4 subtitulo1 = new H4("¿Qué datos recopilamos?");
        subtitulo1.getStyle().set("color", "#1976D2").set("margin-bottom", "10px");

        UnorderedList listaDatos = new UnorderedList();
        listaDatos.add(new ListItem("Nombre completo"));
        listaDatos.add(new ListItem("Nombre de usuario"));
        listaDatos.add(new ListItem("Dirección de correo electrónico"));
        listaDatos.add(new ListItem("Número de teléfono (opcional)"));
        listaDatos.add(new ListItem("Información de pedidos y transacciones"));
        listaDatos.add(new ListItem("Puntos de fidelización (solo clientes)"));
        listaDatos.getStyle().set("color", "#555");

        H4 subtitulo2 = new H4("¿Cómo usamos tus datos?");
        subtitulo2.getStyle().set("color", "#1976D2").set("margin-bottom", "10px");

        UnorderedList listaUso = new UnorderedList();
        listaUso.add(new ListItem("Procesar y gestionar tus pedidos"));
        listaUso.add(new ListItem("Comunicarnos contigo sobre tu cuenta"));
        listaUso.add(new ListItem("Mejorar nuestros servicios"));
        listaUso.add(new ListItem("Gestionar tu programa de puntos de fidelización"));
        listaUso.add(new ListItem("Cumplir con obligaciones legales"));
        listaUso.getStyle().set("color", "#555");

        H4 subtitulo3 = new H4("Seguridad de tus datos");
        subtitulo3.getStyle().set("color", "#1976D2").set("margin-bottom", "10px");

        Paragraph seguridad = new Paragraph(
                "Utilizamos cifrado y medidas de seguridad técnicas y organizativas apropiadas " +
                "para proteger tus datos personales contra acceso no autorizado, pérdida o alteración."
        );
        seguridad.getStyle().set("color", "#555");

        seccion.add(titulo, intro, subtitulo1, listaDatos, subtitulo2, listaUso, subtitulo3, seguridad);
        return seccion;
    }

    private VerticalLayout crearSeccionDerechos(Cliente cliente, Empleado empleado) {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setPadding(true);
        seccion.setSpacing(true);
        seccion.getStyle()
                .set("background", "#e3f2fd")
                .set("border-radius", "8px")
                .set("margin-bottom", "20px");

        H3 titulo = new H3("Tus Derechos bajo el RGPD");
        titulo.getStyle().set("color", "#1565C0").set("margin-top", "0");

        // Crear tarjetas de derechos
        HorizontalLayout cardsLayout = new HorizontalLayout();
        cardsLayout.setWidthFull();
        cardsLayout.setSpacing(true);
        cardsLayout.getStyle().set("flex-wrap", "wrap");

        cardsLayout.add(
                crearTarjetaDerecho(VaadinIcon.EYE, "Acceso", "Consultar los datos que tenemos sobre ti"),
                crearTarjetaDerecho(VaadinIcon.EDIT, "Rectificación", "Modificar datos incorrectos o incompletos"),
                crearTarjetaDerecho(VaadinIcon.TRASH, "Supresión", "Solicitar la eliminación de tus datos"),
                crearTarjetaDerecho(VaadinIcon.HAND, "Oposición", "Oponerte al procesamiento de tus datos"),
                crearTarjetaDerecho(VaadinIcon.DOWNLOAD, "Portabilidad", "Recibir tus datos en formato estructurado"),
                crearTarjetaDerecho(VaadinIcon.BAN, "Limitación", "Restringir el procesamiento de tus datos")
        );

        seccion.add(titulo, cardsLayout);
        return seccion;
    }

    private Div crearTarjetaDerecho(VaadinIcon icono, String titulo, String descripcion) {
        Div tarjeta = new Div();
        tarjeta.getStyle()
                .set("background", "white")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)")
                .set("flex", "1 1 280px")
                .set("min-width", "280px")
                .set("margin-bottom", "15px");

        Icon icon = new Icon(icono);
        icon.setSize("32px");
        icon.setColor("#1976D2");
        icon.getStyle().set("margin-bottom", "10px");

        H4 tituloH4 = new H4(titulo);
        tituloH4.getStyle()
                .set("margin", "10px 0")
                .set("color", "#333")
                .set("font-size", "18px");

        Paragraph desc = new Paragraph(descripcion);
        desc.getStyle()
                .set("color", "#666")
                .set("font-size", "14px")
                .set("margin", "0");

        tarjeta.add(icon, tituloH4, desc);
        return tarjeta;
    }

    private VerticalLayout crearSeccionAcciones(Cliente cliente, Empleado empleado) {
        VerticalLayout seccion = new VerticalLayout();
        seccion.setPadding(true);
        seccion.setSpacing(true);
        seccion.getStyle()
                .set("background", "#fff3e0")
                .set("border-radius", "8px");

        H3 titulo = new H3("Gestiona tus Datos");
        titulo.getStyle().set("color", "#E65100").set("margin-top", "0");

        Paragraph info = new Paragraph(
                "Puedes ejercer tus derechos en cualquier momento. Algunas acciones requieren confirmación adicional por seguridad."
        );
        info.getStyle().set("color", "#666");

        HorizontalLayout botonesLayout = new HorizontalLayout();
        botonesLayout.setWidthFull();
        botonesLayout.setSpacing(true);
        botonesLayout.getStyle().set("flex-wrap", "wrap").set("gap", "15px");

        // Botón Ver mis datos
        Button btnVerDatos = new Button("Ver Mis Datos", new Icon(VaadinIcon.EYE));
        btnVerDatos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnVerDatos.getStyle()
                .set("background-color", "#1976D2")
                .set("flex", "1 1 200px");
        btnVerDatos.addClickListener(e -> UI.getCurrent().navigate("perfil"));

        // Botón Editar datos
        Button btnEditarDatos = new Button("Editar Mis Datos", new Icon(VaadinIcon.EDIT));
        btnEditarDatos.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnEditarDatos.getStyle()
                .set("background-color", "#4CAF50")
                .set("flex", "1 1 200px");
        btnEditarDatos.addClickListener(e -> UI.getCurrent().navigate("perfil"));

        // Botón Eliminar cuenta
        Button btnEliminarCuenta = new Button("Eliminar Mi Cuenta", new Icon(VaadinIcon.TRASH));
        btnEliminarCuenta.addThemeVariants(ButtonVariant.LUMO_ERROR);
        btnEliminarCuenta.getStyle()
                .set("flex", "1 1 200px");
        btnEliminarCuenta.addClickListener(e -> {
            mostrarDialogoEliminarCuenta(cliente, empleado);
        });

        botonesLayout.add(btnVerDatos, btnEditarDatos, btnEliminarCuenta);

        seccion.add(titulo, info, botonesLayout);
        return seccion;
    }

    private void mostrarDialogoEliminarCuenta(Cliente cliente, Empleado empleado) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("⚠️ Eliminar Cuenta");
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        Div advertencia = new Div();
        advertencia.getStyle()
                .set("background", "#ffebee")
                .set("padding", "20px")
                .set("border-radius", "8px")
                .set("border-left", "4px solid #D32F2F")
                .set("margin-bottom", "15px");

        H4 tituloAdvertencia = new H4("¡ATENCIÓN! Esta acción es irreversible");
        tituloAdvertencia.getStyle()
                .set("color", "#D32F2F")
                .set("margin", "0 0 10px 0");

        Paragraph textoAdvertencia = new Paragraph(
                "Al eliminar tu cuenta, se eliminarán permanentemente:\n" +
                "• Todos tus datos personales\n" +
                "• Tu historial de pedidos\n" +
                (cliente != null ? "• Tus " + cliente.getPuntos() + " puntos acumulados\n" : "") +
                "• Tu cuenta de usuario\n\n" +
                "Esta acción NO se puede deshacer."
        );
        textoAdvertencia.getStyle()
                .set("color", "#666")
                .set("white-space", "pre-line")
                .set("margin", "0");

        advertencia.add(tituloAdvertencia, textoAdvertencia);

        Paragraph confirmacion = new Paragraph("Para confirmar, ingresa tu contraseña:");
        confirmacion.getStyle().set("font-weight", "bold").set("color", "#333");

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setWidthFull();
        passwordField.setPlaceholder("Ingresa tu contraseña actual");
        passwordField.setPrefixComponent(new Icon(VaadinIcon.LOCK));

        content.add(advertencia, confirmacion, passwordField);

        Button btnEliminar = new Button("Eliminar Mi Cuenta Permanentemente", new Icon(VaadinIcon.TRASH), e -> {
            if (passwordField.getValue().isEmpty()) {
                Notification.show("Debes ingresar tu contraseña para confirmar")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Verificar contraseña
            boolean passwordValida = false;
            if (cliente != null) {
                passwordValida = clienteService.autenticar(cliente.getUsername(), passwordField.getValue()).isPresent();
            } else if (empleado != null) {
                passwordValida = empleadoService.autenticar(empleado.getUsername(), passwordField.getValue()).isPresent();
            }

            if (!passwordValida) {
                Notification.show("Contraseña incorrecta")
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            // Eliminar cuenta
            try {
                if (cliente != null) {
                    clienteService.eliminarCliente(cliente.getIdCliente());
                } else if (empleado != null) {
                    empleadoService.eliminarEmpleado(empleado.getIdEmpleado());
                }

                sessionService.logout();
                dialog.close();

                Notification.show(
                        "Tu cuenta ha sido eliminada exitosamente. Lamentamos verte partir.",
                        5000,
                        Notification.Position.TOP_CENTER
                ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                UI.getCurrent().navigate("");
                UI.getCurrent().getPage().reload();
            } catch (Exception ex) {
                Notification.show("Error al eliminar la cuenta: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button btnCancelar = new Button("No, mantener mi cuenta", e -> dialog.close());
        btnCancelar.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttons = new HorizontalLayout(btnCancelar, btnEliminar);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        buttons.setWidthFull();

        dialog.add(content);
        dialog.getFooter().add(buttons);
        dialog.open();
    }

    private VerticalLayout crearMensajeNoAutenticado() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(true);

        Icon lockIcon = new Icon(VaadinIcon.LOCK);
        lockIcon.setSize("64px");
        lockIcon.setColor("#1976D2");

        H2 titulo = new H2("Acceso Restringido");
        titulo.getStyle().set("color", "#333");

        Paragraph mensaje = new Paragraph(
                "Debes iniciar sesión para acceder a la configuración de privacidad y protección de datos."
        );
        mensaje.getStyle().set("color", "#666").set("text-align", "center");

        Button btnLogin = new Button("Iniciar Sesión", new Icon(VaadinIcon.SIGN_IN));
        btnLogin.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnLogin.getStyle().set("background-color", "#1976D2");
        btnLogin.addClickListener(e -> UI.getCurrent().navigate("acceso"));

        layout.add(lockIcon, titulo, mensaje, btnLogin);
        return layout;
    }
}
