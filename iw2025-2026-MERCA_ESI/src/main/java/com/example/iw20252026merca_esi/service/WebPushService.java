package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.PushSubscription;
import com.example.iw20252026merca_esi.repository.PushSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;

@Service
public class WebPushService {

    @Autowired
    private PushSubscriptionRepository subscriptionRepository;

    private PushService pushService;

    @Value("${webpush.vapid.public-key}")
    private String publicKey;

    @Value("${webpush.vapid.private-key}")
    private String privateKey;

    @Value("${webpush.vapid.subject}")
    private String subject;

    @PostConstruct
    public void init() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        pushService = new PushService();
        pushService.setPublicKey(publicKey);
        pushService.setPrivateKey(privateKey);
        pushService.setSubject(subject);
    }

    public void guardarSuscripcion(String endpoint, String p256dh, String auth) {
        PushSubscription subscription = new PushSubscription();
        subscription.setEndpoint(endpoint);
        subscription.setP256dh(p256dh);
        subscription.setAuth(auth);
        subscriptionRepository.save(subscription);
    }

    public void enviarNotificacionATodos(String titulo, String mensaje) {
        List<PushSubscription> suscripciones = subscriptionRepository.findAll();

        String payload = String.format("{\"title\":\"%s\",\"body\":\"%s\"}", titulo, mensaje);

        for (PushSubscription sub : suscripciones) {
            try {
                Subscription subscription = new Subscription(
                    sub.getEndpoint(),
                    new Subscription.Keys(sub.getP256dh(), sub.getAuth())
                );

                Notification notification = new Notification(subscription, payload);
                pushService.send(notification);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getPublicKey() {
        return publicKey;
    }
}
