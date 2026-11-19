package com.example.TrujillanosSAC.inventario.dto.response;

public record SolicitudDetalleResponse(
        Long idInsumo,
        String nombre,
        Integer cantidad,
        String unidad
) {
}
