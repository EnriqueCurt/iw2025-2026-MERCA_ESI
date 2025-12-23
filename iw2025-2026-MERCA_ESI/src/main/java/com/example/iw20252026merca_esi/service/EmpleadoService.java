package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.repository.EmpleadoRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

@Service
public class EmpleadoService {
    private static final String ERROR_EMPLEADO_NO_ENCONTRADO = "Empleado no encontrado con id: ";
    private static final String ERROR_ROL_NO_ENCONTRADO = "Rol no encontrado: ";

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private com.example.iw20252026merca_esi.repository.RolRepository rolRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    /**
     * Obtiene todos los empleados
     */
    public List<Empleado> listarEmpleados() {
        return empleadoRepository.findAll();
    }

    /**
     * Busca un empleado por su ID
     */
    public Optional<Empleado> buscarEmpleadoPorId(Integer id) {
        return empleadoRepository.findById(id);
    }

    /**
     * Busca un empleado por su username
     */
    public Optional<Empleado> buscarEmpleadoPorUsername(String username) {
        return empleadoRepository.findByUsername(username);
    }

    /**
     * Busca un empleado por su email
     */
    public Optional<Empleado> buscarEmpleadoPorEmail(String email) {
        return empleadoRepository.findByEmail(email);
    }

    /**
     * Asigna un rol (por nombre) a un empleado
     */
    @Transactional
    public Empleado asignarRolAEmpleado(Integer idEmpleado, String nombreRol) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new IllegalArgumentException( ERROR_EMPLEADO_NO_ENCONTRADO + idEmpleado));

        com.example.iw20252026merca_esi.model.Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_ROL_NO_ENCONTRADO + nombreRol));

        Set<com.example.iw20252026merca_esi.model.Rol> roles = empleado.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(rol);
        empleado.setRoles(roles);

        return empleadoRepository.save(empleado);
    }

    /**
     * Quita un rol a un empleado
     */
    @Transactional
    public Empleado quitarRolAEmpleado(Integer idEmpleado, String nombreRol) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_EMPLEADO_NO_ENCONTRADO + idEmpleado));

        com.example.iw20252026merca_esi.model.Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_ROL_NO_ENCONTRADO + nombreRol));

        Set<com.example.iw20252026merca_esi.model.Rol> roles = empleado.getRoles();
        if (roles != null) {
            roles.remove(rol);
            empleado.setRoles(roles);
        }

        return empleadoRepository.save(empleado);
    }

    /**
     * Reemplaza los roles de un empleado por los nombres indicados (crea conjunto nuevo)
     */
    @Transactional
    public Empleado establecerRolesDeEmpleado(Integer idEmpleado, Set<String> nombresRol) {
        Empleado empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_EMPLEADO_NO_ENCONTRADO + idEmpleado));

        Set<com.example.iw20252026merca_esi.model.Rol> roles = new HashSet<>();
        for (String nombre : nombresRol) {
            com.example.iw20252026merca_esi.model.Rol rol = rolRepository.findByNombre(nombre)
                    .orElseThrow(() -> new IllegalArgumentException(ERROR_ROL_NO_ENCONTRADO + nombre));
            roles.add(rol);
        }

        empleado.setRoles(roles);
        return empleadoRepository.save(empleado);
    }

    /**
     * Registra un nuevo empleado con contraseña
     */
    @Transactional
    public Empleado registrarEmpleado(Empleado empleado) {
        // Verificar que no exista el username
        if (empleadoRepository.existsByUsername(empleado.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        // Verificar que no exista el email
        if (empleadoRepository.existsByEmail(empleado.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Hashear la contraseña antes de guardar
        if (empleado.getContrasena() != null && !empleado.getContrasena().isEmpty()) {
            empleado.setContrasena(passwordEncoder.encode(empleado.getContrasena()));
        }
        
        return empleadoRepository.save(empleado);
    }

    /**
     * Actualiza un empleado existente
     */
    @Transactional
    public Empleado actualizarEmpleado(Integer id, Empleado empleadoActualizado) {
        return empleadoRepository.findById(id)
                .map(empleado -> {
                    empleado.setNombre(empleadoActualizado.getNombre());
                    
                    // Actualizar username solo si cambió y no existe
                    if (!empleado.getUsername().equals(empleadoActualizado.getUsername())) {
                        if (empleadoRepository.existsByUsername(empleadoActualizado.getUsername())) {
                            throw new IllegalArgumentException("El username ya está en uso");
                        }
                        empleado.setUsername(empleadoActualizado.getUsername());
                    }

                    // Actualizar email solo si cambió y no existe
                    if (!empleado.getEmail().equals(empleadoActualizado.getEmail())) {
                        if (empleadoRepository.existsByEmail(empleadoActualizado.getEmail())) {
                            throw new IllegalArgumentException("El email ya está registrado");
                        }
                        empleado.setEmail(empleadoActualizado.getEmail());
                    }

                    // Si se proporciona una nueva contraseña (no vacía), hashearla y actualizarla
                    if (empleadoActualizado.getContrasena() != null && 
                        !empleadoActualizado.getContrasena().isEmpty()) {
                        empleado.setContrasena(passwordEncoder.encode(empleadoActualizado.getContrasena()));
                    }

                    empleado.setTelefono(empleadoActualizado.getTelefono());

                    return empleadoRepository.save(empleado);
                })
                .orElseThrow(() -> new IllegalArgumentException(ERROR_EMPLEADO_NO_ENCONTRADO + id));
    }

    /**
     * Elimina un empleado por su ID
     */
    @Transactional
    public void eliminarEmpleado(Integer id) {
        if (!empleadoRepository.existsById(id)) {
            throw new IllegalArgumentException(ERROR_EMPLEADO_NO_ENCONTRADO + id);
        }
        empleadoRepository.deleteById(id);
    }

    /**
     * Verifica las credenciales de un empleado
     * @return Optional con el empleado si las credenciales son correctas
     */
    @Transactional(readOnly = true)
    public Optional<Empleado> autenticar(String username, String contrasenaPlana) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsername(username);
        
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            // Verificar contraseña usando BCrypt
            if (passwordEncoder.matches(contrasenaPlana, empleado.getContrasena())) {
                // Los roles se cargan EAGER automáticamente ahora
                return Optional.of(empleado);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Verifica si un username está disponible
     */
    public boolean isUsernameDisponible(String username) {
        return !empleadoRepository.existsByUsername(username);
    }

    /**
     * Verifica si un email está disponible
     */
    public boolean isEmailDisponible(String email) {
        return !empleadoRepository.existsByEmail(email);
    }
    
    /**
     * Busca empleados por rol
     */
    public List<Empleado> buscarPorRol(Integer idRol) {
        return empleadoRepository.findByRol(idRol);
    }
    
    /**
     * Busca empleados por nombre de rol
     */
    public List<Empleado> buscarPorNombreRol(String nombreRol) {
        return empleadoRepository.findByNombreRol(nombreRol);
    }
}
