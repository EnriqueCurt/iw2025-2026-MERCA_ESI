package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    // Buscar cliente por username
    Optional<Cliente> findByUsername(String username);
    
    // Buscar cliente por email
    Optional<Cliente> findByEmail(String email);
    
    // Buscar cliente por teléfono
    Optional<Cliente> findByTelefono(String telefono);
    
    // Verificar si existe un cliente con ese username
    boolean existsByUsername(String username);
    
    // Verificar si existe un cliente con ese email
    boolean existsByEmail(String email);
    
    // Buscar clientes por nombre (búsqueda parcial, case insensitive)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar clientes con más de X puntos
    List<Cliente> findByPuntosGreaterThanEqual(Integer puntos);
    
    // Buscar clientes ordenados por puntos (descendente)
    List<Cliente> findAllByOrderByPuntosDesc();
    
    // Top 10 clientes con más puntos
    @Query("SELECT c FROM Cliente c ORDER BY c.puntos DESC")
    List<Cliente> findTop10ByOrderByPuntosDesc();
}
