package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long idPedido,
        LocalDateTime fechaRegistro,
        Long clienteId,
        CatalogoResponseDTO estado,
        BigDecimal total,
        List<DetallePedidoResponseDTO> detalles
) {
}
