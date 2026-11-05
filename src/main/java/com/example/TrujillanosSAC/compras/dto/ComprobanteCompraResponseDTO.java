package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprobanteCompraResponseDTO(
        Long id,
        LocalDate fechaEmision,
        BigDecimal monto,
        String estado,
        Long ordenCompraId
) {}
