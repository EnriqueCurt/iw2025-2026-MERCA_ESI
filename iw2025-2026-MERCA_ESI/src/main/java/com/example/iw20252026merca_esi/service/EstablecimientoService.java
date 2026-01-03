package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Establecimiento;
import com.example.iw20252026merca_esi.repository.EstablecimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstablecimientoService {

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    /**
     * Obtiene todos los establecimientos (normalmente será solo uno)
     */
    public List<Establecimiento> listarEstablecimientos() {
        return establecimientoRepository.findAll();
    }

    /**
     * Obtiene el establecimiento principal (el primero)
     */
    public Optional<Establecimiento> obtenerEstablecimiento() {
        List<Establecimiento> establecimientos = establecimientoRepository.findAll();
        if (!establecimientos.isEmpty()) {
            return Optional.of(establecimientos.get(0));
        }
        return Optional.empty();
    }

    /**
     * Busca un establecimiento por su ID
     */
    public Optional<Establecimiento> buscarEstablecimientoPorId(Integer id) {
        return establecimientoRepository.findById(id);
    }

    /**
     * Guarda o actualiza un establecimiento
     */
    @Transactional
    public Establecimiento guardarEstablecimiento(Establecimiento establecimiento) {
        return establecimientoRepository.save(establecimiento);
    }

    /**
     * Actualiza los datos de un establecimiento existente
     */
    @Transactional
    public Establecimiento actualizarEstablecimiento(Integer id, Establecimiento establecimientoActualizado) {
        Optional<Establecimiento> establecimientoExistente = establecimientoRepository.findById(id);
        
        if (establecimientoExistente.isPresent()) {
            Establecimiento establecimiento = establecimientoExistente.get();
            
            if (establecimientoActualizado.getTelefono() != null) {
                establecimiento.setTelefono(establecimientoActualizado.getTelefono());
            }
            if (establecimientoActualizado.getDireccion() != null) {
                establecimiento.setDireccion(establecimientoActualizado.getDireccion());
            }
            if (establecimientoActualizado.getHorario() != null) {
                establecimiento.setHorario(establecimientoActualizado.getHorario());
            }
            if (establecimientoActualizado.getInformacion() != null) {
                establecimiento.setInformacion(establecimientoActualizado.getInformacion());
            }
            
            return establecimientoRepository.save(establecimiento);
        }
        
        throw new RuntimeException("Establecimiento no encontrado con ID: " + id);
    }

    /**
     * Crea el establecimiento inicial si no existe
     */
    @Transactional
    public Establecimiento crearEstablecimientoInicial() {
        List<Establecimiento> establecimientos = establecimientoRepository.findAll();
        if (establecimientos.isEmpty()) {
            Establecimiento establecimiento = new Establecimiento();
            establecimiento.setTelefono("987654321");
            establecimiento.setDireccion("Calle Principal, 123");
            establecimiento.setHorario("Lunes a Viernes: 9:00 - 22:00, Sábados y Domingos: 10:00 - 23:00");
            establecimiento.setInformacion("Bienvenidos a MercaESI, tu restaurante de confianza.");
            return establecimientoRepository.save(establecimiento);
        }
        return establecimientos.get(0);
    }
}
