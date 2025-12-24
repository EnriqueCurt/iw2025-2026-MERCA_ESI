package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Cliente;
import com.example.iw20252026merca_esi.model.Empleado;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar la sesión del usuario (Cliente o Empleado)
 */
@Service
public class SessionService {
    
    private static final String CLIENTE_SESSION_KEY = "cliente_logueado";
    private static final String EMPLEADO_SESSION_KEY = "empleado_logueado";
    
    /**
     * Guarda un cliente en la sesión
     */
    public void setCliente(Cliente cliente) {
        VaadinSession.getCurrent().setAttribute(CLIENTE_SESSION_KEY, cliente);
    }
    
    /**
     * Obtiene el cliente de la sesión
     */
    public Cliente getCliente() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return null;
        Object attr = session.getAttribute(CLIENTE_SESSION_KEY);
        return attr instanceof Cliente ? (Cliente) attr : null;
    }
    
    /**
     * Guarda un empleado en la sesión
     */
    public void setEmpleado(Empleado empleado) {
        VaadinSession.getCurrent().setAttribute(EMPLEADO_SESSION_KEY, empleado);
    }
    
    /**
     * Obtiene el empleado de la sesión
     */
    public Empleado getEmpleado() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) return null;
        Object attr = session.getAttribute(EMPLEADO_SESSION_KEY);
        return attr instanceof Empleado ? (Empleado) attr : null;
    }
    
    /**
     * Verifica si hay algún usuario logueado (cliente o empleado)
     */
    public boolean isLoggedIn() {
        return getCliente() != null || getEmpleado() != null;
    }
    
    /**
     * Obtiene el nombre del usuario logueado
     */
    public String getNombreUsuario() {
        Cliente cliente = getCliente();
        if (cliente != null) {
            return cliente.getNombre();
        }
        
        Empleado empleado = getEmpleado();
        if (empleado != null) {
            return empleado.getNombre();
        }
        
        return null;
    }
    
    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(CLIENTE_SESSION_KEY, null);
            session.setAttribute(EMPLEADO_SESSION_KEY, null);
        }
    }
}
