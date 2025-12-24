package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "menus")
public class Menu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu")
    private Integer idMenu;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false)
    private Float precio;
    
    @Column(name = "es_oferta", nullable = false)
    private Boolean esOferta = false;
    
    @Column(nullable = false)
    private Boolean estado = true;
    
    @Column(nullable = false)
    private Boolean puntos = false;
    
    @ManyToMany
    @JoinTable(
        name = "menu_producto",
        joinColumns = @JoinColumn(name = "id_menu"),
        inverseJoinColumns = @JoinColumn(name = "id_producto")
    )
    private Set<Producto> productos;
    
    // Constructores
    public Menu() {
    }
    
    public Menu(String nombre, String descripcion, Float precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.esOferta = false;
        this.estado = true;
        this.puntos = false;
    }
    
    // Métodos de negocio
    public Float calcularPrecio() {
        // Puede ser personalizado según lógica de negocio
        // Por ejemplo, precio base o suma de productos con descuento
        return this.precio;
    }
    
    // Getters y Setters
    public Integer getIdMenu() {
        return idMenu;
    }
    
    public void setIdMenu(Integer idMenu) {
        this.idMenu = idMenu;
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
    
    public Boolean getEsOferta() {
        return esOferta;
    }
    
    public void setEsOferta(Boolean esOferta) {
        this.esOferta = esOferta;
    }
    
    public Boolean getEstado() {
        return estado;
    }
    
    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
    
    public Boolean getPuntos() {
        return puntos;
    }
    
    public void setPuntos(Boolean puntos) {
        this.puntos = puntos;
    }
    
    public Set<Producto> getProductos() {
        return productos;
    }
    
    public void setProductos(Set<Producto> productos) {
        this.productos = productos;
    }
}
