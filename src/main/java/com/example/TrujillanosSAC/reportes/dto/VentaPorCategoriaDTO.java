package com.example.TrujillanosSAC.reportes.dto;

import java.math.BigDecimal;

/** Un resumen de unidades e importe vendido por categoría. */
public record VentaPorCategoriaDTO(
        Long categoriaId,
        String categoriaNombre,
        Long unidadesVendidas,
        BigDecimal montoTotal
) {
}
