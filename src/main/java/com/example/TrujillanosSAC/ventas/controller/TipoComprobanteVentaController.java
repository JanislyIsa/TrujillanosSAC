package com.example.TrujillanosSAC.ventas.controller;

import com.example.TrujillanosSAC.ventas.model.TipoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.service.TipoComprobanteVentaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("ventasTipoComprobanteVentaController")
@RequestMapping("/api/v1/ventas/tipos-comprobante")
public class TipoComprobanteVentaController extends CatalogoController<TipoComprobanteVenta> {

    public TipoComprobanteVentaController(TipoComprobanteVentaService service) {
        super(service);
    }
}
