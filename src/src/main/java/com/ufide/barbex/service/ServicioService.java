package com.ufide.barbex.service;

import com.ufide.barbex.entity.*;
import com.ufide.barbex.repository.BarberoServicioRepository;
import com.ufide.barbex.repository.CitaServicioRepository;
import com.ufide.barbex.repository.ServicioRepository;
import com.ufide.barbex.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BarberoServicioRepository barberoServicioRepository;

    @Autowired
    private CitaServicioRepository citaServicioRepository;

    public List<Servicio> listarPorBarberia(Long barberiaId) {
        return servicioRepository.findByBarberiaId(barberiaId);
    }

    public List<Servicio> listarActivosPorBarberia(Long barberiaId) {
        return servicioRepository.findByBarberiaIdAndActivoTrue(barberiaId);
    }

    public Optional<Servicio> findById(Long id) {
        return servicioRepository.findById(id);
    }

    @Transactional
    public Servicio guardar(Servicio servicio) {
        if (servicio.getMontoAdelanto() != null && servicio.getMontoAdelanto().compareTo(servicio.getPrecio()) > 0) {
            throw new RuntimeException("El monto del adelanto no puede ser mayor que el precio total");
        }
        boolean esNuevo = servicio.getId() == null;
        Servicio guardado = servicioRepository.save(servicio);

        // Si es nuevo servicio, asignarlo a todos los barberos de la barberia
        if (esNuevo) {
            List<Usuario> barberos = usuarioRepository.findByBarberiaId(guardado.getBarberia().getId()).stream()
                    .filter(u -> u.getRol() == Rol.BARBERO)
                    .toList();
            for (Usuario barbero : barberos) {
                if (!barberoServicioRepository.existsByBarberoIdAndServicioId(barbero.getId(), guardado.getId())) {
                    barberoServicioRepository.save(new BarberoServicio(barbero, guardado));
                }
            }
        }

        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        servicio.setActivo(false);
        servicioRepository.save(servicio);
    }

    @Transactional
    public void eliminarDefinitivo(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        List<CitaServicio> asociaciones = citaServicioRepository.findByServicioId(id);
        boolean tieneCitasActivas = asociaciones.stream()
                .anyMatch(cs -> {
                    EstadoCita estado = cs.getCita().getEstado();
                    return estado != EstadoCita.CANCELADA && estado != EstadoCita.RECHAZADA;
                });

        if (tieneCitasActivas) {
            throw new RuntimeException("No se puede eliminar el servicio porque tiene citas activas. Cancele las citas primero.");
        }

        // Eliminar relaciones con barberos
        List<BarberoServicio> barberoServicios = barberoServicioRepository.findByServicioId(id);
        barberoServicioRepository.deleteAll(barberoServicios);

        // Eliminar relaciones con citas (incluidas canceladas/rechazadas)
        citaServicioRepository.deleteAll(asociaciones);

        // Eliminar el servicio definitivamente
        servicioRepository.delete(servicio);
    }

    public List<Servicio> listarPorBarbero(Long barberoId) {
        return barberoServicioRepository.findByBarberoId(barberoId).stream()
                .map(BarberoServicio::getServicio)
                .filter(Servicio::isActivo)
                .toList();
    }
}
