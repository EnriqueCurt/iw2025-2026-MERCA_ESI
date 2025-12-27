package com.example.iw20252026merca_esi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración de procesamiento asíncrono con Spring @Async
 * Permite ejecutar tareas en background sin bloquear el hilo principal
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Executor para tareas asíncronas
     * Configura un pool de threads para procesar tareas en paralelo
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Número mínimo de threads activos
        executor.setCorePoolSize(2);

        // Número máximo de threads
        executor.setMaxPoolSize(5);

        // Capacidad de la cola de espera
        executor.setQueueCapacity(100);

        // Prefijo para los nombres de los threads (útil para logs)
        executor.setThreadNamePrefix("Async-");

        // Inicializar el executor
        executor.initialize();

        return executor;
    }
}

