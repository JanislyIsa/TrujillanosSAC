package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrdenCompraAdminDto(
        Long idOrden,
        String codigoOrden,
        LocalDate fechaEmision,
        String proveedor,
        String estado,
        BigDecimal total,
        String descripcion,
        String qrPagoUrl,
        boolean pagada,
        List<OrdenCompraDetalleDto> detalles
) {
}
