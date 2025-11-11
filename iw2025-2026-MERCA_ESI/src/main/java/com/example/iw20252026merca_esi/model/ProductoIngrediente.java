package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_ingrediente")
public class ProductoIngrediente {
    
    @EmbeddedId
    private ProductoIngredienteId id;
    
    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @ManyToOne
    @MapsId("idIngrediente")
    @JoinColumn(name = "id_ingrediente", nullable = false)
    private Ingrediente ingrediente;
    
    @Column(nullable = false)
    private Float cantidad;
    
    // Constructores
    public ProductoIngrediente() {
    }
    
    public ProductoIngrediente(Producto producto, Ingrediente ingrediente, Float cantidad) {
        this.id = new ProductoIngredienteId(producto.getIdProducto(), ingrediente.getIdIngrediente());
        this.producto = producto;
        this.ingrediente = ingrediente;
        this.cantidad = cantidad;
    }
    
    // Getters y Setters
    public ProductoIngredienteId getId() {
        return id;
    }
    
    public void setId(ProductoIngredienteId id) {
        this.id = id;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Ingrediente getIngrediente() {
        return ingrediente;
    }
    
    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }
    
    public Float getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Float cantidad) {
        this.cantidad = cantidad;
    }
}