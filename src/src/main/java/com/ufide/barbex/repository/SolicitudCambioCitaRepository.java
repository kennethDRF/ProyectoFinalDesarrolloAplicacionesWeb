package com.ufide.barbex.repository;

import com.ufide.barbex.entity.EstadoSolicitud;
import com.ufide.barbex.entity.SolicitudCambioCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudCambioCitaRepository extends JpaRepository<SolicitudCambioCita, Long> {
    List<SolicitudCambioCita> findByCitaBarberoIdAndEstadoSolicitud(Long barberoId, EstadoSolicitud estadoSolicitud);
    List<SolicitudCambioCita> findByCitaClienteId(Long clienteId);
    void deleteByCitaId(Long citaId);
}
