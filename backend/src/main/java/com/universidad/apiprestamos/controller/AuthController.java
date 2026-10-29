package com.universidad.apiprestamos.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.apiprestamos.dto.LoginRequest;
import com.universidad.apiprestamos.dto.LoginResponse;
import com.universidad.apiprestamos.security.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // 1) Spring Security compara la contraseña con el hash BCrypt de la BD.
    // 2) Si es incorrecta lanza BadCredentialsException -> 401 (GlobalExceptionHandler).
    // 3) Si es correcta se emite un JWT firmado con el usuario y su rol.
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest datos) {
        Authentication autenticacion = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(datos.username(), datos.password()));
        return jwtService.generarToken(autenticacion);
    }
}
