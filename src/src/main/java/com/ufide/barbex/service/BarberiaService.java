package com.ufide.barbex.service;

import com.ufide.barbex.entity.Barberia;
import com.ufide.barbex.repository.BarberiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BarberiaService {

    @Autowired
    private BarberiaRepository barberiaRepository;

    public Optional<Barberia> findById(Long id) {
        return barberiaRepository.findById(id);
    }

    public Optional<Barberia> findByCodigoAcceso(String codigoAcceso) {
        return barberiaRepository.findByCodigoAcceso(codigoAcceso);
    }

    public List<Barberia> listarTodas() {
        return barberiaRepository.findAll();
    }

    @Transactional
    public Barberia guardar(Barberia barberia) {
        if (barberia.getFechaRegistro() == null) {
            barberia.setFechaRegistro(LocalDateTime.now());
        }
        if (barberia.getCodigoAcceso() == null || barberia.getCodigoAcceso().isEmpty()) {
            barberia.setCodigoAcceso("BAR" + System.currentTimeMillis());
        }
        return barberiaRepository.save(barberia);
    }
}
