package com.example.TrujillanosSAC.ventas.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DetallePedidoRequestDTO(
        @NotNull(message = "El producto es obligatorio")
        @Positive(message = "El producto debe ser válido")
        Long productoId,
        // Sin tope fijo: si piden más de lo que hay, la regla de stock responde 409 (no 400).
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        Integer cantidad
) {
}
