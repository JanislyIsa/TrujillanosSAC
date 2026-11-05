package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoCompraResponseDTO(
        Long id,
        BigDecimal monto,
        LocalDate fechaPago,
        Long comprobanteId,
        Long metodoPagoId,
        String metodoPago
) {}
