package com.example.iw20252026merca_esi.controller;

import com.example.iw20252026merca_esi.service.WebPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/push")
public class PushController {

    @Autowired
    private WebPushService webPushService;

    @GetMapping("/public-key")
    public Map<String, String> getPublicKey() {
        return Map.of("publicKey", webPushService.getPublicKey());
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Map<String, String> subscription) {
        webPushService.guardarSuscripcion(
            subscription.get("endpoint"),
            subscription.get("p256dh"),
            subscription.get("auth")
        );
    }
}
