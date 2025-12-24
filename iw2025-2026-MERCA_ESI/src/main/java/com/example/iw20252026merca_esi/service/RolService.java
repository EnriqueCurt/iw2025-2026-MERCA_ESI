package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Rol;
import com.example.iw20252026merca_esi.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    @Transactional
    public Rol getOrCreateRole(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(nombre)));
    }

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }
    
    /**
     * Lista todos los roles disponibles
     */
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }
}
