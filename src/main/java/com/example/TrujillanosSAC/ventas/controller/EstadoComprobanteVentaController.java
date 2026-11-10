package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.model.EstadoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.service.EstadoComprobanteVentaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ventasEstadoComprobanteVentaController")
@RequestMapping("/api/v1/ventas/estados-comprobante")
public class EstadoComprobanteVentaController extends CatalogoController<EstadoComprobanteVenta> {

    public EstadoComprobanteVentaController(EstadoComprobanteVentaService service) {
        super(service);
    }
}
