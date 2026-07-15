package com.ufide.barbex.service;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CitaServicioRepository citaServicioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BarberiaRepository barberiaRepository;

    @Autowired
    private BarberoServicioRepository barberoServicioRepository;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private SolicitudCambioCitaRepository solicitudRepository;

    public List<Cita> listarPorBarbero(Long barberoId) {
        return citaRepository.findByBarberoId(barberoId);
    }

    public List<Cita> listarPorCliente(Long clienteId) {
        return citaRepository.findByClienteId(clienteId);
    }

    public List<Cita> listarPorBarberia(Long barberiaId) {
        return citaRepository.findByBarberiaId(barberiaId);
    }

    public Optional<Cita> findById(Long id) {
        return citaRepository.findById(id);
    }

    @Transactional
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita crearCita(Long barberoId, Long clienteId, Long barberiaId, LocalDate fecha, LocalTime horaInicio,
                          List<Long> servicioIds, boolean adelantoPagado, String comprobanteAdelanto) {

        Usuario barbero = usuarioRepository.findById(barberoId)
                .orElseThrow(() -> new RuntimeException("Barbero no encontrado"));
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Barberia barberia = barberiaRepository.findById(barberiaId)
                .orElseThrow(() -> new RuntimeException("Barberia no encontrada"));

        if (!cliente.getBarberia().getId().equals(barberia.getId()) ||
            !barbero.getBarberia().getId().equals(barberia.getId())) {
            throw new RuntimeException("Cliente y barbero deben pertenecer a la misma barberia");
        }

        if (barbero.getRol() != Rol.BARBERO) {
            throw new RuntimeException("El usuario seleccionado no es un barbero");
        }

        if (cliente.getRol() != Rol.CLIENTE) {
            throw new RuntimeException("El usuario seleccionado no es un cliente");
        }

        if (servicioIds == null || servicioIds.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un servicio");
        }

        List<Servicio> servicios = servicioRepository.findAllById(servicioIds);
        if (servicios.isEmpty()) {
            throw new RuntimeException("No se encontraron servicios");
        }

        for (Servicio s : servicios) {
            if (!barberoServicioRepository.existsByBarberoIdAndServicioId(barberoId, s.getId())) {
                throw new RuntimeException("El barbero no ofrece el servicio: " + s.getNombre());
            }
        }

        int duracionTotal = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;
        BigDecimal montoAdelanto = BigDecimal.ZERO;
        for (Servicio s : servicios) {
            duracionTotal += s.getDuracionMinutos();
            montoTotal = montoTotal.add(s.getPrecio());
            montoAdelanto = montoAdelanto.add(
                    s.getMontoAdelanto() != null ? s.getMontoAdelanto() : BigDecimal.ZERO);
        }

        LocalTime horaFin = horaInicio.plusMinutes(duracionTotal);

        validarCita(barberoId, fecha, horaInicio, horaFin, null);

        Cita cita = new Cita();
        cita.setBarbero(barbero);
        cita.setCliente(cliente);
        cita.setBarberia(barberia);
        cita.setFecha(fecha);
        cita.setHoraInicio(horaInicio);
        cita.setHoraFin(horaFin);
        cita.setMontoTotal(montoTotal);
        cita.setMontoAdelanto(montoAdelanto);
        cita.setAdelantoPagado(adelantoPagado);
        cita.setComprobanteAdelanto(comprobanteAdelanto);
        cita.setEstado(determinarEstadoInicial(montoAdelanto, adelantoPagado));
        cita.setMotivoCancelacion(null);
        cita.setFechaCreacion(LocalDateTime.now());

        cita = citaRepository.save(cita);

        for (Servicio servicio : servicios) {
            CitaServicio cs = new CitaServicio(cita, servicio);
            citaServicioRepository.save(cs);
        }

        if (cita.getEstado() == EstadoCita.RECHAZADA) {
            cita.setMotivoCancelacion("Falta de adelanto o comprobante incorrecto");
            citaRepository.save(cita);
        }

        return cita;
    }

    private EstadoCita determinarEstadoInicial(BigDecimal montoAdelanto, boolean adelantoPagado) {
        if (montoAdelanto.compareTo(BigDecimal.ZERO) > 0) {
            return adelantoPagado ? EstadoCita.PENDIENTE : EstadoCita.RECHAZADA;
        }
        return EstadoCita.CONFIRMADA;
    }

    public void validarCita(Long barberoId, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, Long citaIdExcluir) {
        if (fecha.isBefore(LocalDate.now())) {
            throw new RuntimeException("No se pueden agendar citas en fechas pasadas");
        }

        if (!horaFin.isAfter(horaInicio)) {
            throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio");
        }

        if (!horarioService.estaDentroHorarioLaboral(barberoId, fecha, horaInicio, horaFin)) {
            throw new RuntimeException("La cita esta fuera del horario laboral del barbero");
        }

        List<Cita> solapadas = citaRepository
                .findByBarberoIdAndFechaAndHoraInicioBeforeAndHoraFinAfterAndEstadoNotAndEstadoNot(
                        barberoId, fecha, horaFin, horaInicio, EstadoCita.CANCELADA, EstadoCita.RECHAZADA);

        if (citaIdExcluir != null) {
            solapadas = solapadas.stream()
                    .filter(c -> !c.getId().equals(citaIdExcluir))
                    .toList();
        }
        if (!solapadas.isEmpty()) {
            throw new RuntimeException("Ya existe una cita en ese horario");
        }
    }

    @Transactional
    public void aprobarAdelanto(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new RuntimeException("La cita no esta pendiente de aprobacion");
        }

        if (!cita.isAdelantoPagado()) {
            throw new RuntimeException("El cliente no ha marcado el adelanto como pagado");
        }

        cita.setEstado(EstadoCita.CONFIRMADA);
        citaRepository.save(cita);
    }

    @Transactional
    public void rechazarCita(Long citaId, String motivo) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(EstadoCita.RECHAZADA);
        cita.setMotivoCancelacion(motivo);
        citaRepository.save(cita);
        solicitudRepository.deleteByCitaId(citaId);
    }

    @Transactional
    public void cancelarDirectamente(Long citaId, String motivo) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(EstadoCita.CANCELADA);
        cita.setMotivoCancelacion(motivo);
        citaRepository.save(cita);
        solicitudRepository.deleteByCitaId(citaId);
    }

    @Transactional
    public void marcarCompletada(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(EstadoCita.COMPLETADA);
        citaRepository.save(cita);
        solicitudRepository.deleteByCitaId(citaId);
    }

    @Transactional
    public Cita editarCita(Long citaId, LocalDate nuevaFecha, LocalTime nuevaHoraInicio) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        int duracionMinutos = (int) java.time.Duration.between(cita.getHoraInicio(), cita.getHoraFin()).toMinutes();
        LocalTime nuevaHoraFin = nuevaHoraInicio.plusMinutes(duracionMinutos);

        validarCita(cita.getBarbero().getId(), nuevaFecha, nuevaHoraInicio, nuevaHoraFin, cita.getId());

        cita.setFecha(nuevaFecha);
        cita.setHoraInicio(nuevaHoraInicio);
        cita.setHoraFin(nuevaHoraFin);

        solicitudRepository.deleteByCitaId(citaId);
        return citaRepository.save(cita);
    }

    @Transactional
    public Cita reagendarCita(Long citaOriginalId, LocalDate nuevaFecha, LocalTime nuevaHoraInicio, String motivo) {
        Cita original = citaRepository.findById(citaOriginalId)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        Cita nueva = new Cita();
        nueva.setBarbero(original.getBarbero());
        nueva.setCliente(original.getCliente());
        nueva.setBarberia(original.getBarberia());
        nueva.setFecha(nuevaFecha);
        int duracionMinutos = (int) java.time.Duration.between(original.getHoraInicio(), original.getHoraFin()).toMinutes();
        LocalTime nuevaHoraFin = nuevaHoraInicio.plusMinutes(duracionMinutos);

        nueva.setHoraInicio(nuevaHoraInicio);
        nueva.setHoraFin(nuevaHoraFin);
        nueva.setMontoTotal(original.getMontoTotal());
        nueva.setMontoAdelanto(original.getMontoAdelanto());
        nueva.setAdelantoPagado(original.isAdelantoPagado());
        nueva.setComprobanteAdelanto(original.getComprobanteAdelanto());
        nueva.setFechaCreacion(LocalDateTime.now());

        if (nueva.requiereAdelanto() && !nueva.isAdelantoPagado()) {
            nueva.setEstado(EstadoCita.PENDIENTE);
        } else {
            nueva.setEstado(EstadoCita.CONFIRMADA);
        }

        validarCita(nueva.getBarbero().getId(), nueva.getFecha(), nueva.getHoraInicio(), nueva.getHoraFin(), null);

        nueva = citaRepository.save(nueva);

        List<CitaServicio> serviciosOriginal = citaServicioRepository.findByCitaId(original.getId());
        for (CitaServicio cs : serviciosOriginal) {
            citaServicioRepository.save(new CitaServicio(nueva, cs.getServicio()));
        }

        original.setEstado(EstadoCita.CANCELADA);
        original.setMotivoCancelacion(motivo + " - Nueva cita ID: " + nueva.getId());
        citaRepository.save(original);
        solicitudRepository.deleteByCitaId(citaOriginalId);

        return nueva;
    }
}
