package com.example.TrujillanosSAC.ventas.dto.request;

import com.example.TrujillanosSAC.ventas.model.codigo.TipoComprobanteCodigo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ComprobanteVentaRequestDTO(
        @NotNull
        @Positive
        Long pedidoId,
        @NotNull
        TipoComprobanteCodigo tipo) {
}
