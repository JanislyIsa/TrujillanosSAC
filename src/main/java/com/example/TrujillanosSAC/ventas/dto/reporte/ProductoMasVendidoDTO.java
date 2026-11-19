package com.example.TrujillanosSAC.ventas.dto.reporte;

import java.math.BigDecimal;

/** Una fila del reporte "productos más vendidos". Se construye con "select new" en la consulta JPQL. */
public record ProductoMasVendidoDTO(
        Long productoId,
        String productoNombre,
        Long unidadesVendidas,
        BigDecimal montoTotal
) {
}
