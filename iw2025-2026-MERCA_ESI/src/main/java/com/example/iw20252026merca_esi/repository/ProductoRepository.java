package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Buscar productos por nombre (búsqueda parcial, case insensitive)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar productos activos
    List<Producto> findByEstadoTrue();
    
    // Buscar productos en oferta
    List<Producto> findByEsOfertaTrue();
    
    // Buscar productos activos y en oferta
    List<Producto> findByEstadoTrueAndEsOfertaTrue();
    
    // Buscar productos por rango de precio
    List<Producto> findByPrecioBetween(Float precioMin, Float precioMax);
    
    // Buscar productos que dan puntos
    List<Producto> findByPuntosTrue();
    
    // Buscar productos por categoría
    @Query("SELECT p FROM Producto p JOIN p.categorias c WHERE c.idCategoria = :idCategoria AND p.estado = true")
    List<Producto> findByCategoria(Integer idCategoria);
}
