package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingrediente")
    private Integer idIngrediente;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false)
    private Boolean estado = true;
    
    @OneToMany(mappedBy = "ingrediente")
    private Set<ProductoIngrediente> productoIngredientes;
    
    // Constructores
    public Ingrediente() {
    }
    
    public Ingrediente(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = true;
    }
    
    // Getters y Setters
    public Integer getIdIngrediente() {
        return idIngrediente;
    }
    
    public void setIdIngrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
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
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    public Set<ProductoIngrediente> getProductoIngredientes() {
        return productoIngredientes;
    }
    
    public void setProductoIngredientes(Set<ProductoIngrediente> productoIngredientes) {
        this.productoIngredientes = productoIngredientes;
    }
}