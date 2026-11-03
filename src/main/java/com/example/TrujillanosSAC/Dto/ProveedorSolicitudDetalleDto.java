package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;

public record ProveedorSolicitudDetalleDto(
        Long idDetalle,
        String insumo,
        BigDecimal cantidad,
        String unidad
) {
}
