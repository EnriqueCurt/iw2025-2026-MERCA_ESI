package com.example.iw20252026merca_esi.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.Image;

@Route(value = "/", layout = MainLayout.class)
public class MainView extends Div {

    public MainView() {

        setSizeFull();
        getStyle()
                .set("background-image", "url('/images/pizita.png')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat")
                .set("min-height", "100%")
                .set("display", "flex")
                .set("flex-direction", "column");

        Span welcomeMessage = new Span("Bienvenido a Merca-ESI");
        welcomeMessage.getStyle()
                .set("font-size", "32px")
                .set("font-weight", "bold")
                .set("color", "#ffffff")
                .set("text-shadow", "0 2px 6px rgba(0,0,0,0.4)")
                .set("margin", "40px auto 0 auto");

        Div overlay = new Div();
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
        overlay.add(welcomeMessage);
        overlay.getStyle()
                .set("background", "rgba(0,0,0,0.35)")
                .set("padding", "30px 40px")
                .set("border-radius", "16px")
                .set("backdrop-filter", "blur(3px)")
                .set("margin", "40px auto")
                .set("max-width", "600px")
                .set("width", "clamp(280px, 80%, 600px)")
                .set("text-align", "center");

        add(overlay);


    }
}