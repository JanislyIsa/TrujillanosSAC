package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.dto.request.PagoCulqiRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PagoResponseDTO;

import java.util.List;

public interface PagoService {
    /** Cobra con el token de Culqi Checkout (tarjeta o Yape). */
    PagoResponseDTO pagarConCulqi(PagoCulqiRequestDTO request, ActorVenta actor);
    /** Empleado sin pedidoId: todos los pagos. Cliente: solo los de sus pedidos. */
    List<PagoResponseDTO> listar(Long pedidoId, ActorVenta actor);
}
