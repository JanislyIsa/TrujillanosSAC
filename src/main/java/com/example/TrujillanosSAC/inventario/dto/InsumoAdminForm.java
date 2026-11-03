package com.example.TrujillanosSAC.inventario.dto;

public record InsumoAdminForm(
        String nombre,
        Integer stock,
        Integer stockMinimo,
        Integer stockLimite,
        Long idUnidadMedida,
        Long idAlmacen
) {
}
