package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    // Buscar menús por nombre (búsqueda parcial, case insensitive)
    List<Menu> findByNombreContainingIgnoreCase(String nombre);

    // Buscar menús activos
    List<Menu> findByEstadoTrue();

    // Buscar menús en oferta
    List<Menu> findByEsOfertaTrue();

    // Buscar menús activos y en oferta
    List<Menu> findByEstadoTrueAndEsOfertaTrue();

    // Buscar menús por rango de precio
    List<Menu> findByPrecioBetween(Float precioMin, Float precioMax);

    // Buscar menús que dan puntos
    List<Menu> findByPuntosTrue();

    @Query("SELECT DISTINCT m FROM Menu m LEFT JOIN FETCH m.productos")
    List<Menu> findAllWithProductos();
}
