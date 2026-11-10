package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.model.EstadoPago;
import com.example.TrujillanosSAC.ventas.service.EstadoPagoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ventasEstadoPagoController")
@RequestMapping("/api/v1/ventas/estados-pago")
public class EstadoPagoController extends CatalogoController<EstadoPago> {

    public EstadoPagoController(EstadoPagoService service) {
        super(service);
    }
}
