package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLayout;
import org.springframework.beans.factory.annotation.Autowired;

@Layout
public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div contentContainer;
    private final SessionService sessionService;
    private Button loginButton;

    public MainLayout(@Autowired SessionService sessionService) {
        this.sessionService = sessionService;
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        setWidthFull();
        setAlignItems(Alignment.STRETCH);
        getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("height", "100vh")
                .set("min-height", "0")
                .set("min-width", "0");

        HorizontalLayout header = createHeader();
        header.setWidthFull();

        HorizontalLayout menuNavigation = createNavigationMenu();
        menuNavigation.setWidthFull();

        contentContainer = new Div();
        contentContainer.setWidthFull();
        contentContainer.getStyle()
                .set("box-sizing", "border-box")
                .set("background-color", "#f5f5f5")
                .set("padding", "clamp(8px, 2vw, 24px)")
                .set("overflow", "auto")
                .set("flex", "1 1 auto")
                .set("min-height", "0")
                .set("min-width", "0");

        Footer footer = createFooter();

        add(header, menuNavigation, contentContainer, footer);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contentContainer.getElement().removeAllChildren();
        if (content != null) {
            contentContainer.getElement().appendChild(content.getElement());
        }
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.getStyle()
                .set("background-color", "#e30613")
                .set("color", "white")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)");
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        Div logoDiv = new Div();
        Span logo = new Span("MercaESI");
        logo.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "white")
                .set("cursor", "pointer");
        logo.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
        logo.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");

        logo.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("")));
        logoDiv.add(logo);

        Div spacer = new Div();
        header.setFlexGrow(1, spacer);

        HorizontalLayout rightSection = new HorizontalLayout();
        rightSection.setSpacing(true);
        rightSection.setAlignItems(Alignment.CENTER);

        // Botón GESTIÓN (solo para administradores)
        if (esAdministrador()) {
            MenuBar gestionMenuBar = new MenuBar();
            gestionMenuBar.addThemeVariants(com.vaadin.flow.component.menubar.MenuBarVariant.LUMO_TERTIARY);
            gestionMenuBar.getStyle()
                    .set("background", "transparent")
                    .set("border", "none");
            
            MenuItem gestionItem = gestionMenuBar.addItem(new Icon(VaadinIcon.COG));
            gestionItem.add("GESTIÓN");
            gestionItem.getElement().getStyle()
                    .set("color", "white")
                    .set("border-radius", "50px")
                    .set("cursor", "pointer")
                    .set("background", "transparent")
                    .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                    .set("transition", "background-color 0.3s");
            
            gestionItem.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
            gestionItem.getElement().setAttribute("onmouseout", "this.style.backgroundColor='transparent';");
            
            SubMenu gestionSubMenu = gestionItem.getSubMenu();
            MenuItem productosItem = gestionSubMenu.addItem("Gestión de Productos");
            productosItem.addClickListener(e -> 
                getUI().ifPresent(ui -> ui.navigate("productos"))
            );
            
            MenuItem empleadosItem = gestionSubMenu.addItem("Gestión de Empleados");
            empleadosItem.addClickListener(e -> 
                getUI().ifPresent(ui -> ui.navigate("empleados"))
            );
            
            rightSection.add(gestionMenuBar);
        }

        // Botón EMPLEADO (para empleados y administradores)
        if (esEmpleado()) {
            Button empleadoButton = new Button("EMPLEADO", new Icon(VaadinIcon.USER_CARD));
            empleadoButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            empleadoButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("pedidos-pendientes")));
            empleadoButton.getStyle()
                    .set("color", "white")
                    .set("border-radius", "50px")
                    .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                    .set("transition", "background-color 0.3s")
                    .set("cursor", "pointer");
            
            empleadoButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
            empleadoButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='transparent';");
            
            rightSection.add(empleadoButton);
        }

        // Botón REPARTIDOR (para repartidores y administradores)
        if (esRepartidor()) {
            Button repartidorButton = new Button("REPARTIDOR", new Icon(VaadinIcon.TRUCK));
            repartidorButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            repartidorButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("repartidor")));
            repartidorButton.getStyle()
                    .set("color", "white")
                    .set("border-radius", "50px")
                    .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                    .set("transition", "background-color 0.3s")
                    .set("cursor", "pointer");
            
            repartidorButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
            repartidorButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='transparent';");
            
            rightSection.add(repartidorButton);
        }

        // Botón COCINA (para personal de cocina y administradores)
        if (esCocina()) {
            Button cocinaButton = new Button("COCINA", new Icon(VaadinIcon.COFFEE));
            cocinaButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            cocinaButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("cocina")));
            cocinaButton.getStyle()
                    .set("color", "white")
                    .set("border-radius", "50px")
                    .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                    .set("transition", "background-color 0.3s")
                    .set("cursor", "pointer");
            
            cocinaButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
            cocinaButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='transparent';");
            
            rightSection.add(cocinaButton);
        }

        Button cartButton = new Button("CESTA", new Icon(VaadinIcon.CART));
        cartButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cartButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("carrito")));
        cartButton.getStyle()
                .set("color", "white")
                .set("border-radius","50px")
                .set("box-shadow", "0 4px 10px rgba(0, 0, 0, 0.3)")
                .set("transition", "background-color 0.3s")
                .set("cursor", "pointer");

        // Efecto similar a los botones del menú: cambio sutil de fondo al pasar el ratón
        cartButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
        cartButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");

        // Botón dinámico: ACCESO o PERFIL según el estado de sesión
        boolean isLoggedIn = sessionService.isLoggedIn();
        String buttonText = isLoggedIn ? "PERFIL" : "ACCESO";
        String navigationTarget = isLoggedIn ? "perfil" : "acceso";
        
        loginButton = new Button(buttonText, new Icon(VaadinIcon.USER));
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(navigationTarget)));
        loginButton.getStyle()
                .set("color", "white")
                .set("border-radius","50px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.3)")
                .set("transition", "background-color 0.3s")
                .set("cursor", "pointer");

        // Efecto similar a los botones del menú: cambio sutil de fondo al pasar el ratón
        loginButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(255,255,255,0.3)';");
        loginButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");


        rightSection.add(cartButton, loginButton);
        header.add(logoDiv, spacer, rightSection);
        return header;
    }

    private HorizontalLayout createNavigationMenu() {
        HorizontalLayout menuBar = new HorizontalLayout();
        menuBar.setWidthFull();
        menuBar.setPadding(true);
        menuBar.setJustifyContentMode(JustifyContentMode.CENTER);
        menuBar.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "6px")
                .set("background-color", "white")
                .set("border-radius", "50px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.55)")
                .set("margin", "0")
                .set("padding", "8px clamp(10px, 2vw, 20px)");

        // Menú principal (visible para todos)
        String[] menuItems = {"PIZZAS", "MENÚS", "OFERTAS", "BURGERS", "BEBIDAS", "POSTRES"};

        for (String item : menuItems) {
            Button menuButton = new Button(item);
            menuButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            menuButton.getStyle()
                    .set("color", "#333")
                    .set("font-weight", "500")
                    .set("padding", "10px 15px")
                    .set("border-radius", "25px")
                    .set("transition", "background-color 0.3s")
                    .set("cursor", "pointer");
            menuButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(item.toLowerCase())));
            menuButton.getElement().setAttribute("onmouseover", "this.style.backgroundColor='rgba(227, 227, 227, 1)';");
            menuButton.getElement().setAttribute("onmouseout", "this.style.backgroundColor='';");
            menuBar.add(menuButton);
        }
        
        return menuBar;
    }

    /**
     * Verifica si el usuario actual es administrador
     */
    private boolean esAdministrador() {
        com.example.iw20252026merca_esi.model.Empleado empleado = sessionService.getEmpleado();
        return empleado != null && empleado.esAdministrador();
    }

    /**
     * Verifica si el usuario actual es un empleado (cualquier rol de empleado)
     */
    private boolean esEmpleado() {
        com.example.iw20252026merca_esi.model.Empleado empleado = sessionService.getEmpleado();
        return empleado != null;
    }

    /**
     * Verifica si el usuario actual es repartidor o administrador
     */
    private boolean esRepartidor() {
        com.example.iw20252026merca_esi.model.Empleado empleado = sessionService.getEmpleado();
        return empleado != null && (empleado.esRepartidor() || empleado.esAdministrador());
    }

    /**
     * Verifica si el usuario actual es de cocina o administrador
     */
    private boolean esCocina() {
        com.example.iw20252026merca_esi.model.Empleado empleado = sessionService.getEmpleado();
        return empleado != null && (empleado.esCocina() || empleado.esAdministrador());
    }

    private Footer createFooter() {
        Footer footer = new Footer();
        footer.setWidthFull();
        footer.getStyle()
                .set("box-sizing", "border-box")
                .set("background-color", "#333")
                .set("color", "white")
                .set("padding", "clamp(12px, 2vw, 20px)")
                .set("margin", "0")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center")
                .set("align-items", "center")
                .set("gap", "8px")
                .set("text-align", "center");

        Span footerText = new Span("© 2025 MercaESI - Todos los derechos reservados");
        footer.add(footerText);
        return footer;
    }
}