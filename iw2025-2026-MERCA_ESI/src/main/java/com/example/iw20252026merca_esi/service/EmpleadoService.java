package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Empleado;
import com.example.iw20252026merca_esi.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

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

        // Guardar contraseña tal cual (sin hashear por ahora)
        // TODO: Implementar BCrypt en producción
        
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

                    // Si se proporciona una nueva contraseña (no vacía), actualizarla
                    if (empleadoActualizado.getContrasena() != null && 
                        !empleadoActualizado.getContrasena().isEmpty()) {
                        empleado.setContrasena(empleadoActualizado.getContrasena());
                    }

                    empleado.setTelefono(empleadoActualizado.getTelefono());

                    return empleadoRepository.save(empleado);
                })
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con id: " + id));
    }

    /**
     * Elimina un empleado por su ID
     */
    @Transactional
    public void eliminarEmpleado(Integer id) {
        if (!empleadoRepository.existsById(id)) {
            throw new IllegalArgumentException("Empleado no encontrado con id: " + id);
        }
        empleadoRepository.deleteById(id);
    }

    /**
     * Verifica las credenciales de un empleado
     * @return Optional con el empleado si las credenciales son correctas
     */
    public Optional<Empleado> autenticar(String username, String contrasenaPlana) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsername(username);
        
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            // Comparar contraseñas directamente (sin hash por ahora)
            // TODO: Implementar BCrypt en producción
            if (contrasenaPlana.equals(empleado.getContrasena())) {
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
