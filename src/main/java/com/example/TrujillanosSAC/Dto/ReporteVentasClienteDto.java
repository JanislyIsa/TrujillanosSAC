package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteVentasClienteDto(
        String cliente,
        String telefono,
        Long totalPedidos,
        BigDecimal montoTotal,
        LocalDate ultimaCompra
) {
}
