package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.*;
import com.example.iw20252026merca_esi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    /**
     * Cancela un pedido si aún está PENDIENTE_PAGO y pertenece al cliente.
     * Solo cambia el estado a CANCELADO y guarda el motivo.
     */
    @Transactional
    public Pedido cancelarPedidoSiPendiente(Integer idPedido, Integer idCliente, String motivo) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (!pedido.getCliente().getIdCliente().equals(idCliente)) {
            throw new IllegalStateException("No autorizado: el pedido no pertenece a este cliente");
        }

        if (!"PENDIENTE_PAGO".equals(pedido.getEstado())) {
            throw new IllegalStateException("Solo se pueden cancelar pedidos en estado Pendiente de Pago");
        }

        pedido.setEstado("CANCELADO");
        pedido.setMotivoCancelacion(motivo);
        pedido.setFechaCierre(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    /**
     * Convierte un pedido de la BD a la estructura del carrito (ItemPedido).
     * Solo permitido para el cliente propietario.
     */
    @Transactional(readOnly = true)
    public List<ItemPedido> cargarItemsDePedidoParaEdicion(Integer idPedido, Integer idCliente) {
        Pedido pedido = pedidoRepository.findByIdAndClienteWithDetalles(idPedido, idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (!"PENDIENTE_PAGO".equals(pedido.getEstado())) {
            throw new IllegalStateException("Solo se pueden modificar pedidos en estado Pendiente de Pago");
        }

        List<ItemPedido> items = new ArrayList<>();

        if (pedido.getDetalleProductos() != null) {
            for (DetallePedidoProducto dp : pedido.getDetalleProductos()) {
                ItemPedido item = ItemPedido.fromProducto(dp.getProducto());
                item.setCantidad(dp.getCantidad());
                // Recuperar exclusiones (si existe)
                if (dp.getIngredientesExcluidos() != null && !dp.getIngredientesExcluidos().trim().isEmpty()) {
                    // Nota: no tenemos los nombres aquí, solo IDs; se mantiene sin nombres
                    ItemPedido.ExclusionIngredientes exc = item.getExclusiones().get(0);
                    List<Integer> ids = parseIds(dp.getIngredientesExcluidos());
                    exc.setIngredientesExcluidos(ids);
                }
                items.add(item);
            }
        }

        if (pedido.getDetalleMenus() != null) {
            for (DetallePedidoMenu dm : pedido.getDetalleMenus()) {
                ItemPedido item = ItemPedido.fromMenu(dm.getMenu());
                item.setCantidad(dm.getCantidad());
                // Exclusiones de menús: formato "idProducto:1,2;idProducto:3,4"
                if (dm.getIngredientesExcluidos() != null && !dm.getIngredientesExcluidos().trim().isEmpty()) {
                    aplicarExclusionesDeMenu(item, dm.getIngredientesExcluidos());
                }
                items.add(item);
            }
        }

        return items;
    }

    /**
     * Convierte un pedido de la BD a la estructura del carrito (ItemPedido).
     * SIN validar estado - permite rehacer cualquier pedido.
     * Solo verifica que el pedido pertenezca al cliente.
     */
    @Transactional(readOnly = true)
    public List<ItemPedido> cargarItemsDePedidoSinValidacion(Integer idPedido, Integer idCliente) {
        Pedido pedido = pedidoRepository.findByIdAndClienteWithDetalles(idPedido, idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        // NO validamos el estado - permitimos rehacer cualquier pedido

        List<ItemPedido> items = new ArrayList<>();

        if (pedido.getDetalleProductos() != null) {
            for (DetallePedidoProducto dp : pedido.getDetalleProductos()) {
                ItemPedido item = ItemPedido.fromProducto(dp.getProducto());
                item.setCantidad(dp.getCantidad());
                // Recuperar exclusiones (si existe)
                if (dp.getIngredientesExcluidos() != null && !dp.getIngredientesExcluidos().trim().isEmpty()) {
                    ItemPedido.ExclusionIngredientes exc = item.getExclusiones().get(0);
                    List<Integer> ids = parseIds(dp.getIngredientesExcluidos());
                    exc.setIngredientesExcluidos(ids);
                }
                items.add(item);
            }
        }

        if (pedido.getDetalleMenus() != null) {
            for (DetallePedidoMenu dm : pedido.getDetalleMenus()) {
                ItemPedido item = ItemPedido.fromMenu(dm.getMenu());
                item.setCantidad(dm.getCantidad());
                // Exclusiones de menús: formato "idProducto:1,2;idProducto:3,4"
                if (dm.getIngredientesExcluidos() != null && !dm.getIngredientesExcluidos().trim().isEmpty()) {
                    aplicarExclusionesDeMenu(item, dm.getIngredientesExcluidos());
                }
                items.add(item);
            }
        }

        return items;
    }

    /**
     * Actualiza un pedido existente si está PENDIENTE_PAGO: sustituye detalles y recalcula total.
     */
    @Transactional
    public Pedido actualizarPedidoPendiente(
            Integer idPedido,
            Integer idCliente,
            List<ItemPedido> items,
            Boolean aDomicilio,
            Boolean paraLlevar,
            String direccion) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El pedido no puede estar vacío");
        }

        Pedido pedido = pedidoRepository.findByIdAndClienteWithDetalles(idPedido, idCliente)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        if (!"PENDIENTE_PAGO".equals(pedido.getEstado())) {
            throw new IllegalStateException("Solo se pueden modificar pedidos en estado Pendiente de Pago");
        }

        // Actualizar datos de entrega
        pedido.setADomicilio(aDomicilio != null ? aDomicilio : false);
        pedido.setParaLlevar(paraLlevar != null ? paraLlevar : false);
        pedido.setDireccion(direccion);

        // Limpiar detalles existentes (orphanRemoval = true)
        if (pedido.getDetalleProductos() != null) {
            pedido.getDetalleProductos().clear();
        }
        if (pedido.getDetalleMenus() != null) {
            pedido.getDetalleMenus().clear();
        }

        // Recalcular total
        Float total = (float) items.stream().mapToDouble(ItemPedido::getSubtotal).sum();
        pedido.setTotal(total);

        // Rellenar nuevos detalles reutilizando los helpers ya existentes
        for (ItemPedido item : items) {
            if (item.esProducto()) {
                agregarDetalleProducto(pedido, item);
            } else if (item.esMenu()) {
                agregarDetalleMenu(pedido, item);
            }
        }

        return pedidoRepository.save(pedido);
    }

    /**
     * Obtiene los pedidos de un cliente ordenados por fecha descendente (historial).
     */
    public List<Pedido> listarPedidosPorCliente(Integer idCliente) {
        return pedidoRepository.findByClienteIdClienteOrderByFechaDesc(idCliente);
    }

    private List<Integer> parseIds(String csv) {
        List<Integer> ids = new ArrayList<>();
        String[] parts = csv.split(",");
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) {
                try {
                    ids.add(Integer.parseInt(t));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return ids;
    }

    private void aplicarExclusionesDeMenu(ItemPedido menuItem, String raw) {
        // Esperado: "idProducto:1,2;idProducto:3".
        String[] bloque = raw.split(";");
        for (String b : bloque) {
            String t = b.trim();
            if (t.isEmpty() || !t.contains(":")) continue;
            String[] kv = t.split(":", 2);
            try {
                Integer idProducto = Integer.parseInt(kv[0].trim());
                List<Integer> ids = parseIds(kv[1]);
                menuItem.getExclusiones().stream()
                        .filter(e -> e.getIdProducto() != null && e.getIdProducto().equals(idProducto))
                        .findFirst()
                        .ifPresent(e -> e.setIngredientesExcluidos(ids));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
