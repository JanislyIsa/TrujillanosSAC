package com.example.TrujillanosSAC.ventas.service.pasarela;

import com.example.TrujillanosSAC.ventas.model.codigo.MetodoPagoCodigo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

/**
 * Cobro real con la API de cargos de Culqi (POST /v2/charges).
 * <p>
 * El navegador (Culqi Checkout) convierte la tarjeta o el pago Yape en un token (llave pública pk_...). Aquí solo
 * se envía ese token junto con la llave secreta (sk_...), así los datos de pago nunca pasan
 * por nuestro servidor. La llave secreta se lee de la variable de entorno CULQI_LLAVE_SECRETA.
 * <p>
 * Pendiente: los cargos que Culqi manda a autenticación 3DS se tratan como no aprobados.
 */
@Component
@ConditionalOnProperty(name = "ventas.pago.pasarela", havingValue = "culqi")
public class CulqiPasarela implements PasarelaPago {

    private static final Logger log = LoggerFactory.getLogger(CulqiPasarela.class);
    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {
    };

    private final RestClient http;

    public CulqiPasarela(@Value("${ventas.culqi.url:https://api.culqi.com/v2}") String url,
                         @Value("${ventas.culqi.llave-secreta:}") String llaveSecreta,
                         @Value("${ventas.culqi.timeout-segundos:30}") long timeoutSegundos) {
        if (llaveSecreta.isBlank()) {
            throw new IllegalStateException("Configure la variable CULQI_LLAVE_SECRETA para usar Culqi");
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(timeoutSegundos));
        this.http = RestClient.builder()
                .baseUrl(url)
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + llaveSecreta)
                .build();
    }

    @Override
    public ResultadoCobro cobrar(SolicitudCobro solicitud) {
        Map<String, Object> cuerpo = Map.of(
                "amount", solicitud.montoCentimos(),
                "currency_code", solicitud.moneda(),
                "email", solicitud.email(),
                "source_id", solicitud.token(),
                "description", solicitud.descripcion());
        try {
            Map<String, Object> respuesta = http.post().uri("/charges")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(MAPA);
            if (respuesta != null && "charge".equals(respuesta.get("object")) && respuesta.get("id") != null) {
                return ResultadoCobro.aprobado(String.valueOf(respuesta.get("id")), mensajeDe(respuesta.get("outcome")),
                        metodoDe(respuesta.get("source")));
            }
            // Respuesta 2xx que no es un cargo: Culqi pide un paso extra (3DS) que aún no se maneja.
            return ResultadoCobro.rechazado("El pago requiere una autenticación adicional que aún no está disponible");
        } catch (HttpClientErrorException e) {
            // 4xx: Culqi rechazó el cargo (tarjeta sin fondos, token vencido, datos inválidos...).
            return ResultadoCobro.rechazado(mensajeDe(leerError(e)));
        } catch (RestClientException e) {
            // 5xx, tiempo de espera o red caída: no hay respuesta clara.
            log.error("No se pudo completar el cobro con Culqi", e);
            return ResultadoCobro.error("No se pudo confirmar el pago con Culqi");
        }
    }

    private static Map<?, ?> leerError(HttpClientErrorException e) {
        try {
            return e.getResponseBodyAs(Map.class);
        } catch (RuntimeException ignorado) {
            return null;
        }
    }

    /**
     * El cargo trae en "source" el token con el que se pagó. Culqi marca los tokens de Yape con el
     * prefijo "ype_" en el id (su "type" llega como "card", comprobado en el sandbox); cualquier otro
     * token se registra como TARJETA.
     */
    static MetodoPagoCodigo metodoDe(Object source) {
        if (source instanceof Map<?, ?> token) {
            String id = String.valueOf(token.get("id")).toLowerCase(Locale.ROOT);
            String tipo = String.valueOf(token.get("type")).toLowerCase(Locale.ROOT);
            if (id.startsWith("ype_") || tipo.contains("yape")) {
                return MetodoPagoCodigo.YAPE;
            }
        }
        return MetodoPagoCodigo.TARJETA;
    }

    /** Culqi devuelve el texto para el cliente en "user_message" (en un error o dentro de "outcome"). */
    private static String mensajeDe(Object datos) {
        if (datos instanceof Map<?, ?> mapa && mapa.get("user_message") != null) {
            return String.valueOf(mapa.get("user_message"));
        }
        return "Respuesta de Culqi sin mensaje";
    }
}
