package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;

public record DetallePedidoResponseDTO(
        Long idDetalle,
        Long productoId,
        String nombreProducto,
        String presentacion,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
