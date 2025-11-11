package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.ProductoIngrediente;
import com.example.iw20252026merca_esi.model.ProductoIngredienteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoIngredienteRepository extends JpaRepository<ProductoIngrediente, ProductoIngredienteId> {
    
    // Buscar ingredientes de un producto
    List<ProductoIngrediente> findByIdIdProducto(Integer idProducto);
    
    // Buscar productos que contienen un ingrediente
    List<ProductoIngrediente> findByIdIdIngrediente(Integer idIngrediente);
    
    // Contar cuántos productos usan un ingrediente
    Long countByIdIdIngrediente(Integer idIngrediente);
    
    // Ingredientes más usados
    @Query("SELECT pi.ingrediente.idIngrediente, pi.ingrediente.nombre, COUNT(pi) as total " +
           "FROM ProductoIngrediente pi " +
           "GROUP BY pi.ingrediente.idIngrediente, pi.ingrediente.nombre " +
           "ORDER BY total DESC")
    List<Object[]> findIngredientesMasUsados();
}