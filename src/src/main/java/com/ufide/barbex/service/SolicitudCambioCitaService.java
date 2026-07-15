package com.ufide.barbex.service;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.repository.CitaRepository;
import com.ufide.barbex.repository.SolicitudCambioCitaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class SolicitudCambioCitaService {

    @Autowired
    private SolicitudCambioCitaRepository solicitudRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CitaService citaService;

    public List<SolicitudCambioCita> listarPendientesPorBarbero(Long barberoId) {
        return solicitudRepository.findByCitaBarberoIdAndEstadoSolicitud(barberoId, EstadoSolicitud.PENDIENTE);
    }

    public List<SolicitudCambioCita> listarPorCliente(Long clienteId) {
        return solicitudRepository.findByCitaClienteId(clienteId);
    }

    public Optional<SolicitudCambioCita> findById(Long id) {
        return solicitudRepository.findById(id);
    }

    @Transactional
    public SolicitudCambioCita solicitarEdicion(Long citaId, LocalDate nuevaFecha, LocalTime nuevaHoraInicio) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.RECHAZADA) {
            throw new RuntimeException("No se puede editar una cita cancelada o rechazada");
        }

        solicitudRepository.deleteByCitaId(citaId);

        SolicitudCambioCita solicitud = new SolicitudCambioCita();
        solicitud.setCita(cita);
        solicitud.setTipo(TipoSolicitud.EDICION);
        solicitud.setNuevaFecha(nuevaFecha);
        solicitud.setNuevaHoraInicio(nuevaHoraInicio);
        solicitud.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public SolicitudCambioCita solicitarCancelacion(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() == EstadoCita.CANCELADA || cita.getEstado() == EstadoCita.RECHAZADA) {
            throw new RuntimeException("La cita ya esta cancelada o rechazada");
        }

        solicitudRepository.deleteByCitaId(citaId);

        SolicitudCambioCita solicitud = new SolicitudCambioCita();
        solicitud.setCita(cita);
        solicitud.setTipo(TipoSolicitud.CANCELACION);
        solicitud.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void aprobarSolicitud(Long solicitudId) {
        SolicitudCambioCita solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("La solicitud ya fue respondida");
        }

        Cita cita = solicitud.getCita();

        if (solicitud.getTipo() == TipoSolicitud.EDICION) {
            int duracionMinutos = (int) java.time.Duration.between(cita.getHoraInicio(), cita.getHoraFin()).toMinutes();
            LocalTime nuevaHoraFin = solicitud.getNuevaHoraInicio().plusMinutes(duracionMinutos);
            citaService.validarCita(cita.getBarbero().getId(), solicitud.getNuevaFecha(),
                    solicitud.getNuevaHoraInicio(), nuevaHoraFin, cita.getId());

            citaService.reagendarCita(cita.getId(), solicitud.getNuevaFecha(), solicitud.getNuevaHoraInicio(),
                    "Reagendado por solicitud de edicion aprobada");
        } else if (solicitud.getTipo() == TipoSolicitud.CANCELACION) {
            cita.setEstado(EstadoCita.CANCELADA);
            cita.setMotivoCancelacion("Cancelacion aprobada por el barbero");
            citaRepository.save(cita);
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.APROBADA);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitudRepository.save(solicitud);
    }

    @Transactional
    public void rechazarSolicitud(Long solicitudId, String motivo) {
        SolicitudCambioCita solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstadoSolicitud() != EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("La solicitud ya fue respondida");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitudRepository.save(solicitud);
    }
}
