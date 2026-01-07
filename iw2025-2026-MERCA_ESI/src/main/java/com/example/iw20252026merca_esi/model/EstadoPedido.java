package com.example.iw20252026merca_esi.model;

/**
 * Enumeración que representa los diferentes estados de un pedido
 */
public enum EstadoPedido {
    PENDIENTE_PAGO("Pendiente de Pago"),
    EN_COCINA("En Cocina"),
    EN_REPARTO("En Reparto"),
    LISTO("Listo"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descripcion;
    
    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    @Override
    public String toString() {
        return descripcion;
    }
}
