package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    // Buscar pedidos por cliente
    List<Pedido> findByClienteIdCliente(Integer idCliente);
    
    // Buscar pedidos por empleado
    List<Pedido> findByEmpleadoIdEmpleado(Integer idEmpleado);
    
    // Buscar pedidos por estado
    List<Pedido> findByEstado(String estado);
    
    // Buscar pedidos por cliente y estado
    List<Pedido> findByClienteIdClienteAndEstado(Integer idCliente, String estado);
    
    // Buscar pedidos por rango de fechas
    List<Pedido> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Buscar pedidos de hoy
    @Query("SELECT p FROM Pedido p WHERE cast(p.fecha as LocalDate) = current_date")
    List<Pedido> findPedidosDeHoy();
    
    // Buscar pedidos por cliente ordenados por fecha (descendente)
    List<Pedido> findByClienteIdClienteOrderByFechaDesc(Integer idCliente);
    
    // Calcular total de ventas por rango de fechas
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fecha BETWEEN :fechaInicio AND :fechaFin AND p.estado = 'COMPLETADO'")
    Float calcularVentasPorPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Contar pedidos por estado
    Long countByEstado(String estado);
    
    // Buscar pedidos con total mayor a cierta cantidad
    List<Pedido> findByTotalGreaterThanEqual(Float total);
    
    // Buscar pedidos ordenados por fecha (ascendente - más antiguos primero)
    List<Pedido> findAllByOrderByFechaAsc();
    
    // Buscar pedidos ordenados por fecha (descendente - más recientes primero)
    List<Pedido> findAllByOrderByFechaDesc();
    
    // Buscar pedidos por estado ordenados por fecha ascendente
    List<Pedido> findByEstadoOrderByFechaAsc(String estado);
    
    // Buscar pedidos asignados a un empleado por estado, ordenados por fecha
    List<Pedido> findByEmpleadoIdEmpleadoAndEstadoOrderByFechaAsc(Integer idEmpleado, String estado);
    
    // Buscar pedidos listos para entregar o en reparto, ordenados por fecha
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('LISTO', 'EN_REPARTO') ORDER BY p.fecha ASC")
    List<Pedido> findPedidosParaEntrega();
    
    // Buscar pedidos con detalles cargados (fetch join)
    @Query("SELECT DISTINCT p FROM Pedido p LEFT JOIN FETCH p.detallePedidos ORDER BY p.fecha ASC")
    List<Pedido> findAllWithDetails();

    /**
     * Carga un pedido de un cliente con sus detalles (productos y menús) para edición.
     */
    @Query("SELECT DISTINCT p FROM Pedido p " +
            "LEFT JOIN FETCH p.detalleProductos dp " +
            "LEFT JOIN FETCH dp.producto " +
            "LEFT JOIN FETCH p.detalleMenus dm " +
            "LEFT JOIN FETCH dm.menu " +
            "WHERE p.idPedido = :idPedido AND p.cliente.idCliente = :idCliente")
    Optional<Pedido> findByIdAndClienteWithDetalles(Integer idPedido, Integer idCliente);
}
