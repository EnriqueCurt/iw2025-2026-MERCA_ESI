package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.DetallePedido;
import com.example.iw20252026merca_esi.model.DetallePedidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, DetallePedidoId> {
    
    // Buscar detalles por pedido
    List<DetallePedido> findByPedidoIdPedido(Integer idPedido);
    
    // Buscar detalles por producto
    List<DetallePedido> findByProductoIdProducto(Integer idProducto);
    
    // Calcular total de un pedido
    @Query("SELECT SUM(d.cantidad * d.precioUnitario) FROM DetallePedido d WHERE d.pedido.idPedido = :idPedido")
    Float calcularTotalPedido(Integer idPedido);
    
    // Productos más vendidos (top N)
    @Query("SELECT d.producto.idProducto, d.producto.nombre, SUM(d.cantidad) as total " +
           "FROM DetallePedido d " +
           "GROUP BY d.producto.idProducto, d.producto.nombre " +
           "ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos();
    
    // Contar cuántas veces se ha pedido un producto
    @Query("SELECT SUM(d.cantidad) FROM DetallePedido d WHERE d.producto.idProducto = :idProducto")
    Long contarVentasProducto(Integer idProducto);
}
