package com.universidad.apiprestamos.exception;

// Se traduce a 404 Not Found en GlobalExceptionHandler.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
