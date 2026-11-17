package com.example.TrujillanosSAC.ventas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** El cliente del pedido es siempre el usuario que inició sesión. */
public record PedidoRequestDTO(
        @NotEmpty(message = "El pedido debe contener al menos un producto")
        @Size(max = 50, message = "El pedido no puede tener más de 50 productos distintos")
        List<@NotNull(message = "El detalle no puede ser nulo") @Valid DetallePedidoRequestDTO> detalles
) {
}
