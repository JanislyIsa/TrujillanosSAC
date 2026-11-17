package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.model.EstadoPedido;
import com.example.TrujillanosSAC.ventas.repository.EstadoPedidoRepository;
import com.example.TrujillanosSAC.ventas.service.service.EstadoPedidoService;
import org.springframework.stereotype.Service;

@Service("ventasEstadoPedidoService")
public class EstadoPedidoServiceImpl extends CatalogoServiceImpl<EstadoPedido> implements EstadoPedidoService {

    public EstadoPedidoServiceImpl(EstadoPedidoRepository repository) {
        super(repository, "Estados de pedido");
    }
}
