package com.universidad.apiprestamos.dto;

import com.universidad.apiprestamos.model.TipoEquipo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// El estado no se recibe: lo controla el sistema (DISPONIBLE al crear,
// PRESTADO/DISPONIBLE al prestar/devolver).
public record EquipoRequest(
        @NotBlank(message = "El código patrimonial es obligatorio.")
        @Size(max = 30, message = "El código patrimonial admite máximo 30 caracteres.")
        String codigoPatrimonial,

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(max = 100, message = "El nombre admite máximo 100 caracteres.")
        String nombre,

        @NotNull(message = "El tipo es obligatorio (PORTATIL, PROYECTOR, TABLETA u OTRO).")
        TipoEquipo tipo) {
}
