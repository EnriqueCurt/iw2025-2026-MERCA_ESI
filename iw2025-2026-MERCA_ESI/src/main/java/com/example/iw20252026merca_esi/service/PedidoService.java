package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.*;
import com.example.iw20252026merca_esi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

/**
 * Servicio para gestionar pedidos confirmados en la base de datos.
 */
@Service
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final MenuRepository menuRepository;
    private final DetallePedidoProductoRepository detallePedidoProductoRepository;
    private final DetallePedidoMenuRepository detallePedidoMenuRepository;
    
    public PedidoService(
            PedidoRepository pedidoRepository,
            ProductoRepository productoRepository,
            MenuRepository menuRepository,
            DetallePedidoProductoRepository detallePedidoProductoRepository,
            DetallePedidoMenuRepository detallePedidoMenuRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.menuRepository = menuRepository;
        this.detallePedidoProductoRepository = detallePedidoProductoRepository;
        this.detallePedidoMenuRepository = detallePedidoMenuRepository;
    }
    
    /**
     * Confirma un pedido guardándolo en la base de datos.
     * @param items Lista de items del pedido actual (de la sesión)
     * @param cliente Cliente que realiza el pedido (obligatorio)
     * @param aDomicilio Si es pedido a domicilio
     * @param paraLlevar Si es pedido para llevar
     * @param direccion Dirección de entrega (si es a domicilio)
     * @return El pedido guardado
     */
    @Transactional
    public Pedido confirmarPedido(
            List<ItemPedido> items,
            Cliente cliente,
            Boolean aDomicilio,
            Boolean paraLlevar,
            String direccion) {
        
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El pedido no puede estar vacío");
        }
        
        if (cliente == null) {
            throw new IllegalArgumentException("El pedido debe estar asociado a un cliente");
        }
        
        // Calcular total
        Float total = (float) items.stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
        
        // Crear pedido
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE_PAGO"); // Estado inicial
        pedido.setTotal(total);
        pedido.setCliente(cliente);
        pedido.setADomicilio(aDomicilio != null ? aDomicilio : false);
        pedido.setParaLlevar(paraLlevar != null ? paraLlevar : false);
        pedido.setDireccion(direccion);
        pedido.setDetalleProductos(new HashSet<>());
        pedido.setDetalleMenus(new HashSet<>());
        
        // Guardar pedido primero para obtener el ID
        pedido = pedidoRepository.save(pedido);
        
        // Crear detalles del pedido
        for (ItemPedido item : items) {
            if (item.esProducto()) {
                agregarDetalleProducto(pedido, item);
            } else if (item.esMenu()) {
                agregarDetalleMenu(pedido, item);
            }
        }
        
        return pedido;
    }
    
    /**
     * Agrega un detalle de producto al pedido.
     */
    private void agregarDetalleProducto(Pedido pedido, ItemPedido item) {
        Producto producto = productoRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getId()));
        
        DetallePedidoProducto detalle = new DetallePedidoProducto();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(item.getCantidad());
        detalle.setPrecioUnitario(item.getPrecio());
        detalle.setSubtotal(item.getSubtotal());
        
        // Guardar exclusiones
        if (item.tieneExclusiones()) {
            detalle.setNotas(item.getTextoExclusiones());
            // Guardar IDs de ingredientes excluidos separados por comas
            // Para productos individuales, tomar la primera exclusión
            ItemPedido.ExclusionIngredientes exclusion = item.getExclusiones().get(0);
            String idsExcluidos = exclusion.getIngredientesExcluidos().stream()
                    .map(String::valueOf)
                    .collect(java.util.stream.Collectors.joining(","));
            detalle.setIngredientesExcluidos(idsExcluidos);
        }
        
        // No llamar a save() - el cascade del pedido se encargará
        pedido.getDetalleProductos().add(detalle);
    }
    
    /**
     * Agrega un detalle de menú al pedido.
     */
    private void agregarDetalleMenu(Pedido pedido, ItemPedido item) {
        Menu menu = menuRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("Menú no encontrado: " + item.getId()));
        
        DetallePedidoMenu detalle = new DetallePedidoMenu();
        detalle.setPedido(pedido);
        detalle.setMenu(menu);
        detalle.setCantidad(item.getCantidad());
        detalle.setPrecioUnitario(item.getPrecio());
        detalle.setSubtotal(item.getSubtotal());
        
        // Guardar exclusiones de menús
        if (item.tieneExclusiones()) {
            // Para menús, concatenar todas las exclusiones de todos los productos
            StringBuilder idsExcluidos = new StringBuilder();
            for (int i = 0; i < item.getExclusiones().size(); i++) {
                ItemPedido.ExclusionIngredientes exclusion = item.getExclusiones().get(i);
                if (exclusion.tieneExclusiones()) {
                    String ids = exclusion.getIngredientesExcluidos().stream()
                            .map(String::valueOf)
                            .collect(java.util.stream.Collectors.joining(","));
                    if (idsExcluidos.length() > 0) {
                        idsExcluidos.append(";");
                    }
                    idsExcluidos.append(exclusion.getIdProducto()).append(":").append(ids);
                }
            }
            if (idsExcluidos.length() > 0) {
                detalle.setIngredientesExcluidos(idsExcluidos.toString());
            }
        }
        
        // No llamar a save() - el cascade del pedido se encargará
        pedido.getDetalleMenus().add(detalle);
    }
    
    /**
     * Obtiene todos los pedidos.
     */
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }
    
    /**
     * Obtiene un pedido por ID.
     */
    public Pedido buscarPorId(Integer id) {
        return pedidoRepository.findById(id).orElse(null);
    }
    
    /**
     * Actualiza el estado de un pedido.
     */
    @Transactional
    public void actualizarEstado(Integer idPedido, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }
    
    /**
     * Marca un pedido como pagado (cambia de PENDIENTE_PAGO a EN_COCINA).
     */
    @Transactional
    public void marcarComoPagado(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        
        if (!"PENDIENTE_PAGO".equals(pedido.getEstado())) {
            throw new IllegalStateException("Solo se pueden marcar como pagados los pedidos pendientes de pago");
        }
        
        pedido.setEstado("EN_COCINA");
        pedidoRepository.save(pedido);
    }
}
