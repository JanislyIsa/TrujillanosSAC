package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;

/** Lo que el cliente ve de una golosina en el catálogo. No expone datos internos de inventario (almacén, mínimos). */
public record ProductoVentaResponseDTO(
        Long idProducto,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String categoria,
        String presentacion,
        String imagenUrl
) {
}
