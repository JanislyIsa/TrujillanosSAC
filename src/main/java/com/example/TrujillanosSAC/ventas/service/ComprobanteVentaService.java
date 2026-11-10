package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.dto.request.ComprobanteVentaRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobanteVentaResponseDTO;

import java.util.List;

public interface ComprobanteVentaService {
    ComprobanteVentaResponseDTO emitir(ComprobanteVentaRequestDTO request, ActorVenta actor);
    ComprobanteVentaResponseDTO obtener(Long id, ActorVenta actor);
    List<ComprobanteVentaResponseDTO> listar();
}
