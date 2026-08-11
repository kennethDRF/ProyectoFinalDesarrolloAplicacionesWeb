package com.ufide.barbex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ServicioDto(
        Long id,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor a cero")
        BigDecimal precio,

        @NotNull(message = "La duracion es obligatoria")
        @Positive(message = "La duracion debe ser mayor a cero")
        Integer duracionMinutos,

        BigDecimal montoAdelanto,

        boolean activo,

        @NotNull(message = "La barberia es obligatoria")
        Long barberiaId,

        String barberiaNombre
) {
}
