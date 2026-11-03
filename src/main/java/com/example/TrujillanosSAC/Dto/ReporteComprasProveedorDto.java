package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;

public record ReporteComprasProveedorDto(
        String proveedor,
        Long totalOrdenes,
        BigDecimal montoTotal,
        BigDecimal precioMinimo,
        BigDecimal precioMaximo
) {
}
