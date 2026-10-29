package com.universidad.apiprestamos.dto;

import com.universidad.apiprestamos.model.Equipo;
import com.universidad.apiprestamos.model.EstadoEquipo;
import com.universidad.apiprestamos.model.TipoEquipo;

public record EquipoResponse(
        Long id,
        String codigoPatrimonial,
        String nombre,
        TipoEquipo tipo,
        EstadoEquipo estado) {

    public static EquipoResponse de(Equipo equipo) {
        return new EquipoResponse(equipo.getId(), equipo.getCodigoPatrimonial(), equipo.getNombre(),
                equipo.getTipo(), equipo.getEstado());
    }
}
