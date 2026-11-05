package com.example.TrujillanosSAC.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record DetalleCotizacionRequestDTO(
        @NotNull @Positive Long insumoId,
        @NotNull @Positive BigDecimal cantidad,
        @NotNull @PositiveOrZero BigDecimal precioUnitario
) {}
