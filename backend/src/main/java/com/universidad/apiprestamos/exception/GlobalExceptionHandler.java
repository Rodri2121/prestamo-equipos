package com.universidad.apiprestamos.exception;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validacion(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .toList();
        return Map.of("mensaje", "Datos no válidos.", "errores", errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> cuerpoInvalido() {
        return Map.of("mensaje",
                "El cuerpo no es un JSON válido o tiene valores incorrectos (fechas AAAA-MM-DD, tipo PORTATIL/PROYECTOR/TABLETA/OTRO).",
                "errores", List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return Map.of("mensaje", "Valor no válido para '" + ex.getName() + "'.", "errores", List.of());
    }

    @ExceptionHandler(DatosInvalidosException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> datosInvalidos(DatosInvalidosException ex) {
        return Map.of("mensaje", "Datos no válidos.", "errores", List.of(ex.getMessage()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> noEncontrado(RecursoNoEncontradoException ex) {
        return Map.of("mensaje", ex.getMessage());
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> conflicto(ConflictoNegocioException ex) {
        return Map.of("mensaje", ex.getMessage());
    }

    // Red de seguridad: si dos peticiones simultáneas esquivan la validación del servicio,
    // las restricciones de la BD (UNIQUE, índice de préstamo activo) responden 409.
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> integridad() {
        return Map.of("mensaje", "La operación viola una restricción de la base de datos.");
    }

    // Login con credenciales incorrectas.
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> credenciales() {
        return Map.of("mensaje", "Usuario o contraseña incorrectos.");
    }
}
