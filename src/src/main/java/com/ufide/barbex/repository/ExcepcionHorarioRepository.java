package com.ufide.barbex.repository;

import com.ufide.barbex.entity.ExcepcionHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExcepcionHorarioRepository extends JpaRepository<ExcepcionHorario, Long> {
    List<ExcepcionHorario> findByBarberoId(Long barberoId);
    Optional<ExcepcionHorario> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha);
}
