package com.universidad.apiprestamos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.universidad.apiprestamos.model.EstadoPrestamo;
import com.universidad.apiprestamos.model.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    boolean existsByEquipoIdAndEstado(Long equipoId, EstadoPrestamo estado);

    // @EntityGraph trae el equipo en la misma consulta (evita N+1 al armar la respuesta).
    @EntityGraph(attributePaths = "equipo")
    List<Prestamo> findAllByOrderByIdDesc();

    @EntityGraph(attributePaths = "equipo")
    List<Prestamo> findByEstadoOrderByIdDesc(EstadoPrestamo estado);

    @EntityGraph(attributePaths = "equipo")
    Optional<Prestamo> findWithEquipoById(Long id);
}
