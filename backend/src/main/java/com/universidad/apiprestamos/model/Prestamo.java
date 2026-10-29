package com.universidad.apiprestamos.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lado dueño de la relación: la FK equipo_id vive en la tabla prestamos.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @Column(name = "nombre_solicitante", nullable = false, length = 100)
    private String nombreSolicitante;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_prevista_devolucion", nullable = false)
    private LocalDate fechaPrevistaDevolucion;

    @Column(name = "fecha_devolucion")
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    protected Prestamo() {
    }

    public Prestamo(Equipo equipo, String nombreSolicitante, LocalDate fechaPrestamo,
            LocalDate fechaPrevistaDevolucion) {
        this.equipo = equipo;
        this.nombreSolicitante = nombreSolicitante;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaPrevistaDevolucion = fechaPrevistaDevolucion;
    }

    public Long getId() {
        return id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaPrevistaDevolucion() {
        return fechaPrevistaDevolucion;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }

    public void registrarDevolucion(LocalDate fecha) {
        this.fechaDevolucion = fecha;
        this.estado = EstadoPrestamo.DEVUELTO;
    }
}
