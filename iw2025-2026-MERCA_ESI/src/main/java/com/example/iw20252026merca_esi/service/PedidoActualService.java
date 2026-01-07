package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.ItemPedido;
import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.model.Producto;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar el pedido actual en construcción del cliente.
 * El pedido se almacena en la sesión hasta que el cliente lo confirma.
 */
@Service
public class PedidoActualService {
    
    private static final String PEDIDO_SESSION_KEY = "pedidoActual";
    
    /**
     * Obtiene el pedido actual de la sesión.
     */
    public List<ItemPedido> getPedidoActual() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        
        @SuppressWarnings("unchecked")
        List<ItemPedido> pedido = (List<ItemPedido>) session.getAttribute(PEDIDO_SESSION_KEY);
        
        if (pedido == null) {
            pedido = new ArrayList<>();
            session.setAttribute(PEDIDO_SESSION_KEY, pedido);
        }
        
        return pedido;
    }
    
    /**
     * Agrega un producto al pedido actual.
     * Si ya existe, incrementa la cantidad.
     */
    public void agregarProducto(Producto producto) {
        agregarItem(ItemPedido.fromProducto(producto));
    }
    
    /**
     * Agrega un item al pedido actual.
     * Si ya existe un item igual (mismo producto/menú Y mismas exclusiones), incrementa la cantidad.
     * Si las exclusiones son diferentes, se agrega como item separado.
     */
    public void agregarItem(ItemPedido nuevoItem) {
        List<ItemPedido> pedido = getPedidoActual();
        
        // Buscar si ya existe un item idéntico (mismo id Y mismas exclusiones)
        Optional<ItemPedido> itemExistente = pedido.stream()
            .filter(item -> item.getTipo() == nuevoItem.getTipo() 
                         && item.getId().equals(nuevoItem.getId())
                         && tienenMismasExclusiones(item, nuevoItem))
            .findFirst();
        
        if (itemExistente.isPresent()) {
            // Incrementar cantidad si son idénticos
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + 1);
        } else {
            // Agregar como nuevo item si tiene diferentes exclusiones
            pedido.add(nuevoItem);
        }
        
        guardarPedido(pedido);
    }
    
    /**
     * Compara si dos items tienen las mismas exclusiones.
     */
    private boolean tienenMismasExclusiones(ItemPedido item1, ItemPedido item2) {
        List<ItemPedido.ExclusionIngredientes> exc1 = item1.getExclusiones();
        List<ItemPedido.ExclusionIngredientes> exc2 = item2.getExclusiones();
        
        // Si ambos no tienen exclusiones, son iguales
        if ((exc1 == null || exc1.isEmpty()) && (exc2 == null || exc2.isEmpty())) {
            return true;
        }
        
        // Si uno tiene exclusiones y el otro no, son diferentes
        if ((exc1 == null || exc1.isEmpty()) || (exc2 == null || exc2.isEmpty())) {
            return false;
        }
        
        // Si tienen diferente número de exclusiones, son diferentes
        if (exc1.size() != exc2.size()) {
            return false;
        }
        
        // Para cada exclusión en item1, buscar la correspondiente en item2 por idProducto
        for (ItemPedido.ExclusionIngredientes e1 : exc1) {
            ItemPedido.ExclusionIngredientes e2 = exc2.stream()
                .filter(e -> e.getIdProducto().equals(e1.getIdProducto()))
                .findFirst()
                .orElse(null);
            
            if (e2 == null) {
                // No encontró el mismo producto en item2
                return false;
            }
            
            List<Integer> ing1 = e1.getIngredientesExcluidos();
            List<Integer> ing2 = e2.getIngredientesExcluidos();
            
            // Ambas listas vacías o null = sin exclusiones = iguales para este producto
            if ((ing1 == null || ing1.isEmpty()) && (ing2 == null || ing2.isEmpty())) {
                continue;
            }
            
            // Una tiene exclusiones y la otra no = diferentes
            if ((ing1 == null || ing1.isEmpty()) || (ing2 == null || ing2.isEmpty())) {
                return false;
            }
            
            // Comparar listas de ingredientes (sin importar orden)
            if (ing1.size() != ing2.size() || !ing1.containsAll(ing2) || !ing2.containsAll(ing1)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Agrega un producto al pedido actual.
     * Si ya existe, incrementa la cantidad.
     * @deprecated Usar agregarItem(ItemPedido) para manejar exclusiones correctamente
     */
    @Deprecated
    public void agregarProductoLegacy(Producto producto) {
        List<ItemPedido> pedido = getPedidoActual();
        
        // Buscar si ya existe el producto
        Optional<ItemPedido> itemExistente = pedido.stream()
            .filter(item -> item.esProducto() && item.getId().equals(producto.getIdProducto()))
            .findFirst();
        
        if (itemExistente.isPresent()) {
            // Incrementar cantidad
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + 1);
        } else {
            // Agregar nuevo item
            pedido.add(ItemPedido.fromProducto(producto));
        }
        
        guardarPedido(pedido);
    }
    
    /**
     * Agrega un menú al pedido actual.
     * Si ya existe con las mismas exclusiones, incrementa la cantidad.
     */
    public void agregarMenu(Menu menu) {
        agregarItem(ItemPedido.fromMenu(menu));
    }
    
    /**
     * Agrega un menú al pedido actual.
     * Si ya existe, incrementa la cantidad.
     * @deprecated Usar agregarItem(ItemPedido) para manejar exclusiones correctamente
     */
    @Deprecated
    public void agregarMenuLegacy(Menu menu) {
        List<ItemPedido> pedido = getPedidoActual();
        
        // Buscar si ya existe el menú
        Optional<ItemPedido> itemExistente = pedido.stream()
            .filter(item -> item.esMenu() && item.getId().equals(menu.getIdMenu()))
            .findFirst();
        
        if (itemExistente.isPresent()) {
            // Incrementar cantidad
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + 1);
        } else {
            // Agregar nuevo item
            pedido.add(ItemPedido.fromMenu(menu));
        }
        
        guardarPedido(pedido);
    }
    
    /**
     * Actualiza la cantidad de un item del pedido.
     * Busca el item exacto comparando tipo, id Y exclusiones.
     */
    public void actualizarCantidad(ItemPedido item, Integer nuevaCantidad) {
        if (nuevaCantidad <= 0) {
            eliminarItem(item);
            return;
        }
        
        List<ItemPedido> pedido = getPedidoActual();
        // Buscar el item exacto (mismo tipo, id y exclusiones)
        pedido.stream()
            .filter(i -> i.getTipo() == item.getTipo() 
                      && i.getId().equals(item.getId())
                      && tienenMismasExclusiones(i, item))
            .findFirst()
            .ifPresent(i -> i.setCantidad(nuevaCantidad));
        
        guardarPedido(pedido);
    }
    
    /**
     * Actualiza las notas de un item del pedido (solo productos).
     * @deprecated Usar actualizarExclusiones en su lugar
     */
    @Deprecated
    public void actualizarNotas(ItemPedido item, String notas) {
        // Método deprecated, mantenido por compatibilidad
    }
    
    /**
     * Elimina un item del pedido.
     * Busca el item exacto comparando tipo, id Y exclusiones.
     */
    public void eliminarItem(ItemPedido item) {
        List<ItemPedido> pedido = getPedidoActual();
        // Eliminar el item exacto (mismo tipo, id y exclusiones)
        pedido.removeIf(i -> i.getTipo() == item.getTipo() 
                          && i.getId().equals(item.getId())
                          && tienenMismasExclusiones(i, item));
        guardarPedido(pedido);
    }
    
    /**
     * Calcula el total del pedido actual.
     */
    public Float getTotal() {
        return (float) getPedidoActual().stream()
            .mapToDouble(ItemPedido::getSubtotal)
            .sum();
    }
    
    /**
     * Obtiene el número de items en el pedido.
     */
    public int getCantidadItems() {
        return getPedidoActual().stream()
            .mapToInt(ItemPedido::getCantidad)
            .sum();
    }
    
    /**
     * Limpia el pedido completamente.
     */
    public void limpiarPedido() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(PEDIDO_SESSION_KEY, new ArrayList<ItemPedido>());
        }
    }
    
    /**
     * Verifica si el pedido está vacío.
     */
    public boolean estaVacio() {
        return getPedidoActual().isEmpty();
    }
    
    /**
     * Guarda el pedido en la sesión.
     */
    private void guardarPedido(List<ItemPedido> pedido) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(PEDIDO_SESSION_KEY, pedido);
        }
    }
}
