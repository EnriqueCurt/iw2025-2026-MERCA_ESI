package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "perfil", layout = MainLayout.class)
@PageTitle("Perfil de Usuario")
public class PerfilView extends VerticalLayout {

    private final SessionService sessionService;

    @Autowired
    public PerfilView(SessionService sessionService) {
        this.sessionService = sessionService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        setPadding(false);
        setSpacing(false);
        
        // Wrapper con padding superior para evitar solapamiento con el header
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setWidthFull();
        wrapper.setPadding(false);
        wrapper.setSpacing(true);
        wrapper.getStyle().set("padding-top", "80px");
        
        // Contenedor principal
        VerticalLayout container = new VerticalLayout();
        container.setWidth("600px");
        container.setPadding(true);
        container.setSpacing(true);
        container.getStyle()
                .set("background-color", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.1)");
        
        // Título
        H2 titulo = new H2("Mi Perfil");
        titulo.getStyle()
                .set("margin-top", "0")
                .set("margin-bottom", "20px")
                .set("color", "#D32F2F");
        
        // Verificar si hay usuario logueado (cliente o empleado)
        Cliente cliente = sessionService.getCliente();
        Empleado empleado = sessionService.getEmpleado();
        
        if (cliente != null) {
            // Cliente logueado - mostrar información
            Div infoSection = new Div();
            infoSection.getStyle().set("width", "100%");
            
            H3 nombreTitulo = new H3("Información Personal (Cliente)");
            nombreTitulo.getStyle().set("color", "#666");
            
            Paragraph nombreP = new Paragraph("Nombre: " + cliente.getNombre());
            Paragraph emailP = new Paragraph("Email: " + cliente.getEmail());
            Paragraph usernameP = new Paragraph("Usuario: " + cliente.getUsername());
            Paragraph puntosP = new Paragraph("Puntos: " + cliente.getPuntos());
            
            nombreP.getStyle().set("font-size", "16px").set("margin", "8px 0");
            emailP.getStyle().set("font-size", "16px").set("margin", "8px 0");
            usernameP.getStyle().set("font-size", "16px").set("margin", "8px 0");
            puntosP.getStyle().set("font-size", "16px").set("margin", "8px 0").set("font-weight", "bold").set("color", "#D32F2F");
            
            infoSection.add(nombreTitulo, nombreP, emailP, usernameP, puntosP);
            
            // Botón de cerrar sesión
            Button logoutButton = new Button("Cerrar Sesión");
            logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            logoutButton.setWidthFull();
            logoutButton.addClickListener(e -> {
                sessionService.logout();
                UI.getCurrent().navigate("");
                UI.getCurrent().getPage().reload();
            });
            
            container.add(titulo, infoSection, logoutButton);
        } else if (empleado != null) {
            // Empleado logueado - mostrar información
            Div infoSection = new Div();
            infoSection.getStyle().set("width", "100%");
            
            H3 nombreTitulo = new H3("Información Personal (Empleado)");
            nombreTitulo.getStyle().set("color", "#666");
            
            Paragraph nombreP = new Paragraph("Nombre: " + empleado.getNombre());
            Paragraph emailP = new Paragraph("Email: " + empleado.getEmail());
            Paragraph usernameP = new Paragraph("Usuario: " + empleado.getUsername());
            
            // Mostrar roles del empleado
            StringBuilder rolesStr = new StringBuilder("Roles: ");
            if (empleado.getRoles() != null && !empleado.getRoles().isEmpty()) {
                empleado.getRoles().forEach(rol -> rolesStr.append(rol.getNombre()).append(", "));
                rolesStr.setLength(rolesStr.length() - 2); // Eliminar última coma
            } else {
                rolesStr.append("Sin roles asignados");
            }
            Paragraph rolesP = new Paragraph(rolesStr.toString());
            
            nombreP.getStyle().set("font-size", "16px").set("margin", "8px 0");
            emailP.getStyle().set("font-size", "16px").set("margin", "8px 0");
            usernameP.getStyle().set("font-size", "16px").set("margin", "8px 0");
            rolesP.getStyle().set("font-size", "16px").set("margin", "8px 0").set("font-weight", "bold").set("color", "#D32F2F");
            
            infoSection.add(nombreTitulo, nombreP, emailP, usernameP, rolesP);
            
            // Botón de cerrar sesión
            Button logoutButton = new Button("Cerrar Sesión");
            logoutButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            logoutButton.setWidthFull();
            logoutButton.addClickListener(e -> {
                sessionService.logout();
                UI.getCurrent().navigate("");
                UI.getCurrent().getPage().reload();
            });
            
            container.add(titulo, infoSection, logoutButton);
        } else {
            // No hay usuario logueado - mostrar botones de login
            Div infoSection = new Div();
            infoSection.getStyle().set("width", "100%");
            
            Paragraph mensaje = new Paragraph("Para ver tu perfil, primero debes iniciar sesión.");
            mensaje.getStyle()
                    .set("color", "#666")
                    .set("font-size", "16px");
            
            VerticalLayout botonesLayout = new VerticalLayout();
            botonesLayout.setWidthFull();
            botonesLayout.setSpacing(true);
            botonesLayout.setPadding(false);
            
            Button loginButton = new Button("Iniciar Sesión");
            loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            loginButton.setWidthFull();
            loginButton.getStyle()
                    .set("background-color", "#D32F2F")
                    .set("color", "white");
            loginButton.addClickListener(e -> UI.getCurrent().navigate("acceso"));
            
            Button registerButton = new Button("Crear Cuenta");
            registerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            registerButton.setWidthFull();
            registerButton.getStyle()
                    .set("color", "#D32F2F")
                    .set("border-color", "#D32F2F");
            registerButton.addClickListener(e -> UI.getCurrent().navigate("acceso"));
            
            botonesLayout.add(loginButton, registerButton);
            infoSection.add(mensaje);
            
            container.add(titulo, infoSection, botonesLayout);
        }
        wrapper.add(container);
        add(wrapper);
    }
}
