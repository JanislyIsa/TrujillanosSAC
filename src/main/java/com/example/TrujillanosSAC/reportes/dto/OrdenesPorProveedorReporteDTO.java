package com.example.TrujillanosSAC.reportes.dto;

import java.math.BigDecimal;

/** Resumen de órdenes emitidas por proveedor en un rango de fechas. */
public record OrdenesPorProveedorReporteDTO(
        Long idProveedor,
        String proveedor,
        Long cantidadOrdenes,
        BigDecimal montoTotal
) {
}
