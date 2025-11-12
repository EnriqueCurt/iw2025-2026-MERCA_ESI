package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Obtiene todos los clientes
     */
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por su ID
     */
    public Optional<Cliente> buscarClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

    /**
     * Busca un cliente por su username
     */
    public Optional<Cliente> buscarClientePorUsername(String username) {
        return clienteRepository.findByUsername(username);
    }

    /**
     * Busca un cliente por su email
     */
    public Optional<Cliente> buscarClientePorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Registra un nuevo cliente con contraseña hasheada
     */
    @Transactional
    public Cliente registrarCliente(Cliente cliente) {
        // Verificar que no exista el username
        if (clienteRepository.existsByUsername(cliente.getUsername())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }

        // Verificar que no exista el email
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Guardar contraseña tal cual (sin hashear por ahora)
        // TODO: Implementar BCrypt en producción
        
        // Inicializar puntos si no están establecidos
        if (cliente.getPuntos() == null) {
            cliente.setPuntos(0);
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Actualiza un cliente existente
     * Si se proporciona una nueva contraseña, se hashea antes de guardar
     */
    @Transactional
    public Cliente actualizarCliente(Integer id, Cliente clienteActualizado) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(clienteActualizado.getNombre());
                    
                    // Actualizar username solo si cambió y no existe
                    if (!cliente.getUsername().equals(clienteActualizado.getUsername())) {
                        if (clienteRepository.existsByUsername(clienteActualizado.getUsername())) {
                            throw new IllegalArgumentException("El username ya está en uso");
                        }
                        cliente.setUsername(clienteActualizado.getUsername());
                    }

                    // Actualizar email solo si cambió y no existe
                    if (!cliente.getEmail().equals(clienteActualizado.getEmail())) {
                        if (clienteRepository.existsByEmail(clienteActualizado.getEmail())) {
                            throw new IllegalArgumentException("El email ya está registrado");
                        }
                        cliente.setEmail(clienteActualizado.getEmail());
                    }

                    // Si se proporciona una nueva contraseña (no vacía), actualizarla
                    if (clienteActualizado.getContrasena() != null && 
                        !clienteActualizado.getContrasena().isEmpty()) {
                        cliente.setContrasena(clienteActualizado.getContrasena());
                    }

                    cliente.setTelefono(clienteActualizado.getTelefono());
                    
                    if (clienteActualizado.getPuntos() != null) {
                        cliente.setPuntos(clienteActualizado.getPuntos());
                    }

                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + id));
    }

    /**
     * Elimina un cliente por su ID
     */
    @Transactional
    public void eliminarCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente no encontrado con id: " + id);
        }
        clienteRepository.deleteById(id);
    }

    /**
     * Verifica las credenciales de un cliente
     * @return Optional con el cliente si las credenciales son correctas
     */
    public Optional<Cliente> autenticar(String username, String contrasenaPlana) {
        Optional<Cliente> clienteOpt = clienteRepository.findByUsername(username);
        
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            // Comparar contraseñas directamente (sin hash por ahora)
            if (contrasenaPlana.equals(cliente.getContrasena())) {
                return Optional.of(cliente);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Añade puntos a un cliente
     */
    @Transactional
    public Cliente añadirPuntos(Integer idCliente, Integer puntos) {
        return clienteRepository.findById(idCliente)
                .map(cliente -> {
                    cliente.setPuntos(cliente.getPuntos() + puntos);
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con id: " + idCliente));
    }

    /**
     * Verifica si un username está disponible
     */
    public boolean isUsernameDisponible(String username) {
        return !clienteRepository.existsByUsername(username);
    }

    /**
     * Verifica si un email está disponible
     */
    public boolean isEmailDisponible(String email) {
        return !clienteRepository.existsByEmail(email);
    }
}
