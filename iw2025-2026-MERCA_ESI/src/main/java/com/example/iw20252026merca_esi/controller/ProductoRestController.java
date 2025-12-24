package com.example.iw20252026merca_esi.controller;

import com.example.iw20252026merca_esi.model.Producto;
import com.example.iw20252026merca_esi.repository.ProductoRepository;
import com.example.iw20252026merca_esi.service.OpenFoodFactsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private OpenFoodFactsService openFoodFactsService;

    // Endpoint básico - solo tus productos
    @GetMapping
    public ResponseEntity<List<Producto>> getProductos() {
        List<Producto> productos = productoRepository.findAll();
        return ResponseEntity.ok(productos);
    }

    // Endpoint por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoPorId(@PathVariable Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // NUEVO: Endpoint que combina tus productos + API externa
    @GetMapping("/enriquecidos")
    public ResponseEntity<List<ProductoEnriquecido>> getProductosEnriquecidos() {
        // 1. Obtener todos los productos de tu base de datos
        List<Producto> productos = productoRepository.findAll();

        // 2. Para cada producto, buscar info nutricional en Open Food Facts
        List<ProductoEnriquecido> enriquecidos = productos.stream()
                .map(producto -> {
                    ProductoEnriquecido enriched = new ProductoEnriquecido();
                    enriched.setProducto(producto);

                    // Buscar en Open Food Facts por nombre
                    try {
                        OpenFoodFactsService.FoodInfo info =
                                openFoodFactsService.buscarProductoPorNombre(producto.getNombre());
                        enriched.setInfoNutricional(info);
                        enriched.setEncontradoEnApi(info != null);
                    } catch (Exception e) {
                        // Si falla la API, continuar sin info nutricional
                        enriched.setInfoNutricional(null);
                        enriched.setEncontradoEnApi(false);
                    }

                    return enriched;
                })
                .collect(Collectors.toList());

        // 3. Devolver la respuesta combinada
        return ResponseEntity.ok(enriquecidos);
    }

    // Crear nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        producto.setEstado(true);
        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Clase auxiliar para la respuesta enriquecida
    public static class ProductoEnriquecido {
        private Producto producto;
        private OpenFoodFactsService.FoodInfo infoNutricional;
        private boolean encontradoEnApi;

        // Constructor vacío
        public ProductoEnriquecido() {}

        // Getters y Setters
        public Producto getProducto() {
            return producto;
        }

        public void setProducto(Producto producto) {
            this.producto = producto;
        }

        public OpenFoodFactsService.FoodInfo getInfoNutricional() {
            return infoNutricional;
        }

        public void setInfoNutricional(OpenFoodFactsService.FoodInfo infoNutricional) {
            this.infoNutricional = infoNutricional;
        }

        public boolean isEncontradoEnApi() {
            return encontradoEnApi;
        }

        public void setEncontradoEnApi(boolean encontradoEnApi) {
            this.encontradoEnApi = encontradoEnApi;
        }
    }
}



