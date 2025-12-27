// src/main/java/com/example/iw20252026merca_esi/service/WebPushService.java
package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.PushSubscription;
import com.example.iw20252026merca_esi.repository.PushSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;
import java.util.List;

@Service
public class WebPushService {

    private static final Logger logger = LoggerFactory.getLogger(WebPushService.class);

    private final PushSubscriptionRepository subscriptionRepository;

    private PushService pushService;

    @Value("${webpush.vapid.public-key}")
    private String publicKey;

    @Value("${webpush.vapid.private-key}")
    private String privateKey;

    @Value("${webpush.vapid.subject}")
    private String subject;

    public WebPushService(PushSubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        PublicKey pub = decodeRawUncompressedPublicKeyP256(publicKey);
        PrivateKey priv = decodeRawPrivateKeyP256(privateKey);

        PushService ps = new PushService();
        ps.setPublicKey(pub);
        ps.setPrivateKey(priv);
        ps.setSubject(subject);

        this.pushService = ps;

        logger.info("WebPush inicializado");
    }

    @Transactional
    public void guardarSuscripcion(String endpoint, String p256dh, String auth) {
        logger.info("Intentando guardar suscripción push...");
        logger.debug("Endpoint: {}", endpoint);
        logger.debug("p256dh length: {}", p256dh != null ? p256dh.length() : 0);
        logger.debug("auth length: {}", auth != null ? auth.length() : 0);

        if (endpoint == null || endpoint.isBlank()) {
            logger.error("Intento de guardar suscripción con endpoint vacío");
            throw new IllegalArgumentException("Endpoint vacío");
        }
        if (p256dh == null || p256dh.isBlank() || auth == null || auth.isBlank()) {
            logger.error("Intento de guardar suscripción con keys inválidas");
            throw new IllegalArgumentException("Keys inválidas");
        }

        try {
            PushSubscription subscription = subscriptionRepository.findByEndpoint(endpoint)
                    .orElseGet(() -> {
                        logger.info("Nueva suscripción detectada");
                        return new PushSubscription();
                    });

            subscription.setEndpoint(endpoint);
            subscription.setP256dh(p256dh);
            subscription.setAuth(auth);

            PushSubscription saved = subscriptionRepository.save(subscription);
            logger.info("Suscripción guardada exitosamente con ID: {}", saved.getId());
        } catch (Exception e) {
            logger.error("Error al guardar suscripción en BD", e);
            throw new RuntimeException("Error al guardar suscripción: " + e.getMessage(), e);
        }
    }

    public void enviarNotificacionATodos(String titulo, String mensaje) {
        List<PushSubscription> suscripciones = subscriptionRepository.findAll();
        logger.info("Enviando notificación a {} suscripciones", suscripciones.size());

        String payload = "{\"title\":\"" + escapeJson(titulo) + "\",\"body\":\"" + escapeJson(mensaje) + "\"}";

        int exitosas = 0;
        int fallidas = 0;

        for (PushSubscription sub : suscripciones) {
            try {
                Subscription subscription = new Subscription(
                        sub.getEndpoint(),
                        new Subscription.Keys(sub.getP256dh(), sub.getAuth())
                );

                Notification notification = new Notification(subscription, payload);
                pushService.send(notification);
                exitosas++;
                logger.debug("Notificación enviada a: {}", sub.getEndpoint().substring(0, Math.min(50, sub.getEndpoint().length())));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread interrumpido al enviar notificación push", e);
                return;
            } catch (Exception e) {
                fallidas++;
                logger.error("Error al enviar notificación push a endpoint (eliminando suscripción inválida): {}",
                    sub.getEndpoint().substring(0, Math.min(50, sub.getEndpoint().length())), e);
                // Eliminar suscripción inválida
                try {
                    subscriptionRepository.delete(sub);
                    logger.info("Suscripción inválida eliminada");
                } catch (Exception ex) {
                    logger.error("Error al eliminar suscripción inválida", ex);
                }
            }
        }

        logger.info("Notificaciones enviadas: {} exitosas, {} fallidas", exitosas, fallidas);
    }

    public String getPublicKey() {
        return publicKey;
    }

    private static PublicKey decodeRawUncompressedPublicKeyP256(String base64Url) throws Exception {
        byte[] raw = Base64.getUrlDecoder().decode(base64Url);
        if (raw.length != 65 || raw[0] != 0x04) {
            throw new IllegalArgumentException("Public key VAPID inválida (se espera punto no comprimido de 65 bytes)");
        }

        byte[] xBytes = new byte[32];
        byte[] yBytes = new byte[32];
        System.arraycopy(raw, 1, xBytes, 0, 32);
        System.arraycopy(raw, 33, yBytes, 0, 32);

        BigInteger x = new BigInteger(1, xBytes);
        BigInteger y = new BigInteger(1, yBytes);

        // Usar BouncyCastle explícitamente
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256r1");
        org.bouncycastle.math.ec.ECPoint point = spec.getCurve().createPoint(x, y);
        org.bouncycastle.jce.spec.ECPublicKeySpec pubKeySpec =
            new org.bouncycastle.jce.spec.ECPublicKeySpec(point, spec);

        KeyFactory kf = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        return kf.generatePublic(pubKeySpec);
    }

    private static PrivateKey decodeRawPrivateKeyP256(String base64Url) throws Exception {
        byte[] raw = Base64.getUrlDecoder().decode(base64Url);

        BigInteger s = new BigInteger(1, raw);

        // Usar BouncyCastle explícitamente
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256r1");
        org.bouncycastle.jce.spec.ECPrivateKeySpec privKeySpec =
            new org.bouncycastle.jce.spec.ECPrivateKeySpec(s, spec);

        KeyFactory kf = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        return kf.generatePrivate(privKeySpec);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
