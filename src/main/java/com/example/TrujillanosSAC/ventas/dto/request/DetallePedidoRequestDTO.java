package com.example.TrujillanosSAC.ventas.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetallePedidoRequestDTO(
        @NotNull
        @Positive
        Long productoId,
        @NotNull
        @Positive
        @Max(1000)
        Integer cantidad
) {
}
