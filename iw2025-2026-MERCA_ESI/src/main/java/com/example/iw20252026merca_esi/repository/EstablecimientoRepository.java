package com.example.iw20252026merca_esi.repository;

import com.example.iw20252026merca_esi.model.Establecimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Integer> {
}
