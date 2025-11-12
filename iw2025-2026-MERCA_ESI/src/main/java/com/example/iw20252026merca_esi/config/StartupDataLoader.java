package com.example.iw20252026merca_esi.config;

import com.example.iw20252026merca_esi.service.RolService;
import com.example.iw20252026merca_esi.util.RolEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializa datos básicos en la base de datos al arrancar la aplicación.
 * Crea los roles necesarios si no existen.
 */
@Component
public class StartupDataLoader implements ApplicationRunner {

    @Autowired
    private RolService rolService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Crear roles si no existen
        for (RolEnum rolEnum : RolEnum.values()) {
            rolService.getOrCreateRole(rolEnum.getNombre());
        }
        
        System.out.println("✓ Roles inicializados: ADMINISTRADOR, EMPLEADO, REPARTIDOR, MANAGER, PROPIETARIO");
    }
}
