package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;

/** Resumen de órdenes emitidas por proveedor, dentro de un rango opcional de fechas. */
public record OrdenesPorProveedorReporteDTO(
        Long idProveedor,
        String proveedor,
        Long cantidadOrdenes,
        BigDecimal montoTotal
) {
}
