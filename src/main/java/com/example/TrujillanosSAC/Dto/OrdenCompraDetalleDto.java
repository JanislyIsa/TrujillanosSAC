package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;

public record OrdenCompraDetalleDto(
        String insumo,
        BigDecimal cantidad,
        String unidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
