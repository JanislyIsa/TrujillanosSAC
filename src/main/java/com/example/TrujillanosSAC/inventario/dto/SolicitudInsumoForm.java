package com.example.TrujillanosSAC.inventario.dto;

import java.math.BigDecimal;

public record SolicitudInsumoForm(
        Long idInsumo,
        BigDecimal cantidad
) {
}
