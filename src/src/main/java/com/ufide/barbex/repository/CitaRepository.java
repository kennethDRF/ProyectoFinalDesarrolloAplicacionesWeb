package com.ufide.barbex.repository;

import com.ufide.barbex.entity.Cita;
import com.ufide.barbex.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByBarberoId(Long barberoId);

    List<Cita> findByClienteId(Long clienteId);

    List<Cita> findByBarberiaId(Long barberiaId);

    List<Cita> findByBarberoIdAndFecha(Long barberoId, LocalDate fecha);

    List<Cita> findByBarberoIdAndFechaAndEstadoNotAndEstadoNot(
            Long barberoId, LocalDate fecha, EstadoCita estado1, EstadoCita estado2);

    List<Cita> findByBarberoIdAndFechaAndHoraInicioBeforeAndHoraFinAfterAndEstadoNotAndEstadoNot(
            Long barberoId, LocalDate fecha, LocalTime horaFin, LocalTime horaInicio,
            EstadoCita estado1, EstadoCita estado2);

    List<Cita> findByBarberoIdAndEstadoNotAndEstadoNotAndFechaGreaterThanEqual(
            Long barberoId, EstadoCita estado1, EstadoCita estado2, LocalDate fechaDesde);
}
