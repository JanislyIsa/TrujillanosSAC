package com.example.TrujillanosSAC.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ComprobanteCompraRequestDTO(
        @NotNull @Positive Long ordenCompraId,
        @NotNull LocalDate fechaEmision,
        @NotNull @PositiveOrZero BigDecimal monto
) {}
