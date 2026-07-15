package com.ufide.barbex.repository;

import com.ufide.barbex.entity.Barberia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberiaRepository extends JpaRepository<Barberia, Long> {
    Optional<Barberia> findByCodigoAcceso(String codigoAcceso);
}
