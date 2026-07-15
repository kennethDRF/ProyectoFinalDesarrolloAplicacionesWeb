package com.ufide.barbex.repository;

import com.ufide.barbex.entity.BarberoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BarberoServicioRepository extends JpaRepository<BarberoServicio, Long> {

    List<BarberoServicio> findByBarberoId(Long barberoId);

    List<BarberoServicio> findByServicioId(Long servicioId);

    boolean existsByBarberoIdAndServicioId(Long barberoId, Long servicioId);

    void deleteByBarberoIdAndServicioId(Long barberoId, Long servicioId);
}
