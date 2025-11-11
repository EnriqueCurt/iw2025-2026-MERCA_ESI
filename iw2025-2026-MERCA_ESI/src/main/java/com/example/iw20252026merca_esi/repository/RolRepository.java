package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    // Buscar rol por nombre exacto
    Optional<Rol> findByNombre(String nombre);
    
    // Buscar roles por nombre (búsqueda parcial, case insensitive)
    List<Rol> findByNombreContainingIgnoreCase(String nombre);
    
    // Verificar si existe un rol con ese nombre
    boolean existsByNombre(String nombre);
}
