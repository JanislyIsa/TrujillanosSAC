package com.example.TrujillanosSAC.inventario.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SolicitudLineaRequest(
        @NotNull @Positive Long idInsumo,
        @NotNull @Positive @DecimalMax("2147483647") BigDecimal cantidad
) {
}
