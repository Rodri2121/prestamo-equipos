package com.universidad.apiprestamos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.apiprestamos.dto.PrestamoRequest;
import com.universidad.apiprestamos.dto.PrestamoResponse;
import com.universidad.apiprestamos.model.EstadoPrestamo;
import com.universidad.apiprestamos.service.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // GET /api/prestamos            -> todos
    // GET /api/prestamos?estado=ACTIVO -> filtrados
    @GetMapping
    public List<PrestamoResponse> listar(@RequestParam(required = false) EstadoPrestamo estado) {
        return prestamoService.listar(estado);
    }

    @GetMapping("/{id}")
    public PrestamoResponse obtener(@PathVariable Long id) {
        return prestamoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PrestamoResponse> registrar(@Valid @RequestBody PrestamoRequest datos) {
        PrestamoResponse creado = prestamoService.registrar(datos);
        return ResponseEntity.created(URI.create("/api/prestamos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}/devolver")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return prestamoService.devolver(id);
    }
}
