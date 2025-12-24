package com.example.iw20252026merca_esi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class DetallePedidoId implements Serializable {
    
    @Column(name = "id_pedido")
    private Integer idPedido;
    
    @Column(name = "id_producto")
    private Integer idProducto;
    
    // Constructores
    public DetallePedidoId() {
    }
    
    public DetallePedidoId(Integer idPedido, Integer idProducto) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
    }
    
    // Getters y Setters
    public Integer getIdPedido() {
        return idPedido;
    }
    
    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }
    
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetallePedidoId that = (DetallePedidoId) o;
        return Objects.equals(idPedido, that.idPedido) && 
               Objects.equals(idProducto, that.idProducto);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idPedido, idProducto);
    }
}
