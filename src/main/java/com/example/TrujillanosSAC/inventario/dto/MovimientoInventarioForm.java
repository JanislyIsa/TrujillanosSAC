package com.example.TrujillanosSAC.inventario.dto;

public record MovimientoInventarioForm(
        String tipoMovimiento,
        String tipoItem,
        Long idItem,
        Integer cantidad
) {
}
