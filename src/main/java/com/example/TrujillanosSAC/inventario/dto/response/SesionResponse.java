package com.example.TrujillanosSAC.inventario.dto.response;

public record SesionResponse(
        String username,
        String nombre,
        String cargo,
        boolean inventario,
        boolean compras
) {
}
