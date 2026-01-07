package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "detalle_pedido_menu")
@IdClass(DetallePedidoMenuId.class)
public class DetallePedidoMenu implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_menu", nullable = false)
    private Menu menu;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Float precioUnitario;

    @Column(nullable = false)
    private Float subtotal;

    // Constructores
    public DetallePedidoMenu() {
    }

    public DetallePedidoMenu(Pedido pedido, Menu menu, Integer cantidad, Float precioUnitario) {
        this.pedido = pedido;
        this.menu = menu;
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
