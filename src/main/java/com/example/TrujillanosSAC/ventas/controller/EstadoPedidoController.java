package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.model.EstadoPedido;
import com.example.TrujillanosSAC.ventas.service.EstadoPedidoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ventasEstadoPedidoController")
@RequestMapping("/api/v1/ventas/estados-pedido")
public class EstadoPedidoController extends CatalogoController<EstadoPedido> {

    public EstadoPedidoController(EstadoPedidoService service) {
        super(service);
    }
}
