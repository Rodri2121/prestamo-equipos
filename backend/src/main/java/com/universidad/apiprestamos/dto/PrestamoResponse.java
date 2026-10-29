package com.universidad.apiprestamos.dto;

import java.time.LocalDate;

import com.universidad.apiprestamos.model.EstadoPrestamo;
import com.universidad.apiprestamos.model.Prestamo;

public record PrestamoResponse(
        Long id,
        EquipoResponse equipo,
        String nombreSolicitante,
        LocalDate fechaPrestamo,
        LocalDate fechaPrevistaDevolucion,
        LocalDate fechaDevolucion,
        EstadoPrestamo estado) {

    public static PrestamoResponse de(Prestamo prestamo) {
        return new PrestamoResponse(prestamo.getId(), EquipoResponse.de(prestamo.getEquipo()),
                prestamo.getNombreSolicitante(), prestamo.getFechaPrestamo(),
                prestamo.getFechaPrevistaDevolucion(), prestamo.getFechaDevolucion(), prestamo.getEstado());
    }
}
