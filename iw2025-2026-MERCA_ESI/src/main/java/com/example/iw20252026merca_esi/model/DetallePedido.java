package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedidos")
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "id_menu")
    private Menu menu;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Float precioUnitario;

    public DetallePedido() {}

    // Constructor para producto
    public DetallePedido(Pedido pedido, Producto producto, Integer cantidad, Float precioUnitario) {
        this.pedido = pedido;
        this.producto = producto;
        this.menu = null;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Constructor para menu
    public DetallePedido(Pedido pedido, Menu menu, Integer cantidad, Float precioUnitario) {
        this.pedido = pedido;
        this.menu = menu;
        this.producto = null;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Float calcularSubtotal() {
        return this.precioUnitario * this.cantidad;
    }

    /**
     * Helper: indica si la entidad tiene exactamente uno de producto/menu.
     * Validación adicional recomendable en el servicio antes de persistir.
     */
    public boolean tieneProductoOmenuValido() {
        return (this.producto == null) ^ (this.menu == null);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}

/**
@Entity
@Table(name = "detalle_pedidos")
public class DetallePedido {
    
    @EmbeddedId
    private DetallePedidoId id;
    
    @ManyToOne
    @MapsId("idPedido")
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(name = "precio_unitario", nullable = false)
    private Float precioUnitario;
    
    // Constructores
    public DetallePedido() {
    }
    
    public DetallePedido(Pedido pedido, Producto producto, Integer cantidad, Float precioUnitario) {
        this.id = new DetallePedidoId(pedido.getIdPedido(), producto.getIdProducto());
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    
    // Métodos de negocio
    public Float calcularSubtotal() {
        return this.precioUnitario * this.cantidad;
    }
    
    // Getters y Setters
    public DetallePedidoId getId() {
        return id;
    }
    
    public void setId(DetallePedidoId id) {
        this.id = id;
    }
    
    public Pedido getPedido() {
        return pedido;
    }
    
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public Float getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(Float precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}*/
