package com.example.TrujillanosSAC.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Escribe los 401/403 de los filtros con el mismo formato que ErrorResponseDTO
 * (timestamp, status, error, message, path, validationErrors), igual que los demás errores de la API.
 */
final class RespuestaErrorSeguridad {

    private RespuestaErrorSeguridad() {
    }

    static void escribir(HttpServletRequest request, HttpServletResponse response,
                         int status, String error, String mensaje) throws IOException {
        String json = "{"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + status + ","
                + "\"error\":\"" + escapar(error) + "\","
                + "\"message\":\"" + escapar(mensaje) + "\","
                + "\"path\":\"" + escapar(request.getRequestURI()) + "\","
                + "\"validationErrors\":null"
                + "}";
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json);
    }

    private static String escapar(String texto) {
        return texto == null ? "" : texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
