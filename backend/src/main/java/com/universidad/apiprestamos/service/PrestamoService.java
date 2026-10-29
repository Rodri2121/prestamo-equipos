package com.universidad.apiprestamos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universidad.apiprestamos.dto.PrestamoRequest;
import com.universidad.apiprestamos.dto.PrestamoResponse;
import com.universidad.apiprestamos.exception.ConflictoNegocioException;
import com.universidad.apiprestamos.exception.DatosInvalidosException;
import com.universidad.apiprestamos.exception.RecursoNoEncontradoException;
import com.universidad.apiprestamos.model.Equipo;
import com.universidad.apiprestamos.model.EstadoEquipo;
import com.universidad.apiprestamos.model.EstadoPrestamo;
import com.universidad.apiprestamos.model.Prestamo;
import com.universidad.apiprestamos.repository.PrestamoRepository;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EquipoService equipoService;

    public PrestamoService(PrestamoRepository prestamoRepository, EquipoService equipoService) {
        this.prestamoRepository = prestamoRepository;
        this.equipoService = equipoService;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar(EstadoPrestamo estado) {
        List<Prestamo> prestamos = estado == null
                ? prestamoRepository.findAllByOrderByIdDesc()
                : prestamoRepository.findByEstadoOrderByIdDesc(estado);
        return prestamos.stream().map(PrestamoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse obtener(Long id) {
        return PrestamoResponse.de(buscar(id));
    }

    // Toda la operación es una transacción: si algo falla, ni se crea el
    // préstamo ni cambia el estado del equipo.
    @Transactional
    public PrestamoResponse registrar(PrestamoRequest datos) {
        LocalDate fechaPrestamo = datos.fechaPrestamo() != null ? datos.fechaPrestamo() : LocalDate.now();
        if (datos.fechaPrevistaDevolucion().isBefore(fechaPrestamo)) {
            throw new DatosInvalidosException(
                    "La fecha prevista de devolución no puede ser anterior a la fecha del préstamo.");
        }

        Equipo equipo = equipoService.buscar(datos.equipoId());
        if (equipo.getEstado() == EstadoEquipo.PRESTADO) {
            throw new ConflictoNegocioException(
                    "El equipo " + equipo.getCodigoPatrimonial() + " ya está PRESTADO.");
        }

        Prestamo prestamo = new Prestamo(equipo, datos.nombreSolicitante().trim(), fechaPrestamo,
                datos.fechaPrevistaDevolucion());
        equipo.setEstado(EstadoEquipo.PRESTADO);
        return PrestamoResponse.de(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = buscar(id);
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ConflictoNegocioException("El préstamo " + id + " ya fue devuelto.");
        }
        prestamo.registrarDevolucion(LocalDate.now());
        prestamo.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);
        return PrestamoResponse.de(prestamo);
    }

    private Prestamo buscar(Long id) {
        return prestamoRepository.findWithEquipoById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el préstamo con id " + id + "."));
    }
}
