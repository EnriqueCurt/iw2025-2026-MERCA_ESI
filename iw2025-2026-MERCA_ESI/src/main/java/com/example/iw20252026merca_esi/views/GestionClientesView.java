package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.service.ClienteService;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.component.textfield.IntegerField;
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

@Route(value = "clientes", layout = MainLayout.class)
@PageTitle("Gestión de Clientes - MercaESI")
@RolesAllowed("ADMINISTRADOR")
public class GestionClientesView extends VerticalLayout implements BeforeEnterObserver {

    private final ClienteService clienteService;
    private final SessionService sessionService;
    private Grid<Cliente> grid;
    private TextField searchField;

    @Autowired
    public GestionClientesView(ClienteService clienteService, SessionService sessionService) {
        this.clienteService = clienteService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Título
        H2 titulo = new H2("Gestión de Clientes");
        titulo.getStyle().set("color", "#D32F2F");

        // Botón para crear nuevo cliente
        Button btnNuevo = new Button("Nuevo Cliente", new Icon(VaadinIcon.PLUS));
        btnNuevo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnNuevo.getStyle().set("background-color", "#D32F2F");
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
        if (empleado == null || !empleado.esAdministrador()) {
            event.rerouteTo("");
            Notification.show("Acceso denegado.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configurarGrid() {
        grid = new Grid<>(Cliente.class, false);
        grid.setHeight("600px");
        grid.setWidthFull();

        // Columnas
        grid.addColumn(Cliente::getIdCliente).setHeader("ID").setWidth("80px").setFlexGrow(0).setSortProperty("idCliente");
        grid.addColumn(Cliente::getNombre).setHeader("Nombre").setSortable(true).setSortProperty("nombre");
        grid.addColumn(Cliente::getUsername).setHeader("Usuario").setSortable(true).setSortProperty("username");
        grid.addColumn(Cliente::getEmail).setHeader("Email").setSortable(true).setSortProperty("email");
        grid.addColumn(Cliente::getTelefono).setHeader("Teléfono");
        grid.addColumn(Cliente::getPuntos).setHeader("Puntos").setSortable(true).setWidth("100px").setSortProperty("puntos");

        // Columna de acciones
        grid.addComponentColumn(cliente -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);

            Button btnEditar = new Button(new Icon(VaadinIcon.EDIT));
            btnEditar.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            btnEditar.addClickListener(e -> abrirDialogoEditar(cliente));

            Button btnEliminar = new Button(new Icon(VaadinIcon.TRASH));
            btnEliminar.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            btnEliminar.addClickListener(e -> confirmarEliminar(cliente));

            actions.add(btnEditar, btnEliminar);
            return actions;
        }).setHeader("Acciones").setWidth("150px").setFlexGrow(0);

        // Cargar datos con lazy loading
        configurarLazyDataProvider();
    }

