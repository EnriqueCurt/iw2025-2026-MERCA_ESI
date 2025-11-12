package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
    // Buscar categoría por nombre exacto
    Optional<Categoria> findByNombre(String nombre);
    
    // Buscar categorías por nombre (búsqueda parcial, case insensitive)
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    
    // Verificar si existe una categoría con ese nombre
    boolean existsByNombre(String nombre);
}
