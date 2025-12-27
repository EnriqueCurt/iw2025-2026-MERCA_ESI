package com.example.iw20252026merca_esi.controller;

import com.example.iw20252026merca_esi.service.WebPushService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private static final Logger logger = LoggerFactory.getLogger(PushController.class);

    private final WebPushService webPushService;

    public PushController(WebPushService webPushService) {
        this.webPushService = webPushService;
    }

    // Acepta JSON de suscripción "plano" o envuelto { "subscription": { ... } }
    @PostMapping(value = "/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> subscribe(@RequestBody(required = false) JsonNode body) {
        logger.info("Recibida petición de suscripción push");
        logger.debug("Body recibido: {}", body);

        if (body == null || body.isNull()) {
            logger.error("Body vacío o no JSON");
            return ResponseEntity.badRequest().body("Suscripción inválida: body vacío o no JSON");
        }

        JsonNode subNode = body.hasNonNull("subscription") ? body.get("subscription") : body;

        String endpoint = textOrNull(subNode.get("endpoint"));
        JsonNode keys = subNode.get("keys");
        String p256dh = keys != null ? textOrNull(keys.get("p256dh")) : null;
        String auth = keys != null ? textOrNull(keys.get("auth")) : null;

        logger.debug("Endpoint: {}", endpoint);
        logger.debug("Keys p256dh: {}", p256dh != null ? "presente" : "ausente");
        logger.debug("Keys auth: {}", auth != null ? "presente" : "ausente");

        if (isBlank(endpoint) || isBlank(p256dh) || isBlank(auth)) {
            logger.error("Faltan campos requeridos - endpoint: {}, p256dh: {}, auth: {}",
                endpoint != null, p256dh != null, auth != null);
            return ResponseEntity.badRequest().body("Suscripción inválida: faltan endpoint/keys.p256dh/keys.auth");
        }

        try {
            webPushService.guardarSuscripcion(endpoint, p256dh, auth);
            logger.info("Suscripción guardada exitosamente");
            return ResponseEntity.ok().body("{\"message\":\"Suscripción guardada exitosamente\"}");
        } catch (Exception e) {
            logger.error("Error al procesar suscripción", e);
            return ResponseEntity.internalServerError()
                .body("{\"error\":\"Error al guardar suscripción: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/vapid-public-key")
    public ResponseEntity<String> vapidPublicKey() {
        String key = Objects.toString(webPushService.getPublicKey(), "");
        logger.debug("Enviando clave pública VAPID");
        return ResponseEntity.ok(key);
    }

    private static String textOrNull(JsonNode node) {
        return node == null || node.isNull() ? null : node.asText(null);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
