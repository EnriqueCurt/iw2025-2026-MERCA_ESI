// Java
package com.example.iw20252026merca_esi.config;

import com.example.iw20252026merca_esi.model.Categoria;
import com.example.iw20252026merca_esi.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

//@Component
public class CargarCategoriasBase implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;

    public CargarCategoriasBase(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Deshabilitado: No cargar categorías predeterminadas
        // createIfNotExists("Pizza");
        // createIfNotExists("Bebida");
        // createIfNotExists("Burger");
    }

    private void createIfNotExists(String nombre) {
        if (!categoriaRepository.existsByNombre(nombre)) {
            categoriaRepository.save(new Categoria(nombre));
        }
    }
}
