package com.example.TrujillanosSAC.inventario.dto.response;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        AlertaResponse alerta,
        LocalDateTime creadaEn,
        LocalDateTime leidaEn
) {
}
