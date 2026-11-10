package com.example.TrujillanosSAC.ventas.dto.request;

import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoPedidoRequestDTO(
        @NotNull EstadoPedidoCodigo estado
) {
}
