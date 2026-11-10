package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponseDTO(
        Long idPago,
        Long pedidoId,
        CatalogoResponseDTO metodoPago,
        CatalogoResponseDTO estado,
        LocalDateTime fechaPago,
        BigDecimal monto,
        String moneda,
        String referenciaExterna,
        String mensaje
) {
}
