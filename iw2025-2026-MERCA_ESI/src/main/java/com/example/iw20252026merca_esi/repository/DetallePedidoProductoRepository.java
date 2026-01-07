package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.DetallePedidoProducto;
import com.example.iw20252026merca_esi.model.DetallePedidoProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoProductoRepository extends JpaRepository<DetallePedidoProducto, DetallePedidoProductoId> {
    
    // Buscar todos los detalles de un pedido específico
    List<DetallePedidoProducto> findByPedidoIdPedido(Integer idPedido);
    
    // Buscar todos los pedidos que contienen un producto específico
    List<DetallePedidoProducto> findByProductoIdProducto(Integer idProducto);
}
