package com.example.TrujillanosSAC.inventario.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReporteMovimientoInventarioDto(
        String codigoMovimiento,
        String tipoMovimiento,
        LocalDate fecha,
        LocalDateTime hora,
        String almacen,
        Integer totalProductos,
        Integer totalInsumos,
        Integer totalItems
) {
}
