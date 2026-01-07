package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "detalle_pedido_producto")
@IdClass(DetallePedidoProductoId.class)
public class DetallePedidoProducto implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Float precioUnitario;

    @Column(nullable = false)
    private Float subtotal;

    @Column(length = 500)
    private String notas; // Ej: "Sin cebolla", "Extra queso"

    // Constructores
    public DetallePedidoProducto() {
    }

    public DetallePedidoProducto(Pedido pedido, Producto producto, Integer cantidad, Float precioUnitario) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    // Getters y Setters
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
        calcularSubtotal();
    }

    public Float getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Float precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public Float getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Float subtotal) {
        this.subtotal = subtotal;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // Métodos auxiliares
    private void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        }
    }

    @PrePersist
    @PreUpdate
    public void calcularSubtotalAntesDeGuardar() {
        calcularSubtotal();
    }
}
