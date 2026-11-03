package com.example.TrujillanosSAC.Dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record CarritoCompraItemDto(
        Long idInsumo,
        String nombre,
        String unidad,
        BigDecimal cantidad,
        Integer stockActual,
        Integer stockMinimo
) implements Serializable {
}
