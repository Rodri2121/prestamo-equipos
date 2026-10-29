package com.universidad.apiprestamos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_patrimonial", nullable = false, unique = true, length = 30)
    private String codigoPatrimonial;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEquipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoEquipo estado = EstadoEquipo.DISPONIBLE;

    // Lado inverso de la relación 1:N. Al eliminar el equipo se borra su
    // historial de préstamos (solo se permite si ninguno está ACTIVO).
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.REMOVE)
    private List<Prestamo> prestamos = new ArrayList<>();

    protected Equipo() {
    }

    public Equipo(String codigoPatrimonial, String nombre, TipoEquipo tipo) {
        this.codigoPatrimonial = codigoPatrimonial;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoPatrimonial() {
        return codigoPatrimonial;
    }

    public void setCodigoPatrimonial(String codigoPatrimonial) {
        this.codigoPatrimonial = codigoPatrimonial;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }
}
