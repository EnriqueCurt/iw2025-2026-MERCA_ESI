package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.DetallePedidoMenu;
import com.example.iw20252026merca_esi.model.DetallePedidoMenuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoMenuRepository extends JpaRepository<DetallePedidoMenu, DetallePedidoMenuId> {
    
    // Buscar todos los detalles de menús de un pedido específico con productos cargados
    @Query("SELECT dm FROM DetallePedidoMenu dm " +
           "JOIN FETCH dm.menu m " +
           "LEFT JOIN FETCH m.productos " +
           "WHERE dm.pedido.idPedido = :idPedido")
    List<DetallePedidoMenu> findByPedidoIdPedido(@Param("idPedido") Integer idPedido);
    
    // Buscar todos los pedidos que contienen un menú específico
    List<DetallePedidoMenu> findByMenuIdMenu(Integer idMenu);
}
