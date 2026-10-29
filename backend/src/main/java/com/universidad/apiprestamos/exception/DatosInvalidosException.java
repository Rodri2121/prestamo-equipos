package com.universidad.apiprestamos.exception;

// Se traduce a 400 Bad Request: datos que no pasan una validación entre campos (p. ej. fechas).
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
