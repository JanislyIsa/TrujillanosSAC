package com.example.TrujillanosSAC.compras.dto;

import java.math.BigDecimal;

public record DetalleOrdenCompraResponseDTO(
        Long id,
        Long insumoId,
        String insumoNombre,
        String unidadMedida,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}
