package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.model.EstadoPedido;
import com.example.TrujillanosSAC.ventas.repository.EstadoPedidoRepository;
import org.springframework.stereotype.Service;

@Service("ventasEstadoPedidoService")
public class EstadoPedidoService extends CatalogoService<EstadoPedido> {

    public EstadoPedidoService(EstadoPedidoRepository repository) {
        super(repository, "Estados de pedido");
    }
}
