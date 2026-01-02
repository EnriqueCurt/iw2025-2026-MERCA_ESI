package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByNombre(String nombre);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM Producto p WHERE p.estado = true")
    List<Producto> findByEstadoTrue();

    List<Producto> findByEsOfertaTrue();

    List<Producto> findByEstadoTrueAndEsOfertaTrue();

    List<Producto> findByPrecioBetween(Float precioMin, Float precioMax);

    List<Producto> findByPuntosTrue();

    @Query("SELECT p FROM Producto p JOIN p.categorias c WHERE c.idCategoria = :idCategoria AND p.estado = true")
    List<Producto> findByCategoria(Integer idCategoria);

    @Query("SELECT p FROM Producto p JOIN p.categorias c WHERE c.nombre = :nombreCategoria AND p.estado = true")
    List<Producto> findByCategoriaNombre(String nombreCategoria);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.categorias WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithCategorias(Integer id);

    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.categorias ORDER BY p.nombre ASC")
    List<Producto> findAllWithCategorias();
}

