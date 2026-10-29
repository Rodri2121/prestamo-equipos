package com.universidad.apiprestamos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// fechaPrestamo es opcional: si no se envía se usa la fecha de hoy.
// Que la fecha prevista no sea anterior a la de préstamo se valida en PrestamoService.
public record PrestamoRequest(
        @NotNull(message = "El id del equipo es obligatorio.")
        Long equipoId,

        @NotBlank(message = "El nombre del solicitante es obligatorio.")
        @Size(max = 100, message = "El nombre del solicitante admite máximo 100 caracteres.")
        String nombreSolicitante,

        LocalDate fechaPrestamo,

        @NotNull(message = "La fecha prevista de devolución es obligatoria (formato AAAA-MM-DD).")
        LocalDate fechaPrevistaDevolucion) {
}
