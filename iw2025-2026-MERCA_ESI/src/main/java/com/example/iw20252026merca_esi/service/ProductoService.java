package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> findByCategoriaNombre(String nombreCategoria) {
        return productoRepository.findByCategoriaNombre(nombreCategoria);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }


    public List<Producto> listarProductosActivos() {
        return productoRepository.findByEstadoTrue();
    }
}


