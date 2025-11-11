package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Ingrediente;
import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.model.ProductoIngrediente;
import com.example.iw20252026merca_esi.repository.ProductoIngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoIngredienteService {

    private final ProductoIngredienteRepository productoIngredienteRepository;

    public ProductoIngredienteService(ProductoIngredienteRepository productoIngredienteRepository) {
        this.productoIngredienteRepository = productoIngredienteRepository;
    }

    @Transactional
    public ProductoIngrediente agregarIngredienteAProducto(Producto producto, Ingrediente ingrediente, Float cantidad) {
        ProductoIngrediente pi = new ProductoIngrediente(producto, ingrediente, cantidad);
        return productoIngredienteRepository.save(pi);
    }

    public List<ProductoIngrediente> listarIngredientesDeProducto(Integer idProducto) {
        return productoIngredienteRepository.findByIdIdProducto(idProducto);
    }

    public List<ProductoIngrediente> listarProductosConIngrediente(Integer idIngrediente) {
        return productoIngredienteRepository.findByIdIdIngrediente(idIngrediente);
    }

    @Transactional
    public void eliminarIngredienteDeProducto(Integer idProducto, Integer idIngrediente) {
        productoIngredienteRepository.deleteById(
            new com.example.iw20252026merca_esi.model.ProductoIngredienteId(idProducto, idIngrediente)
        );
    }
}