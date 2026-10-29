package com.universidad.apiprestamos.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.universidad.apiprestamos.dto.LoginResponse;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expiracionMinutos;

    public JwtService(JwtEncoder jwtEncoder, @Value("${app.jwt.expiracion-minutos}") long expiracionMinutos) {
        this.jwtEncoder = jwtEncoder;
        this.expiracionMinutos = expiracionMinutos;
    }

    public LoginResponse generarToken(Authentication autenticacion) {
        Instant ahora = Instant.now();
        // Authorities llegan como "ROLE_ADMIN"; en el token se guarda solo "ADMIN".
        List<String> roles = autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replaceFirst("^ROLE_", ""))
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("prestamos-api")
                .subject(autenticacion.getName())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES))
                .claim("roles", roles)
                .build();

        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
        return new LoginResponse(token, "Bearer", expiracionMinutos * 60, autenticacion.getName(), roles.get(0));
    }
}
