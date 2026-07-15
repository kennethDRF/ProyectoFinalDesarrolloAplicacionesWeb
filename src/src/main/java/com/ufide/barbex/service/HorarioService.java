package com.ufide.barbex.service;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioService {

    @Autowired
    private HorarioGeneralRepository horarioGeneralRepository;

    @Autowired
    private ExcepcionHorarioRepository excepcionHorarioRepository;

    @Autowired
    private CitaRepository citaRepository;

    public List<HorarioGeneral> listarPorBarbero(Long barberoId) {
        List<HorarioGeneral> horarios = horarioGeneralRepository.findByBarberoId(barberoId);
        if (horarios.size() < 7) {
            crearHorarioPorDefecto(barberoId);
            horarios = horarioGeneralRepository.findByBarberoId(barberoId);
        }
        return horarios;
    }

    public List<ExcepcionHorario> listarExcepcionesPorBarbero(Long barberoId) {
        return excepcionHorarioRepository.findByBarberoId(barberoId);
    }

    public List<ExcepcionHorario> listarExcepcionesFuturasPorBarbero(Long barberoId) {
        return excepcionHorarioRepository.findByBarberoId(barberoId).stream()
                .filter(e -> !e.getFecha().isBefore(LocalDate.now()))
                .toList();
    }

    @Transactional
    public void crearHorarioPorDefecto(Long barberoId) {
        Usuario barbero = new Usuario();
        barbero.setId(barberoId);

        for (DayOfWeek dia : DayOfWeek.values()) {
            Optional<HorarioGeneral> existente = horarioGeneralRepository.findByBarberoIdAndDiaSemana(barberoId, dia);
            if (existente.isEmpty()) {
                HorarioGeneral h = new HorarioGeneral();
                h.setDiaSemana(dia);
                h.setLibre(true);
                h.setActivo(true);
                h.setBarbero(barbero);
                horarioGeneralRepository.save(h);
            }
        }
    }

    @Transactional
    public void guardarHorariosSemanales(Long barberoId, List<HorarioGeneral> horarios) {
        for (HorarioGeneral h : horarios) {
            Optional<HorarioGeneral> existenteOpt = horarioGeneralRepository.findByBarberoIdAndDiaSemana(barberoId, h.getDiaSemana());
            if (existenteOpt.isPresent()) {
                HorarioGeneral existente = existenteOpt.get();
                existente.setLibre(h.isLibre());
                if (!h.isLibre()) {
                    existente.setHoraInicio(h.getHoraInicio());
                    existente.setHoraFin(h.getHoraFin());
                } else {
                    existente.setHoraInicio(null);
                    existente.setHoraFin(null);
                }
                horarioGeneralRepository.save(existente);
            } else {
                Usuario barbero = new Usuario();
                barbero.setId(barberoId);
                h.setBarbero(barbero);
                h.setActivo(true);
                if (h.isLibre()) {
                    h.setHoraInicio(null);
                    h.setHoraFin(null);
                }
                horarioGeneralRepository.save(h);
            }
        }
    }

    @Transactional
    public ExcepcionHorario guardarExcepcion(ExcepcionHorario excepcion) {
        if (excepcion.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden crear excepciones en fechas pasadas");
        }
        return excepcionHorarioRepository.save(excepcion);
    }

    @Transactional
    public void eliminarHorarioGeneral(Long id) {
        horarioGeneralRepository.deleteById(id);
    }

    @Transactional
    public void eliminarExcepcion(Long id) {
        excepcionHorarioRepository.deleteById(id);
    }

    public boolean estaDentroHorarioLaboral(Long barberoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        Optional<ExcepcionHorario> excepcionOpt = excepcionHorarioRepository.findByBarberoIdAndFecha(barberoId, fecha);

        if (excepcionOpt.isPresent()) {
            ExcepcionHorario excepcion = excepcionOpt.get();
            if (excepcion.getTipo() == TipoExcepcion.NO_TRABAJA) {
                return false;
            }
            return !horaInicio.isBefore(excepcion.getHoraInicio()) && !horaFin.isAfter(excepcion.getHoraFin());
        }

        Optional<HorarioGeneral> horarioOpt = horarioGeneralRepository.findByBarberoIdAndDiaSemana(barberoId, fecha.getDayOfWeek());
        if (horarioOpt.isEmpty() || horarioOpt.get().isLibre()) {
            return false;
        }

        HorarioGeneral horario = horarioOpt.get();
        return !horaInicio.isBefore(horario.getHoraInicio()) && !horaFin.isAfter(horario.getHoraFin());
    }

    public List<LocalTime> calcularSlotsDisponibles(Long barberoId, LocalDate fecha, int duracionMinutos) {
        List<LocalTime> slots = new ArrayList<>();

        LocalTime inicioJornada;
        LocalTime finJornada;

        Optional<ExcepcionHorario> excepcionOpt = excepcionHorarioRepository.findByBarberoIdAndFecha(barberoId, fecha);
        if (excepcionOpt.isPresent()) {
            ExcepcionHorario excepcion = excepcionOpt.get();
            if (excepcion.getTipo() == TipoExcepcion.NO_TRABAJA) {
                return slots;
            }
            inicioJornada = excepcion.getHoraInicio();
            finJornada = excepcion.getHoraFin();
        } else {
            Optional<HorarioGeneral> horarioOpt = horarioGeneralRepository.findByBarberoIdAndDiaSemana(barberoId, fecha.getDayOfWeek());
            if (horarioOpt.isEmpty() || horarioOpt.get().isLibre()) {
                return slots;
            }
            inicioJornada = horarioOpt.get().getHoraInicio();
            finJornada = horarioOpt.get().getHoraFin();
        }

        List<Cita> citas = citaRepository.findByBarberoIdAndFecha(barberoId, fecha).stream()
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA && c.getEstado() != EstadoCita.RECHAZADA)
                .sorted(Comparator.comparing(Cita::getHoraInicio))
                .toList();

        LocalTime horaActual = inicioJornada;
        int indexCita = 0;

        while (!horaActual.plusMinutes(duracionMinutos).isAfter(finJornada)) {
            LocalTime horaFinSlot = horaActual.plusMinutes(duracionMinutos);

            boolean hayChoque = false;
            while (indexCita < citas.size()) {
                Cita cita = citas.get(indexCita);
                if (cita.getHoraFin().isBefore(horaActual) || cita.getHoraFin().equals(horaActual)) {
                    indexCita++;
                } else if (cita.getHoraInicio().isBefore(horaFinSlot) && cita.getHoraFin().isAfter(horaActual)) {
                    hayChoque = true;
                    horaActual = cita.getHoraFin();
                    break;
                } else {
                    break;
                }
            }

            if (!hayChoque) {
                slots.add(horaActual);
                horaActual = horaActual.plusMinutes(30); // Saltos de 30 min
            }
        }

        return slots;
    }

    public List<Cita> obtenerCitasAfectadasPorCambioHorario(Long barberoId, LocalDate fecha, LocalTime nuevoInicio, LocalTime nuevoFin) {
        return citaRepository.findByBarberoIdAndFecha(barberoId, fecha).stream()
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA && c.getEstado() != EstadoCita.RECHAZADA)
                .filter(c -> c.getHoraInicio().isBefore(nuevoFin) && c.getHoraFin().isAfter(nuevoInicio))
                .toList();
    }

    public List<Cita> obtenerCitasAfectadasPorDiaNoTrabajo(Long barberoId, LocalDate fecha) {
        return citaRepository.findByBarberoIdAndFecha(barberoId, fecha).stream()
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA && c.getEstado() != EstadoCita.RECHAZADA)
                .toList();
    }

    public List<Cita> obtenerCitasAfectadasPorExcepcion(Long barberoId, ExcepcionHorario excepcion) {
        LocalDate fecha = excepcion.getFecha();
        List<Cita> citas = citaRepository.findByBarberoIdAndFecha(barberoId, fecha).stream()
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA && c.getEstado() != EstadoCita.RECHAZADA)
                .toList();

        if (excepcion.getTipo() == TipoExcepcion.NO_TRABAJA) {
            return citas;
        }

        return citas.stream()
                .filter(c -> c.getHoraInicio().isBefore(excepcion.getHoraInicio())
                        || c.getHoraFin().isAfter(excepcion.getHoraFin()))
                .toList();
    }
}
