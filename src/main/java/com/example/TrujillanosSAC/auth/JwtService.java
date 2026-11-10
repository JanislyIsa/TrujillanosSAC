package com.example.TrujillanosSAC.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Emite y valida los JWT de la API (firma HS256). Basado en el JwtService de sysventas.
 * <p>
 * El secreto sale de la variable de entorno JWT_SECRET (Base64, mínimo 32 bytes); nunca va en el código ni en Git.
 * Si no está definido: en el perfil prod la aplicación no arranca; en dev/test se genera uno aleatorio
 * (los tokens dejan de servir al reiniciar), para que nadie quede bloqueado al ejecutar el proyecto.
 */
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey clave;
    private final long expiracionMinutos;

    public JwtService(@Value("${seguridad.jwt.secreto:}") String secretoBase64,
                      @Value("${seguridad.jwt.expiracion-minutos:60}") long expiracionMinutos,
                      Environment entorno) {
        if (expiracionMinutos <= 0) {
            throw new IllegalStateException("JWT_EXPIRACION_MINUTOS debe ser mayor que cero");
        }
        this.expiracionMinutos = expiracionMinutos;
        this.clave = crearClave(secretoBase64, entorno);
    }

    private static SecretKey crearClave(String secretoBase64, Environment entorno) {
        if (secretoBase64 == null || secretoBase64.isBlank()) {
            if (entorno.acceptsProfiles(Profiles.of("prod"))) {
                throw new IllegalStateException("Defina la variable JWT_SECRET (Base64, mínimo 32 bytes)");
            }
            log.warn("JWT_SECRET no está definido: se usa un secreto aleatorio. Los tokens dejarán de servir al reiniciar.");
            byte[] aleatorio = new byte[32];
            new SecureRandom().nextBytes(aleatorio);
            return Keys.hmacShaKeyFor(aleatorio);
        }
        try {
            // Keys.hmacShaKeyFor exige al menos 256 bits (32 bytes) para HS256.
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64.trim()));
        } catch (RuntimeException e) {
            throw new IllegalStateException("JWT_SECRET debe estar en Base64 y tener al menos 32 bytes", e);
        }
    }

    /** Token con: sub = username, roles (sin el prefijo ROLE_), iat = emitido, exp = vence. */
    public String generarToken(String username, List<String> roles) {
        return generarToken(username, roles, Instant.now());
    }

    /** Visible en el paquete para las pruebas (por ejemplo, para crear un token ya vencido). */
    String generarToken(String username, List<String> roles, Instant emitidoEn) {
        Instant vence = emitidoEn.plus(expiracionMinutos, ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Date.from(emitidoEn))
                .expiration(Date.from(vence))
                .signWith(clave)
                .compact();
    }

    /**
     * Valida firma y vencimiento y devuelve el username (claim "sub").
     * Lanza ExpiredJwtException si venció y JwtException si la firma o el formato no son válidos.
     */
    public String extraerUsername(String token) {
        return leerClaims(token).getSubject();
    }

    public Claims leerClaims(String token) {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }

    public long getExpiracionSegundos() {
        return expiracionMinutos * 60;
    }
}
