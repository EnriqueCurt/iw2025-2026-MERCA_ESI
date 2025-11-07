package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "categorias")
public class Categoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @ManyToMany(mappedBy = "categorias")
    private Set<Producto> productos;
    
    // Constructores
    public Categoria() {
    }
    
    public Categoria(String nombre) {
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public Integer getIdCategoria() {
        return idCategoria;
    }
    
    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public Set<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }
}
