package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.model.TipoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.repository.TipoComprobanteVentaRepository;
import org.springframework.stereotype.Service;

@Service("ventasTipoComprobanteVentaService")
public class TipoComprobanteVentaService extends CatalogoService<TipoComprobanteVenta> {

    public TipoComprobanteVentaService(TipoComprobanteVentaRepository repository) {
        super(repository, "Tipos de comprobante");
    }
}
