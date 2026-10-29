package com.universidad.apiprestamos.exception;

// Se traduce a 409 Conflict: la petición es válida pero choca con una regla de negocio
// (equipo ya prestado, código duplicado, eliminar con préstamo activo...).
public class ConflictoNegocioException extends RuntimeException {

    public ConflictoNegocioException(String mensaje) {
        super(mensaje);
    }
}