    private void configurarLazyDataProvider() {
        String filtro = searchField.getValue();
        
        CallbackDataProvider<Cliente, Void> dataProvider = DataProvider.fromCallbacks(
            // Fetch callback: carga solo los datos necesarios para la página actual
            query -> {
                int page = query.getPage();
                int size = query.getPageSize();
                
                // Construir ordenamiento dinámico desde el Grid
                Sort sort = Sort.by("idCliente").descending(); // Default
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
                    return clienteService.listarClientesPaginados(pageRequest).stream();
                } else {
                    // Para búsqueda, seguimos usando el método anterior (ya optimizado con índices)
                    String filtroLower = filtro.toLowerCase();
                    return clienteService.listarClientes().stream()
                        .filter(cliente -> 
                            cliente.getNombre().toLowerCase().contains(filtroLower) ||
                            cliente.getUsername().toLowerCase().contains(filtroLower) ||
                            (cliente.getEmail() != null && cliente.getEmail().toLowerCase().contains(filtroLower))
                        )
                        .skip(query.getOffset())
                        .limit(query.getLimit());
                }
            },
            // Count callback: cuenta el total de registros
            query -> {
                if (filtro == null || filtro.trim().isEmpty()) {
                    return (int) clienteService.contarClientes();
                } else {
                    String filtroLower = filtro.toLowerCase();
                    return (int) clienteService.listarClientes().stream()
                        .filter(cliente -> 
                            cliente.getNombre().toLowerCase().contains(filtroLower) ||
                            cliente.getUsername().toLowerCase().contains(filtroLower) ||
                            (cliente.getEmail() != null && cliente.getEmail().toLowerCase().contains(filtroLower))
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
        dialog.setHeaderTitle("Nuevo Cliente");
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

        IntegerField puntosField = new IntegerField("Puntos");
        puntosField.setWidthFull();
        puntosField.setValue(0);
        puntosField.setMin(0);

        FormLayout formLayout = new FormLayout(nombreField, usernameField, emailField, telefonoField, passwordField, puntosField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Botones
        Button btnGuardar = new Button("Guardar", e -> {
            try {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNombre(nombreField.getValue());
                nuevoCliente.setUsername(usernameField.getValue());
                nuevoCliente.setEmail(emailField.getValue());
                nuevoCliente.setTelefono(telefonoField.getValue().isEmpty() ? null : telefonoField.getValue());
                nuevoCliente.setContrasena(passwordField.getValue());
                nuevoCliente.setPuntos(puntosField.getValue());

                clienteService.registrarCliente(nuevoCliente);

                actualizarGrid();
                dialog.close();
                Notification.show("Cliente creado exitosamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

    private void abrirDialogoEditar(Cliente cliente) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Cliente");
        dialog.setWidth("500px");

        // Formulario
        TextField nombreField = new TextField("Nombre");
        nombreField.setValue(cliente.getNombre());
        nombreField.setWidthFull();

        TextField usernameField = new TextField("Usuario");
        usernameField.setValue(cliente.getUsername());
        usernameField.setWidthFull();

        TextField emailField = new TextField("Email");
        emailField.setValue(cliente.getEmail());
        emailField.setWidthFull();

        TextField telefonoField = new TextField("Teléfono");
        telefonoField.setValue(cliente.getTelefono() != null ? cliente.getTelefono() : "");
        telefonoField.setWidthFull();

        PasswordField passwordField = new PasswordField("Nueva Contraseña");
        passwordField.setWidthFull();
        passwordField.setHelperText("Dejar en blanco para mantener la actual");

        IntegerField puntosField = new IntegerField("Puntos");
        puntosField.setValue(cliente.getPuntos());
        puntosField.setWidthFull();
        puntosField.setMin(0);

        FormLayout formLayout = new FormLayout(nombreField, usernameField, emailField, telefonoField, passwordField, puntosField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Botones
        Button btnGuardar = new Button("Guardar", e -> {
            try {
                Cliente clienteActualizado = new Cliente();
                clienteActualizado.setNombre(nombreField.getValue());
                clienteActualizado.setUsername(usernameField.getValue());
                clienteActualizado.setEmail(emailField.getValue());
                clienteActualizado.setTelefono(telefonoField.getValue().isEmpty() ? null : telefonoField.getValue());
                clienteActualizado.setPuntos(puntosField.getValue());
                
                if (!passwordField.getValue().isEmpty()) {
                    clienteActualizado.setContrasena(passwordField.getValue());
                }

                clienteService.actualizarCliente(cliente.getIdCliente(), clienteActualizado);

                actualizarGrid();
                dialog.close();
                Notification.show("Cliente actualizado exitosamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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

    private void confirmarEliminar(Cliente cliente) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Confirmar eliminación");
        
        Div contenido = new Div();
        contenido.setText("¿Está seguro que desea eliminar al cliente '" + cliente.getNombre() + "'?");
        contenido.getStyle().set("padding", "20px");

        Button btnConfirmar = new Button("Eliminar", e -> {
            try {
                clienteService.eliminarCliente(cliente.getIdCliente());
                actualizarGrid();
                dialog.close();
                Notification.show("Cliente eliminado exitosamente").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
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
