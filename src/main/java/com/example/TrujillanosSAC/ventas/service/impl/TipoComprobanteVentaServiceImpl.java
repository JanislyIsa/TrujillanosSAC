package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.model.TipoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.repository.TipoComprobanteVentaRepository;
import com.example.TrujillanosSAC.ventas.service.service.TipoComprobanteVentaService;
import org.springframework.stereotype.Service;

@Service("ventasTipoComprobanteVentaService")
public class TipoComprobanteVentaServiceImpl extends CatalogoServiceImpl<TipoComprobanteVenta> implements TipoComprobanteVentaService {

    public TipoComprobanteVentaServiceImpl(TipoComprobanteVentaRepository repository) {
        super(repository, "Tipos de comprobante");
    }
}
