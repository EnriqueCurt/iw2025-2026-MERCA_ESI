package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Ingrediente;
import com.example.iw20252026merca_esi.repository.IngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional
    public Ingrediente guardarIngrediente(Ingrediente ingrediente) {
        return ingredienteRepository.save(ingrediente);
    }

    public List<Ingrediente> listarIngredientes() {
        return ingredienteRepository.findAll();
    }

    public List<Ingrediente> listarIngredientesActivos() {
        return ingredienteRepository.findByEstadoTrue();
    }

    public Optional<Ingrediente> buscarPorId(Integer id) {
        return ingredienteRepository.findById(id);
    }

    public Optional<Ingrediente> buscarPorNombre(String nombre) {
        return ingredienteRepository.findByNombre(nombre);
    }

    @Transactional
    public void eliminarIngrediente(Integer id) {
        ingredienteRepository.deleteById(id);
    }
}