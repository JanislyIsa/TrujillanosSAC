package com.example.TrujillanosSAC.ventas.service.pasarela;

import com.example.TrujillanosSAC.ventas.model.codigo.MetodoPagoCodigo;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CulqiPasarelaTest {

    @Test
    void tokenYapeDelSandboxSeRegistraComoYape() {
        // Así llega un token Yape de Culqi: id con prefijo "ype_" y type "card"
        var source = Map.of("object", "token", "id", "ype_test_abc123", "type", "card");
        assertThat(CulqiPasarela.metodoDe(source)).isEqualTo(MetodoPagoCodigo.YAPE);
    }

    @Test
    void tokenDeTarjetaSeRegistraComoTarjeta() {
        var source = Map.of("object", "token", "id", "tkn_test_abc123", "type", "card");
        assertThat(CulqiPasarela.metodoDe(source)).isEqualTo(MetodoPagoCodigo.TARJETA);
    }

    @Test
    void sinSourceSeRegistraComoTarjeta() {
        assertThat(CulqiPasarela.metodoDe(null)).isEqualTo(MetodoPagoCodigo.TARJETA);
    }
}
