package com.example.TrujillanosSAC.reportes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Resumen de unidades de productos e insumos registradas por movimiento. */
public record MovimientoInventarioReporteDTO(
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
