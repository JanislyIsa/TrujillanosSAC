package com.example.TrujillanosSAC.inventario.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        int estado,
        String mensaje,
        String ruta,
        LocalDateTime fechaHora,
        Map<String, String> errores
) {
}
