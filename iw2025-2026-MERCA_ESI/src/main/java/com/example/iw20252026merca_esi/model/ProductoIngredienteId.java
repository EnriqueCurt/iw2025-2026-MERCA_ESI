package com.example.iw20252026merca_esi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProductoIngredienteId implements Serializable {
    
    @Column(name = "id_producto")
    private Integer idProducto;
    
    @Column(name = "id_ingrediente")
    private Integer idIngrediente;
    
    // Constructores
    public ProductoIngredienteId() {
    }
    
    public ProductoIngredienteId(Integer idProducto, Integer idIngrediente) {
        this.idProducto = idProducto;
        this.idIngrediente = idIngrediente;
    }
    
    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }
    
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    
    public Integer getIdIngrediente() {
        return idIngrediente;
    }
    
    public void setIdIngrediente(Integer idIngrediente) {
        this.idIngrediente = idIngrediente;
    }
    
    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoIngredienteId that = (ProductoIngredienteId) o;
        return Objects.equals(idProducto, that.idProducto) && 
               Objects.equals(idIngrediente, that.idIngrediente);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idIngrediente);
    }
}