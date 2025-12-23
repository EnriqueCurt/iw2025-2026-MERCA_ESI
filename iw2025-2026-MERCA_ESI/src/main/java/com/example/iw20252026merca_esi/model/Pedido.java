package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;
    
    @Column(nullable = false)
    private LocalDateTime fecha;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    
    @Column(nullable = false)
    private Float total;
    
    @Column(nullable = false, length = 50)
    private String estado;
    
    @Column(length = 255)
    private String direccion;
    
    @Column(name = "a_domicilio", nullable = false)
    private Boolean aDomicilio;
    
    @Column(name = "para_llevar", nullable = false)
    private Boolean paraLlevar;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_empleado")
    private Empleado empleado;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Set<DetallePedido> detallePedidos;
    
    // Constructores
    public Pedido() {
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE_PAGO";
        this.total = 0.0f;
        this.aDomicilio = false;
        this.paraLlevar = false;
    }
    
    public Pedido(Cliente cliente, Empleado empleado) {
        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = LocalDateTime.now();
        this.estado = "PENDIENTE_PAGO";
        this.total = 0.0f;
        this.aDomicilio = false;
        this.paraLlevar = false;
    }
    
    // Getters y Setters
    public Integer getIdPedido() {
        return idPedido;
    }
    
    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
    
    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }
    
    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
    
    public Float getTotal() {
        return total;
    }
    
    public void setTotal(Float total) {
        this.total = total;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public Empleado getEmpleado() {
        return empleado;
    }
    
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }
    
    public Set<DetallePedido> getDetallePedidos() {
        return detallePedidos;
    }
    
    public void setDetallePedidos(Set<DetallePedido> detallePedidos) {
        this.detallePedidos = detallePedidos;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public Boolean getADomicilio() {
        return aDomicilio;
    }
    
    public void setADomicilio(Boolean aDomicilio) {
        this.aDomicilio = aDomicilio;
    }
    
    public Boolean getParaLlevar() {
        return paraLlevar;
    }
    
    public void setParaLlevar(Boolean paraLlevar) {
        this.paraLlevar = paraLlevar;
    }
    
    // Métodos helper para trabajar con estados
    
    /**
     * Cambia el estado del pedido usando el enum EstadoPedido
     */
    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estado = estadoPedido.name();
    }
    
    /**
     * Obtiene el estado como enum EstadoPedido
     */
    public EstadoPedido getEstadoPedido() {
        try {
            return EstadoPedido.valueOf(this.estado);
        } catch (IllegalArgumentException e) {
            return EstadoPedido.PENDIENTE_PAGO;
        }
    }
    
    /**
     * Verifica si el pedido está en un estado específico
     */
    public boolean tieneEstado(EstadoPedido estadoPedido) {
        return this.estado.equals(estadoPedido.name());
    }
    
    /**
     * Obtiene la descripción legible del estado actual
     */
    public String getEstadoDescripcion() {
        return getEstadoPedido().getDescripcion();
    }
}
