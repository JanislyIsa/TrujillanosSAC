package com.example.TrujillanosSAC.ventas.service.service;

import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.dto.request.ComprobanteVentaRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobanteVentaResponseDTO;

import java.util.List;

public interface ComprobanteVentaService {
    ComprobanteVentaResponseDTO emitir(ComprobanteVentaRequestDTO request, ActorVenta actor);
    ComprobanteVentaResponseDTO obtener(Long id, ActorVenta actor);
    /** Comprobante de un pedido; 404 si todavía no se emitió. El cliente solo ve los de sus pedidos. */
    ComprobanteVentaResponseDTO obtenerPorPedido(Long pedidoId, ActorVenta actor);
    List<ComprobanteVentaResponseDTO> listar();
}
