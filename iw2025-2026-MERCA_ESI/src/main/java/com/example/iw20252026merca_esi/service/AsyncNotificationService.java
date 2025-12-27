package com.example.iw20252026merca_esi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio para procesar notificaciones de forma asíncrona
 * Usando Spring @Async para ejecutar en background sin bloquear el hilo principal
 */
@Service
public class AsyncNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncNotificationService.class);

    private final WebPushService webPushService;

    public AsyncNotificationService(WebPushService webPushService) {
        this.webPushService = webPushService;
    }

    /**
     * Envía notificaciones push de forma asíncrona
     * Este método se ejecuta en un thread separado sin bloquear el hilo principal
     *
     * @param titulo Título de la notificación
     * @param mensaje Mensaje de la notificación
     */
    @Async("taskExecutor")
    public void enviarNotificacionAsync(String titulo, String mensaje) {
        String threadName = Thread.currentThread().getName();
        logger.info("📤 [{}] Enviando notificación asíncrona: '{}'", threadName, titulo);

        try {
            // Ejecutar envío de notificaciones en background
            webPushService.enviarNotificacionATodos(titulo, mensaje);

            logger.info("✅ [{}] Notificación enviada exitosamente", threadName);

        } catch (Exception e) {
            logger.error("❌ [{}] Error al enviar notificación asíncrona", threadName, e);
            // No lanzar excepción para no afectar el hilo principal
        }
    }

    /**
     * Envía notificación con tipo específico (para futuras extensiones)
     *
     * @param titulo Título de la notificación
     * @param mensaje Mensaje de la notificación
     * @param tipo Tipo de notificación (PRODUCTO_NUEVO, PEDIDO_LISTO, etc.)
     */
    @Async("taskExecutor")
    public void enviarNotificacionAsync(String titulo, String mensaje, String tipo) {
        String threadName = Thread.currentThread().getName();
        logger.info("📤 [{}] Enviando notificación asíncrona [{}]: '{}'", threadName, tipo, titulo);

        try {
            webPushService.enviarNotificacionATodos(titulo, mensaje);
            logger.info("✅ [{}] Notificación [{}] enviada exitosamente", threadName, tipo);

        } catch (Exception e) {
            logger.error("❌ [{}] Error al enviar notificación [{}]", threadName, tipo, e);
        }
    }
}

