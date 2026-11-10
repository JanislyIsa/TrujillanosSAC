package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.model.MetodoPago;
import com.example.TrujillanosSAC.ventas.service.MetodoPagoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ventasMetodoPagoController")
@RequestMapping("/api/v1/ventas/metodos-pago")
public class MetodoPagoController extends CatalogoController<MetodoPago> {

    public MetodoPagoController(MetodoPagoService service) {
        super(service);
    }
}
