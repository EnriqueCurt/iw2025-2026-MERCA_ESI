package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    
    // Buscar empleado por email
    Optional<Empleado> findByEmail(String email);
    
    // Buscar empleado por teléfono
    Optional<Empleado> findByTelefono(String telefono);
    
    // Buscar empleados por nombre (búsqueda parcial, case insensitive)
    List<Empleado> findByNombreContainingIgnoreCase(String nombre);
    
    // Verificar si existe un empleado con ese email
    boolean existsByEmail(String email);
    
    // Buscar empleados por rol
    @Query("SELECT e FROM Empleado e JOIN e.roles r WHERE r.idRol = :idRol")
    List<Empleado> findByRol(Integer idRol);
    
    // Buscar empleados por nombre de rol
    @Query("SELECT e FROM Empleado e JOIN e.roles r WHERE r.nombre = :nombreRol")
    List<Empleado> findByNombreRol(String nombreRol);
}
