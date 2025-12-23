package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.Pedido;
import com.example.iw20252026merca_esi.repository.PedidoRepository;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "pedidos-pendientes", layout = MainLayout.class)
@PageTitle("Pedidos Pendientes - MercaESI")
@RolesAllowed({"ADMINISTRADOR", "PROPIETARIO", "MANAGER", "REPARTIDOR"})
public class PedidosPendientesView extends VerticalLayout implements BeforeEnterObserver {
    
    private final PedidoRepository pedidoRepository;
    private final SessionService sessionService;
    private Grid<Pedido> pedidosGrid;

    @Autowired
    public PedidosPendientesView(PedidoRepository pedidoRepository, SessionService sessionService) {
        this.pedidoRepository = pedidoRepository;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Título
        H2 titulo = new H2("Pedidos Pendientes");
        titulo.getStyle().set("color", "#D32F2F");

        // Botón para actualizar
        Button btnActualizar = new Button("Actualizar", new Icon(VaadinIcon.REFRESH));
        btnActualizar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnActualizar.getStyle().set("background-color", "#D32F2F");
        btnActualizar.addClickListener(e -> cargarPedidosPendientes());

        HorizontalLayout headerLayout = new HorizontalLayout(titulo, btnActualizar);
        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.setAlignItems(Alignment.CENTER);

        // Grid de pedidos
        configurarGrid();

        add(headerLayout, pedidosGrid);

        // Cargar datos iniciales
        cargarPedidosPendientes();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar que el usuario sea empleado
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null) {
            event.rerouteTo("");
            Notification.show("Acceso denegado. Solo empleados.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void configurarGrid() {
        pedidosGrid = new Grid<>(Pedido.class, false);
        pedidosGrid.setHeight("600px");
        pedidosGrid.setWidthFull();

        // Columnas
        pedidosGrid.addColumn(Pedido::getIdPedido)
                .setHeader("ID Pedido")
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("100px");

        pedidosGrid.addColumn(pedido -> {
            if (pedido.getFecha() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                return pedido.getFecha().format(formatter);
            }
            return "";
        })
                .setHeader("Fecha")
                .setSortable(true)
                .setFlexGrow(1);

        pedidosGrid.addColumn(pedido -> {
            if (pedido.getCliente() != null) {
                return pedido.getCliente().getNombre();
            }
            return "Sin cliente";
        })
                .setHeader("Cliente")
                .setSortable(true)
                .setFlexGrow(1);

        pedidosGrid.addColumn(pedido -> String.format("%.2f €", pedido.getTotal()))
                .setHeader("Total")
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("120px");

        pedidosGrid.addColumn(Pedido::getEstado)
                .setHeader("Estado")
                .setSortable(true)
                .setFlexGrow(0)
                .setWidth("120px");

        pedidosGrid.addColumn(pedido -> {
            if (pedido.getEmpleado() != null) {
                return pedido.getEmpleado().getNombre();
            }
            return "Sin asignar";
        })
                .setHeader("Empleado Asignado")
                .setSortable(true)
                .setFlexGrow(1);

        // Columna de acciones
        pedidosGrid.addComponentColumn(pedido -> {
            Button btnVer = new Button("Ver", new Icon(VaadinIcon.EYE));
            btnVer.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            btnVer.addClickListener(e -> verDetallePedido(pedido));

            Button btnAsignar = new Button("Asignarme", new Icon(VaadinIcon.USER_CHECK));
            btnAsignar.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            btnAsignar.getStyle().set("background-color", "#4CAF50");
            btnAsignar.addClickListener(e -> asignarPedido(pedido));
            
            // Deshabilitar si ya está asignado
            if (pedido.getEmpleado() != null) {
                btnAsignar.setEnabled(false);
            }

            HorizontalLayout actions = new HorizontalLayout(btnVer, btnAsignar);
            actions.setSpacing(true);
            return actions;
        })
                .setHeader("Acciones")
                .setFlexGrow(1);
    }

    private void cargarPedidosPendientes() {
        List<Pedido> pedidosPendientes = pedidoRepository.findByEstado("PENDIENTE");
        pedidosGrid.setItems(pedidosPendientes);
        
        Notification.show(String.format("Se encontraron %d pedidos pendientes", pedidosPendientes.size()))
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void verDetallePedido(Pedido pedido) {
        // TODO: Implementar vista de detalle del pedido
        Notification.show("Detalle del pedido #" + pedido.getIdPedido())
                .addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    private void asignarPedido(Pedido pedido) {
        Empleado empleado = sessionService.getEmpleado();
        if (empleado != null) {
            pedido.setEmpleado(empleado);
            pedidoRepository.save(pedido);
            
            Notification.show("Pedido #" + pedido.getIdPedido() + " asignado correctamente")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // Recargar datos
            cargarPedidosPendientes();
        } else {
            Notification.show("Error: No se pudo obtener el empleado actual")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
