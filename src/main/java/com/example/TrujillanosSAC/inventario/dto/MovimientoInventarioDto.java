package com.example.TrujillanosSAC.inventario.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimientoInventarioDto(
        Long idMovimiento,
        String codigoMovimiento,
        String codigoComprobante,
        String tipoMovimiento,
        String tipoItem,
        String nombreItem,
        Integer cantidad,
        String unidad,
        Integer stockAnterior,
        Integer stockNuevo,
        LocalDate fecha,
        LocalDateTime hora,
        String almacen
) {
}
