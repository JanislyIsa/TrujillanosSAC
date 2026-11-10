package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;

public record DetallePedidoResponseDTO(
        Long idDetalle,
        Long productoId,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
