package com.example.TrujillanosSAC.inventario.dto.response;

public record FaltanteResponse(
        Long idInsumo,
        String nombre,
        int stock,
        int stockMinimo,
        int stockLimite,
        int cantidadSugerida,
        String unidad
) {
}
