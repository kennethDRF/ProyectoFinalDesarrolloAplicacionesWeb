package com.ufide.barbex.repository;

import com.ufide.barbex.entity.HorarioGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioGeneralRepository extends JpaRepository<HorarioGeneral, Long> {
    List<HorarioGeneral> findByBarberoId(Long barberoId);
    Optional<HorarioGeneral> findByBarberoIdAndDiaSemana(Long barberoId, DayOfWeek diaSemana);
}
