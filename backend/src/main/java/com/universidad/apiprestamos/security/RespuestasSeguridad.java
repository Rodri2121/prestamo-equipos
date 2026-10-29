package com.universidad.apiprestamos.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletResponse;

// Respuestas JSON para los errores que Spring Security corta antes de llegar al controlador.
//   401 Unauthorized: no hay token, o es inválido / expiró  -> "¿quién eres?"
//   403 Forbidden:    el token es válido pero el rol no alcanza -> "sé quién eres, pero no puedes"
final class RespuestasSeguridad {

    private RespuestasSeguridad() {
    }

    static AuthenticationEntryPoint noAutenticado() {
        return (request, response, ex) -> {
            response.setHeader("WWW-Authenticate", "Bearer");
            escribir(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "No autenticado: envía un token válido en el encabezado Authorization: Bearer <token>.");
        };
    }

    static AccessDeniedHandler accesoDenegado() {
        return (request, response, ex) -> escribir(response, HttpServletResponse.SC_FORBIDDEN,
                "Acceso denegado: tu rol no tiene permiso para esta operación.");
    }

    private static void escribir(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}
