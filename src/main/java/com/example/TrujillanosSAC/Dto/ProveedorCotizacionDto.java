package com.example.TrujillanosSAC.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProveedorCotizacionDto(
        Long idCotizacion,
        String codigoCotizacion,
        String codigoOrden,
        LocalDate fechaCotizacion,
        LocalDate fechaEntrega,
        String estado,
        BigDecimal monto,
        Long idOrden,
        String qrPagoUrl
) {
}
