package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ComprobanteVentaResponseDTO(
        Long idComprobante,
        Long pedidoId,
        CatalogoResponseDTO tipo,
        CatalogoResponseDTO estado,
        String serie,
        String numero,
        LocalDateTime fechaEmision,
        BigDecimal total,
        String estadoSunat
) {
}
