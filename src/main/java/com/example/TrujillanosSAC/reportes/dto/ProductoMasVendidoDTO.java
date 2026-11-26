package com.example.TrujillanosSAC.reportes.dto;

import java.math.BigDecimal;

/** Un producto y las unidades e importe total vendidos en el período consultado. */
public record ProductoMasVendidoDTO(
        Long productoId,
        String productoNombre,
        Long unidadesVendidas,
        BigDecimal montoTotal
) {
}
