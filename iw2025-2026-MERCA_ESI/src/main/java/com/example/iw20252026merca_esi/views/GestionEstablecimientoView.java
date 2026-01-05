package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.Establecimiento;
import com.example.iw20252026merca_esi.service.EstablecimientoService;
import com.example.iw20252026merca_esi.service.SessionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "gestion-establecimiento", layout = MainLayout.class)
@PageTitle("Gestión del Establecimiento - MercaESI")
@RolesAllowed({"ADMINISTRADOR", "MANAGER"})
public class GestionEstablecimientoView extends VerticalLayout implements BeforeEnterObserver {

    private final EstablecimientoService establecimientoService;
    private final SessionService sessionService;
    
    private TextField telefonoField;
    private TextField direccionField;
    private TextField horarioField;
    private TextArea informacionField;
    
    private Establecimiento establecimiento;

    private static final String COLOR2 = "#D32F2F";

    @Autowired
    public GestionEstablecimientoView(EstablecimientoService establecimientoService, SessionService sessionService) {
        this.establecimientoService = establecimientoService;
        this.sessionService = sessionService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);

        // Título
        H2 titulo = new H2("Gestión del Establecimiento");
        titulo.getStyle().set("color", COLOR2);

        // Cargar datos del establecimiento
        cargarEstablecimiento();

        // Crear formulario
        FormLayout formLayout = crearFormulario();

        // Botón para guardar cambios
        Button btnGuardar = new Button("Guardar Cambios", e -> guardarCambios());
        btnGuardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnGuardar.getStyle().set("background-color", COLOR2);

        HorizontalLayout buttonLayout = new HorizontalLayout(btnGuardar);
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // Layout principal
        VerticalLayout mainLayout = new VerticalLayout(titulo, formLayout, buttonLayout);
        mainLayout.setWidth("800px");
        mainLayout.setPadding(true);
        mainLayout.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        add(mainLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar que el usuario esté autenticado y sea admin o manager
        Empleado empleado = sessionService.getEmpleado();
        if (empleado == null || (!empleado.esAdministrador() && !empleado.esManager())) {
            event.rerouteTo("");
            Notification.show("Acceso denegado.")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void cargarEstablecimiento() {
        Optional<Establecimiento> establecimientoOpt = establecimientoService.obtenerEstablecimiento();
        if (establecimientoOpt.isPresent()) {
            this.establecimiento = establecimientoOpt.get();
        } else {
            // Crear establecimiento inicial si no existe
            this.establecimiento = establecimientoService.crearEstablecimientoInicial();
        }
    }

    private FormLayout crearFormulario() {
        // Campo teléfono
        telefonoField = new TextField("Teléfono");
        telefonoField.setWidthFull();
        telefonoField.setPlaceholder("Ej: 987654321");
        telefonoField.setValue(establecimiento.getTelefono() != null ? establecimiento.getTelefono() : "");

        // Campo dirección
        direccionField = new TextField("Dirección");
        direccionField.setWidthFull();
        direccionField.setPlaceholder("Ej: Calle Principal, 123");
        direccionField.setValue(establecimiento.getDireccion() != null ? establecimiento.getDireccion() : "");

        // Campo horario
        horarioField = new TextField("Horario");
        horarioField.setWidthFull();
        horarioField.setPlaceholder("Ej: Lunes a Viernes: 9:00 - 22:00");
        horarioField.setValue(establecimiento.getHorario() != null ? establecimiento.getHorario() : "");

        // Campo información
        informacionField = new TextArea("Información");
        informacionField.setWidthFull();
        informacionField.setHeight("200px");
        informacionField.setPlaceholder("Información adicional sobre el establecimiento...");
        informacionField.setValue(establecimiento.getInformacion() != null ? establecimiento.getInformacion() : "");

        FormLayout formLayout = new FormLayout();
        formLayout.add(telefonoField, direccionField, horarioField, informacionField);
        formLayout.setColspan(informacionField, 2);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        return formLayout;
    }

    private void guardarCambios() {
        try {
            establecimiento.setTelefono(telefonoField.getValue());
            establecimiento.setDireccion(direccionField.getValue());
            establecimiento.setHorario(horarioField.getValue());
            establecimiento.setInformacion(informacionField.getValue());

            establecimientoService.guardarEstablecimiento(establecimiento);

            Notification.show("Datos del establecimiento actualizados correctamente")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
