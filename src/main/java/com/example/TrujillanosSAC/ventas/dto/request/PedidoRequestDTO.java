package com.example.TrujillanosSAC.ventas.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/** El cliente del pedido es siempre el usuario que inició sesión. */
public record PedidoRequestDTO(
        @NotEmpty
        @Size(max = 50)
        List<@NotNull @Valid DetallePedidoRequestDTO> detalles
) {
}
