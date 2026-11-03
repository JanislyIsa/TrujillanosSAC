package com.example.TrujillanosSAC.Service;

import com.example.TrujillanosSAC.Dto.PedidoResumenDto;
import com.example.TrujillanosSAC.Dto.CheckoutRequest;

import java.util.List;

public interface PedidoService {
    List<PedidoResumenDto> listarPedidosDelCliente(String username);

    Long registrarPedido(String username, CheckoutRequest request);
}
