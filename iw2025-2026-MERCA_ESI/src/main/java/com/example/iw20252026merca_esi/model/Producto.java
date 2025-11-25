package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "productos")
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;
    
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    private byte[] imagen;
    
    @ManyToMany
    @JoinTable(
        name = "producto_categoria",
        joinColumns = @JoinColumn(name = "id_producto"),
        inverseJoinColumns = @JoinColumn(name = "id_categoria")
    )
    private Set<Categoria> categorias;
    
    @ManyToMany(mappedBy = "productos")
    private Set<Menu> menus;
    
    @OneToMany(mappedBy = "producto")
    private Set<DetallePedido> detallePedidos;
        
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private Set<ProductoIngrediente> productoIngredientes;
    

    // Constructores
    public Producto() {
    }
    
    public Producto(String nombre, String descripcion, Float precio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.esOferta = false;
        this.estado = true;
        this.puntos = false;
    }
    
    // Métodos de negocio
    public Float calcularSubtotal(int cantidad) {
        return this.precio * cantidad;
    }
    
    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
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
    
    public Set<Categoria> getCategorias() {
        return categorias;
    }
    
    public void setCategorias(Set<Categoria> categorias) {
        this.categorias = categorias;
    }
    
    public Set<Menu> getMenus() {
        return menus;
    }
    
    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }
    
    public Set<DetallePedido> getDetallePedidos() {
        return detallePedidos;
    }
    
    public void setDetallePedidos(Set<DetallePedido> detallePedidos) {
        this.detallePedidos = detallePedidos;
    }
    public Set<ProductoIngrediente> getProductoIngredientes() {
        return productoIngredientes;
    }
    
    public void setProductoIngredientes(Set<ProductoIngrediente> productoIngredientes) {
        this.productoIngredientes = productoIngredientes;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
