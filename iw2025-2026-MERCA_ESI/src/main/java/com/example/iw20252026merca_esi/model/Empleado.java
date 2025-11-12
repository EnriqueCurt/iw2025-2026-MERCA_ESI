package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "empleados")
public class Empleado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 60)
    private String contrasena;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 20)
    private String telefono;
    
    @ManyToMany
    @JoinTable(
        name = "empleado_rol",
        joinColumns = @JoinColumn(name = "id_empleado"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles;
    
    @OneToMany(mappedBy = "empleado")
    private Set<Pedido> pedidos;
    
    // Constructores
    public Empleado() {
    }
    
    public Empleado(String nombre, String username, String contrasena, String email, String telefono) {
        this.nombre = nombre;
        this.username = username;
        this.contrasena = contrasena;
        this.email = email;
        this.telefono = telefono;
    }
    
    // Getters y Setters
    public Integer getIdEmpleado() {
        return idEmpleado;
    }
    
    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public Set<Rol> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }
    
    public Set<Pedido> getPedidos() {
        return pedidos;
    }
    
    public void setPedidos(Set<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
