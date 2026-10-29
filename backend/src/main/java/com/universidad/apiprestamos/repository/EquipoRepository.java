package com.universidad.apiprestamos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.universidad.apiprestamos.model.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findAllByOrderByIdAsc();

    boolean existsByCodigoPatrimonial(String codigoPatrimonial);

    boolean existsByCodigoPatrimonialAndIdNot(String codigoPatrimonial, Long id);
}
