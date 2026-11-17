package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CotizacionResponseDTO(
        Long id,
        LocalDate fechaCotizacion,
        LocalDate fechaEntrega,
        String estado,
        Long solicitudId,
        Long proveedorId,
        String proveedorNombre,
        BigDecimal monto,
        List<DetalleCotizacionResponseDTO> detalles,
        String descripcion
) {}
