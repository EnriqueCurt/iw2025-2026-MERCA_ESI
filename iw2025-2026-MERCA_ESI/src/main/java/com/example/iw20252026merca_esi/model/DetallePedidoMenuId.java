package com.example.iw20252026merca_esi.model;

import java.io.Serializable;
import java.util.Objects;

public class DetallePedidoMenuId implements Serializable {

    private Integer pedido;
    private Integer menu;

    public DetallePedidoMenuId() {
    }

    public DetallePedidoMenuId(Integer pedido, Integer menu) {
        this.pedido = pedido;
        this.menu = menu;
    }

    public Integer getPedido() {
        return pedido;
    }

    public void setPedido(Integer pedido) {
        this.pedido = pedido;
    }

    public Integer getMenu() {
        return menu;
    }

    public void setMenu(Integer menu) {
        this.menu = menu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedidoMenuId that = (DetallePedidoMenuId) o;
        return Objects.equals(pedido, that.pedido) && Objects.equals(menu, that.menu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedido, menu);
    }
}
