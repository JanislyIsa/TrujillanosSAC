package com.example.TrujillanosSAC.inventario.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Resumen de las unidades de productos e insumos registradas en cada movimiento. */
public record ReporteMovimientoInventarioDto(
        Long idMovimiento,
        String tipoMovimiento,
        LocalDate fecha,
        LocalDateTime hora,
        String almacen,
        Long unidadesProductos,
        Long unidadesInsumos,
        Long totalUnidades
) {
}
