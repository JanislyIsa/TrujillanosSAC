package com.example.TrujillanosSAC.inventario.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MovimientoResponse(
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
