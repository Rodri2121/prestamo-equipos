package com.universidad.apiprestamos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.universidad.apiprestamos.dto.EquipoRequest;
import com.universidad.apiprestamos.dto.EquipoResponse;
import com.universidad.apiprestamos.exception.ConflictoNegocioException;
import com.universidad.apiprestamos.exception.RecursoNoEncontradoException;
import com.universidad.apiprestamos.model.Equipo;
import com.universidad.apiprestamos.model.EstadoPrestamo;
import com.universidad.apiprestamos.repository.EquipoRepository;
import com.universidad.apiprestamos.repository.PrestamoRepository;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final PrestamoRepository prestamoRepository;

    public EquipoService(EquipoRepository equipoRepository, PrestamoRepository prestamoRepository) {
        this.equipoRepository = equipoRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listar() {
        return equipoRepository.findAllByOrderByIdAsc().stream().map(EquipoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public EquipoResponse obtener(Long id) {
        return EquipoResponse.de(buscar(id));
    }

    @Transactional
    public EquipoResponse crear(EquipoRequest datos) {
        String codigo = datos.codigoPatrimonial().trim();
        if (equipoRepository.existsByCodigoPatrimonial(codigo)) {
            throw new ConflictoNegocioException("Ya existe un equipo con el código patrimonial " + codigo + ".");
        }
        Equipo equipo = new Equipo(codigo, datos.nombre().trim(), datos.tipo());
        return EquipoResponse.de(equipoRepository.save(equipo));
    }

    @Transactional
    public EquipoResponse actualizar(Long id, EquipoRequest datos) {
        Equipo equipo = buscar(id);
        String codigo = datos.codigoPatrimonial().trim();
        if (equipoRepository.existsByCodigoPatrimonialAndIdNot(codigo, id)) {
            throw new ConflictoNegocioException("Ya existe otro equipo con el código patrimonial " + codigo + ".");
        }
        equipo.setCodigoPatrimonial(codigo);
        equipo.setNombre(datos.nombre().trim());
        equipo.setTipo(datos.tipo());
        return EquipoResponse.de(equipo);
    }

    @Transactional
    public void eliminar(Long id) {
        Equipo equipo = buscar(id);
        // Regla: no se elimina un equipo con préstamos activos.
        if (prestamoRepository.existsByEquipoIdAndEstado(id, EstadoPrestamo.ACTIVO)) {
            throw new ConflictoNegocioException(
                    "No se puede eliminar el equipo " + equipo.getCodigoPatrimonial() + ": tiene un préstamo activo.");
        }
        equipoRepository.delete(equipo);
    }

    Equipo buscar(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el equipo con id " + id + "."));
    }
}
