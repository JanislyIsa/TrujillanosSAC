package com.example.TrujillanosSAC.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRETO = Base64.getEncoder()
            .encodeToString("secreto-de-prueba-de-32-bytes-o-mas!!".getBytes());

    private static JwtService servicio(String secreto, String... perfiles) {
        MockEnvironment entorno = new MockEnvironment();
        entorno.setActiveProfiles(perfiles);
        return new JwtService(secreto, 60, entorno);
    }

    @Test
    void generaYLeeElUsuarioYLosRoles() {
        JwtService jwt = servicio(SECRETO);
        String token = jwt.generarToken("cliente", List.of("CLIENTE"));

        assertThat(jwt.extraerUsername(token)).isEqualTo("cliente");
        assertThat(jwt.leerClaims(token).get("roles", List.class)).containsExactly("CLIENTE");
        assertThat(jwt.getExpiracionSegundos()).isEqualTo(3600);
    }

    @Test
    void rechazaTokenVencidoOFirmadoConOtraClave() {
        JwtService jwt = servicio(SECRETO);
        String vencido = jwt.generarToken("cliente", List.of("CLIENTE"), Instant.now().minus(2, ChronoUnit.HOURS));
        assertThatThrownBy(() -> jwt.extraerUsername(vencido)).isInstanceOf(ExpiredJwtException.class);

        String deOtraClave = servicio("").generarToken("cliente", List.of("CLIENTE"));
        assertThatThrownBy(() -> jwt.extraerUsername(deOtraClave)).isInstanceOf(JwtException.class);
    }

    @Test
    void exigeUnSecretoValidoYEnProduccionQueExista() {
        String corto = Base64.getEncoder().encodeToString("muy-corto".getBytes());
        assertThatThrownBy(() -> servicio(corto)).isInstanceOf(IllegalStateException.class);
        assertThatCode(() -> servicio("")).doesNotThrowAnyException();
        assertThatThrownBy(() -> servicio("", "prod")).isInstanceOf(IllegalStateException.class);
    }
}
