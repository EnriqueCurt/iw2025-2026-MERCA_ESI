package com.example.iw20252026merca_esi.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Representa un item en el pedido actual del cliente.
 * Puede ser un producto individual o un menú completo.
 */
public class ItemPedido implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public enum TipoItem {
        PRODUCTO,
        MENU
    }
    
    /**
     * Clase interna para representar exclusiones de ingredientes por producto.
     * Permite mapear qué ingredientes se excluyen de cada producto.
     */
    public static class ExclusionIngredientes implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Integer idProducto;
        private String nombreProducto;
        private List<Integer> ingredientesExcluidos;
        private List<String> nombresIngredientesExcluidos;
        
        public ExclusionIngredientes() {
            this.ingredientesExcluidos = new ArrayList<>();
            this.nombresIngredientesExcluidos = new ArrayList<>();
        }
        
        public ExclusionIngredientes(Integer idProducto, String nombreProducto) {
            this();
            this.idProducto = idProducto;
            this.nombreProducto = nombreProducto;
        }
        
        public Integer getIdProducto() { return idProducto; }
        public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
        
        public String getNombreProducto() { return nombreProducto; }
        public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
        
        public List<Integer> getIngredientesExcluidos() { return ingredientesExcluidos; }
        public void setIngredientesExcluidos(List<Integer> ingredientesExcluidos) { 
            this.ingredientesExcluidos = ingredientesExcluidos; 
        }
        
        public List<String> getNombresIngredientesExcluidos() { return nombresIngredientesExcluidos; }
        public void setNombresIngredientesExcluidos(List<String> nombresIngredientesExcluidos) { 
            this.nombresIngredientesExcluidos = nombresIngredientesExcluidos; 
        }
        
        public boolean tieneExclusiones() {
            return ingredientesExcluidos != null && !ingredientesExcluidos.isEmpty();
        }
        
        public String getTextoExclusiones() {
            if (!tieneExclusiones()) return "";
            return "SIN: " + String.join(", ", nombresIngredientesExcluidos);
        }
    }
    
    private TipoItem tipo;
    private Integer id; // ID del producto o menú
    private String nombre;
    private String descripcion;
    private Float precio;
    private Integer cantidad;
    
    // Exclusiones de ingredientes
    // Para producto individual: 1 elemento en la lista (el producto mismo)
    // Para menú: N elementos (uno por cada producto del menú)
    private List<ExclusionIngredientes> exclusiones;
    
    // Para menús, guardar referencia a los productos incluidos
    private String productosIncluidos; // Lista de nombres de productos del menú
    private List<Producto> productosDelMenu; // Lista completa de productos (para dialog de ingredientes)
    
    public ItemPedido() {
        this.cantidad = 1;
        this.exclusiones = new ArrayList<>();
    }
    
    // Constructor para producto
    public static ItemPedido fromProducto(Producto producto) {
        ItemPedido item = new ItemPedido();
        item.tipo = TipoItem.PRODUCTO;
        item.id = producto.getIdProducto();
        item.nombre = producto.getNombre();
        item.descripcion = producto.getDescripcion();
        item.precio = producto.getPrecio();
        item.cantidad = 1;
        
        // Inicializar estructura de exclusiones para el producto
        ExclusionIngredientes exclusion = new ExclusionIngredientes(
            producto.getIdProducto(), 
            producto.getNombre()
        );
        item.exclusiones.add(exclusion);
        
        return item;
    }
    
    // Constructor para menú
    public static ItemPedido fromMenu(Menu menu) {
        ItemPedido item = new ItemPedido();
        item.tipo = TipoItem.MENU;
        item.id = menu.getIdMenu();
        item.nombre = menu.getNombre();
        item.descripcion = menu.getDescripcion();
        item.precio = menu.getPrecio();
        item.cantidad = 1;
        
        // Construir lista de productos incluidos
        if (menu.getProductos() != null && !menu.getProductos().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Producto p : menu.getProductos()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(p.getNombre());
                
                // Inicializar estructura de exclusiones para cada producto del menú
                ExclusionIngredientes exclusion = new ExclusionIngredientes(
                    p.getIdProducto(), 
                    p.getNombre()
                );
                item.exclusiones.add(exclusion);
            }
            item.productosIncluidos = sb.toString();
            item.productosDelMenu = new ArrayList<>(menu.getProductos());
        }
        
        return item;
    }
    
    /**
     * Actualiza las exclusiones de ingredientes para un producto específico.
     * Para productos individuales, actualiza la única exclusión.
     * Para menús, actualiza la exclusión del producto especificado.
     */
    public void actualizarExclusiones(Integer idProducto, List<Ingrediente> ingredientesExcluidos) {
        ExclusionIngredientes exclusion = exclusiones.stream()
            .filter(e -> e.getIdProducto().equals(idProducto))
            .findFirst()
            .orElse(null);
        
        if (exclusion != null) {
            exclusion.setIngredientesExcluidos(
                ingredientesExcluidos.stream()
                    .map(Ingrediente::getIdIngrediente)
                    .collect(Collectors.toList())
            );
            exclusion.setNombresIngredientesExcluidos(
                ingredientesExcluidos.stream()
                    .map(Ingrediente::getNombre)
                    .collect(Collectors.toList())
            );
        }
    }
    
    /**
     * Obtiene el texto formateado de todas las exclusiones.
     */
    public String getTextoExclusiones() {
        return exclusiones.stream()
            .filter(ExclusionIngredientes::tieneExclusiones)
            .map(e -> {
                if (esMenu()) {
                    return e.getNombreProducto() + " " + e.getTextoExclusiones();
                } else {
                    return e.getTextoExclusiones();
                }
            })
            .collect(Collectors.joining("; "));
    }
    
    /**
     * Verifica si el item tiene alguna exclusión de ingredientes.
     */
    public boolean tieneExclusiones() {
        return exclusiones.stream().anyMatch(ExclusionIngredientes::tieneExclusiones);
    }
    
    public Float getSubtotal() {
        return precio * cantidad;
    }
    
    public boolean esProducto() {
        return tipo == TipoItem.PRODUCTO;
    }
    
    public boolean esMenu() {
        return tipo == TipoItem.MENU;
    }
    
    // Getters y Setters
    public TipoItem getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoItem tipo) {
        this.tipo = tipo;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Float getPrecio() {
        return precio;
    }
    
    public void setPrecio(Float precio) {
        this.precio = precio;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getProductosIncluidos() {
        return productosIncluidos;
    }
    
    public void setProductosIncluidos(String productosIncluidos) {
        this.productosIncluidos = productosIncluidos;
    }
    
    public List<Producto> getProductosDelMenu() {
        return productosDelMenu;
    }
    
    public void setProductosDelMenu(List<Producto> productosDelMenu) {
        this.productosDelMenu = productosDelMenu;
    }
    
    public List<ExclusionIngredientes> getExclusiones() {
        return exclusiones;
    }
    
    public void setExclusiones(List<ExclusionIngredientes> exclusiones) {
        this.exclusiones = exclusiones;
    }
}
