package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.model.Establecimiento;
import com.example.iw20252026merca_esi.service.EstablecimientoService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "/", layout = MainLayout.class)
public class MainView extends Div {

    private final EstablecimientoService establecimientoService;

    @Autowired
    public MainView(EstablecimientoService establecimientoService) {
        this.establecimientoService = establecimientoService;

        setSizeFull();
        getStyle()
                .set("background-image", "url('/images/pizita.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("min-height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("padding", "40px 20px")
                .set("overflow-y", "auto");

        // Cargar información del establecimiento
        Optional<Establecimiento> establecimientoOpt = establecimientoService.obtenerEstablecimiento();
        Establecimiento establecimiento = establecimientoOpt.orElse(null);

        // Título de bienvenida
        H1 welcomeMessage = new H1("Bienvenido a MercaESI");
        welcomeMessage.getStyle()
                .set("font-size", "48px")
                .set("font-weight", "bold")
                .set("color", "#ffffff")
                .set("text-shadow", "0 4px 12px rgba(0,0,0,0.6)")
                .set("margin", "0")
                .set("text-align", "center");

        // Contenedor principal con información del establecimiento
        VerticalLayout infoCard = new VerticalLayout();
        infoCard.getStyle()
                .set("background", "rgba(255, 255, 255, 0.95)")
                .set("padding", "30px")
                .set("border-radius", "16px")
                .set("box-shadow", "0 8px 24px rgba(0,0,0,0.3)")
                .set("max-width", "700px")
                .set("width", "100%")
                .set("margin", "20px 0");

        if (establecimiento != null) {
            // Información del establecimiento
            if (establecimiento.getInformacion() != null && !establecimiento.getInformacion().isEmpty()) {
                Paragraph info = new Paragraph(establecimiento.getInformacion());
                info.getStyle()
                        .set("font-size", "18px")
                        .set("color", "#333")
                        .set("text-align", "center")
                        .set("margin", "0 0 20px 0")
                        .set("line-height", "1.6");
                infoCard.add(info);
            }

            // Dirección
            if (establecimiento.getDireccion() != null && !establecimiento.getDireccion().isEmpty()) {
                HorizontalLayout direccionLayout = new HorizontalLayout();
                direccionLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
                Icon locationIcon = new Icon(VaadinIcon.MAP_MARKER);
                locationIcon.setColor("#D32F2F");
                locationIcon.getStyle().set("margin-right", "8px");
                Span direccion = new Span(establecimiento.getDireccion());
                direccion.getStyle()
                        .set("font-size", "16px")
                        .set("color", "#555");
                direccionLayout.add(locationIcon, direccion);
                infoCard.add(direccionLayout);
            }

            // Teléfono
            if (establecimiento.getTelefono() != null && !establecimiento.getTelefono().isEmpty()) {
                HorizontalLayout telefonoLayout = new HorizontalLayout();
                telefonoLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
                Icon phoneIcon = new Icon(VaadinIcon.PHONE);
                phoneIcon.setColor("#D32F2F");
                phoneIcon.getStyle().set("margin-right", "8px");
                Span telefono = new Span(establecimiento.getTelefono());
                telefono.getStyle()
                        .set("font-size", "16px")
                        .set("color", "#555");
                telefonoLayout.add(phoneIcon, telefono);
                infoCard.add(telefonoLayout);
            }

            // Horario
            if (establecimiento.getHorario() != null && !establecimiento.getHorario().isEmpty()) {
                HorizontalLayout horarioLayout = new HorizontalLayout();
                horarioLayout.setAlignItems(HorizontalLayout.Alignment.CENTER);
                Icon clockIcon = new Icon(VaadinIcon.CLOCK);
                clockIcon.setColor("#D32F2F");
                clockIcon.getStyle().set("margin-right", "8px");
                Span horario = new Span(establecimiento.getHorario());
                horario.getStyle()
                        .set("font-size", "16px")
                        .set("color", "#555");
                horarioLayout.add(clockIcon, horario);
                infoCard.add(horarioLayout);
            }
        }

        // Botones de navegación destacados
        H3 navegacionTitle = new H3("Explora nuestro catálogo");
        navegacionTitle.getStyle()
                .set("color", "#D32F2F")
                .set("margin", "30px 0 20px 0")
                .set("text-align", "center")
                .set("font-size", "24px");
        
        HorizontalLayout botonesLayout = new HorizontalLayout();
        botonesLayout.setSpacing(true);
        botonesLayout.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        botonesLayout.getStyle()
                .set("flex-wrap", "wrap")
                .set("gap", "20px");

        // Botón CARTA
        Button btnCarta = new Button("CARTA", new Icon(VaadinIcon.BOOK));
        btnCarta.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnCarta.getStyle()
                .set("background-color", "#e30613")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "20px 40px")
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("box-shadow", "0 4px 12px rgba(227, 6, 19, 0.4)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");
        btnCarta.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("carta")));

        // Botón MENÚS
        Button btnMenus = new Button("MENÚS", new Icon(VaadinIcon.LIST));
        btnMenus.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnMenus.getStyle()
                .set("background-color", "#e30613")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "20px 40px")
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("box-shadow", "0 4px 12px rgba(227, 6, 19, 0.4)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");
        btnMenus.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("menús")));

        // Botón OFERTAS
        Button btnOfertas = new Button("OFERTAS", new Icon(VaadinIcon.STAR));
        btnOfertas.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnOfertas.getStyle()
                .set("background-color", "#FF9800")
                .set("color", "white")
                .set("border-radius", "12px")
                .set("padding", "20px 40px")
                .set("font-size", "18px")
                .set("font-weight", "bold")
                .set("box-shadow", "0 4px 12px rgba(255, 152, 0, 0.4)")
                .set("cursor", "pointer")
                .set("transition", "all 0.3s ease");
        btnOfertas.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("ofertas")));

        botonesLayout.add(btnCarta, btnMenus, btnOfertas);
        infoCard.add(navegacionTitle, botonesLayout);

        // Contenedor principal con overlay
        Div overlay = new Div();
        overlay.add(welcomeMessage, infoCard);
        overlay.getStyle()
                .set("background", "rgba(0,0,0,0.3)")
                .set("padding", "40px")
                .set("border-radius", "20px")
                .set("backdrop-filter", "blur(5px)")
                .set("max-width", "800px")
                .set("width", "100%");

        add(overlay);

        // Registro del Service Worker para notificaciones push
        registrarServiceWorker();
        // Registro del Service Worker para notificaciones push
        registrarServiceWorker();
    }

    private void registrarServiceWorker() {
        getElement().executeJs(
                "if ('serviceWorker' in navigator && 'PushManager' in window) {" +
                        "  console.log('Iniciando proceso de suscripción push...');" +
                        "  Notification.requestPermission().then(function(permission) {" +
                        "    console.log('Permiso de notificaciones:', permission);" +
                        "    if (permission === 'granted') {" +
                        "      navigator.serviceWorker.register('/push-sw.js').then(function(registration) {" +
                        "        console.log('Service Worker registrado:', registration);" +
                        "        return registration.pushManager.getSubscription().then(function(subscription) {" +
                        "          if (subscription) {" +
                        "            console.log('Ya existe suscripción:', subscription);" +
                        "            return subscription;" +
                        "          }" +
                        "          console.log('Creando nueva suscripción...');" +
                        "          return registration.pushManager.subscribe({" +
                        "            userVisibleOnly: true," +
                        "            applicationServerKey: urlBase64ToUint8Array('BPBDhfJW56VVyf-MVZJfhfHvSnzaFBYN3HkOmj2zhu_YfJFH8ytnhBipLthBIhSrNoySd17msinm2GNXBuXiug8')" +
                        "          });" +
                        "        });" +
                        "      }).then(function(subscription) {" +
                        "        console.log('Suscripción obtenida, enviando al servidor...');" +
                        "        var subscriptionData = {" +
                        "          endpoint: subscription.endpoint," +
                        "          keys: {" +
                        "            p256dh: btoa(String.fromCharCode.apply(null, new Uint8Array(subscription.getKey('p256dh'))))," +
                        "            auth: btoa(String.fromCharCode.apply(null, new Uint8Array(subscription.getKey('auth'))))" +
                        "          }" +
                        "        };" +
                        "        console.log('Datos de suscripción:', subscriptionData);" +
                        "        return fetch('/api/push/subscribe', {" +
                        "          method: 'POST'," +
                        "          headers: {'Content-Type': 'application/json'}," +
                        "          body: JSON.stringify(subscriptionData)" +
                        "        }).then(function(response) {" +
                        "          console.log('Respuesta del servidor:', response.status);" +
                        "          if (!response.ok) {" +
                        "            return response.text().then(function(text) {" +
                        "              throw new Error('Error del servidor: ' + text);" +
                        "            });" +
                        "          }" +
                        "          return response.text();" +
                        "        }).then(function(data) {" +
                        "          console.log('Suscripción guardada exitosamente:', data);" +
                        "        });" +
                        "      }).catch(function(err) {" +
                        "        console.error('Error en suscripción push:', err);" +
                        "      });" +
                        "    } else {" +
                        "      console.log('Permiso de notificaciones denegado');" +
                        "    }" +
                        "  });" +
                        "} else {" +
                        "  console.log('Push no soportado en este navegador');" +
                        "}" +
                        "function urlBase64ToUint8Array(base64String) {" +
                        "  const padding = '='.repeat((4 - base64String.length % 4) % 4);" +
                        "  const base64 = (base64String + padding).replace(/\\-/g, '+').replace(/_/g, '/');" +
                        "  const rawData = window.atob(base64);" +
                        "  const outputArray = new Uint8Array(rawData.length);" +
                        "  for (let i = 0; i < rawData.length; ++i) {" +
                        "    outputArray[i] = rawData.charCodeAt(i);" +
                        "  }" +
                        "  return outputArray;" +
                        "}"
        );
    }
}