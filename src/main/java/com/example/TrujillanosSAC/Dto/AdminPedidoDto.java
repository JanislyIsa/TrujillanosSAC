package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AdminPedidoDto(
        Long idPedido,
        String cliente,
        String telefono,
        LocalDate fechaEmision,
        LocalDateTime hora,
        BigDecimal total,
        Long idEstado,
        String estado,
        String productos
) {
}
