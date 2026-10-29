package com.universidad.apiprestamos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.apiprestamos.dto.EquipoRequest;
import com.universidad.apiprestamos.dto.EquipoResponse;
import com.universidad.apiprestamos.service.EquipoService;

import jakarta.validation.Valid;

// Los permisos por rol se definen en SecurityConfig:
// GET -> USER o ADMIN; POST/PUT/DELETE -> solo ADMIN.
@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public List<EquipoResponse> listar() {
        return equipoService.listar();
    }

    @GetMapping("/{id}")
    public EquipoResponse obtener(@PathVariable Long id) {
        return equipoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@Valid @RequestBody EquipoRequest datos) {
        EquipoResponse creado = equipoService.crear(datos);
        return ResponseEntity.created(URI.create("/api/equipos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public EquipoResponse actualizar(@PathVariable Long id, @Valid @RequestBody EquipoRequest datos) {
        return equipoService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        equipoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
