package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;

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
}
