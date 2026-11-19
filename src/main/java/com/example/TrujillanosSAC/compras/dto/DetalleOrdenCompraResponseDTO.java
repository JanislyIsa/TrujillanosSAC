package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;

public record DetalleOrdenCompraResponseDTO(
        Long id,
        Long insumoId,
        String insumoNombre,
        String unidadMedida,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
