package com.example.iw20252026merca_esi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "establecimiento")
public class Establecimiento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_establecimiento")
    private Integer idEstablecimiento;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 200)
    private String direccion;
    
    @Column(length = 100)
    private String horario;
    
    @Column(columnDefinition = "TEXT")
    private String informacion;
    
    // Constructor por defecto
    public Establecimiento() {
    }
    
    // Constructor con parámetros
    public Establecimiento(String telefono, String direccion, String horario, String informacion) {
        this.telefono = telefono;
        this.direccion = direccion;
        this.horario = horario;
        this.informacion = informacion;
    }
    
    // Getters y Setters
    public Integer getIdEstablecimiento() {
        return idEstablecimiento;
    }
    
    public void setIdEstablecimiento(Integer idEstablecimiento) {
        this.idEstablecimiento = idEstablecimiento;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getHorario() {
        return horario;
    }
    
    public void setHorario(String horario) {
        this.horario = horario;
    }
    
    public String getInformacion() {
        return informacion;
    }
    
    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }
}
