package com.example.TrujillanosSAC.inventario.dto.response;

public record ComprobanteResponse(
        MovimientoResponse movimiento,
        String responsable,
        String motivo
) {
}
