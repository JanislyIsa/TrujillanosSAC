package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrdenCompraResponseDTO(
        Long id,
        LocalDate fechaEmision,
        String descripcion,
        Long cotizacionId,
        String proveedorNombre,
        BigDecimal total,
        List<DetalleOrdenCompraResponseDTO> detalles
) {}
