package com.example.iw20252026merca_esi.model;

import java.io.Serializable;
import java.util.Objects;

public class DetallePedidoProductoId implements Serializable {

    private Integer pedido;
    private Integer producto;

    public DetallePedidoProductoId() {
    }

    public DetallePedidoProductoId(Integer pedido, Integer producto) {
        this.pedido = pedido;
        this.producto = producto;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getProducto() {
        return producto;
    }

    public void setProducto(Integer producto) {
        this.producto = producto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedidoProductoId that = (DetallePedidoProductoId) o;
        return Objects.equals(pedido, that.pedido) && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, producto);
    }
}
