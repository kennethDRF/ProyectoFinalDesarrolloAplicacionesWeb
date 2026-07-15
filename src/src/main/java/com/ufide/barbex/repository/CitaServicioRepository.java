package com.ufide.barbex.repository;

import com.ufide.barbex.entity.CitaServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaServicioRepository extends JpaRepository<CitaServicio, Long> {
    List<CitaServicio> findByCitaId(Long citaId);
    void deleteByCitaId(Long citaId);
    List<CitaServicio> findByServicioId(Long servicioId);
    long countByServicioId(Long servicioId);
}
