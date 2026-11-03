package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoResumenDto(
        Long idPedido,
        LocalDate fechaEmision,
        String estado,
        BigDecimal total,
        String productos
) {
}
