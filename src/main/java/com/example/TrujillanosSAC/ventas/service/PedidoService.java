package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.dto.request.PedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PedidoResponseDTO;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crear(PedidoRequestDTO request, ActorVenta actor);
    List<PedidoResponseDTO> listar(ActorVenta actor);
    PedidoResponseDTO obtener(Long id, ActorVenta actor);
    PedidoResponseDTO cancelar(Long id, ActorVenta actor);
    PedidoResponseDTO cambiarEstado(Long id, EstadoPedidoCodigo destino);
    /** Cancela los pedidos que siguen pendientes de pago desde antes de {@code limite}. Devuelve cuántos. */
    int cancelarVencidos(LocalDateTime limite);
}
