package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;

public record DetalleCotizacionResponseDTO(
        Long id,
        Long insumoId,
        String insumoNombre,
        String unidadMedida,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
