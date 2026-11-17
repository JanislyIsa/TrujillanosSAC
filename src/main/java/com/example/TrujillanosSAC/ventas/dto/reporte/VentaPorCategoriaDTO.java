package com.example.TrujillanosSAC.ventas.dto.reporte;

import java.math.BigDecimal;

/**
 * Una fila del reporte "ventas por categoría": cuánto se vendió de cada categoría de golosinas.
 * Se construye en la consulta JPQL con "select new" (proyección), no a partir de entidades.
 */
public record VentaPorCategoriaDTO(
        Long categoriaId,
        String categoriaNombre,
        Long unidadesVendidas,
        BigDecimal montoTotal
) {
}
