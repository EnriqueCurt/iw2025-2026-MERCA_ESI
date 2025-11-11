package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
    @Query("SELECT p FROM Pedido p WHERE DATE(p.fecha) = CURRENT_DATE")
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
}
