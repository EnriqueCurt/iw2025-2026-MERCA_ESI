package com.example.iw20252026merca_esi.config;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.model.Rol;
import com.example.iw20252026merca_esi.repository.EmpleadoRepository;
import com.example.iw20252026merca_esi.repository.RolRepository;
import com.example.iw20252026merca_esi.service.RolService;
import com.example.iw20252026merca_esi.util.RolEnum;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.Set;

/**
 * Inicializa datos básicos en la base de datos al arrancar la aplicación.
 * Crea los roles necesarios si no existen.
 */
@Component
public class StartupDataLoader implements ApplicationRunner {

    @Autowired
    private RolService rolService;

    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Crear roles si no existen
        for (RolEnum rolEnum : RolEnum.values()) {
            rolService.getOrCreateRole(rolEnum.getNombre());
        }
        
        System.out.println("✓ Roles inicializados: ADMINISTRADOR, EMPLEADO, REPARTIDOR, MANAGER, PROPIETARIO");
        
        // Crear usuario admin si no existe
        Optional<Empleado> adminExistente = empleadoRepository.findByUsername("admin");
        
        if (adminExistente.isEmpty()) {
            Empleado admin = new Empleado();
            admin.setNombre("admin");
            admin.setUsername("admin");
            admin.setContrasena(passwordEncoder.encode("123456"));
            admin.setEmail("admin@mail.com");
            admin.setTelefono(null);
            
            // Obtener el rol ADMINISTRADOR
            Rol rolAdmin = rolRepository.findByNombre("ADMINISTRADOR")
                    .orElseThrow(() -> new RuntimeException("Rol ADMINISTRADOR no encontrado"));
            
            Set<Rol> roles = new HashSet<>();
            roles.add(rolAdmin);
            admin.setRoles(roles);
            
            empleadoRepository.save(admin);
            System.out.println("✓ Usuario admin creado exitosamente");
        } else {
            System.out.println("✓ Usuario admin ya existe");
        }
    }
}
