package com.example.TrujillanosSAC.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PagoCompraRequestDTO(
        @NotNull @Positive Long comprobanteId,
        @NotNull @Positive BigDecimal monto,
        @NotNull LocalDate fechaPago,
        @NotNull @Positive Long metodoPagoId
) {}
