package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer> {
    
    // Buscar ingrediente por nombre exacto
    Optional<Ingrediente> findByNombre(String nombre);
    
    // Buscar ingredientes por nombre (búsqueda parcial, case insensitive)
    List<Ingrediente> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar ingredientes activos
    List<Ingrediente> findByEstadoTrue();
    
    // Verificar si existe un ingrediente con ese nombre
    boolean existsByNombre(String nombre);
}