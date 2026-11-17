package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.model.EstadoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.repository.EstadoComprobanteVentaRepository;
import com.example.TrujillanosSAC.ventas.service.service.EstadoComprobanteVentaService;
import org.springframework.stereotype.Service;

@Service("ventasEstadoComprobanteVentaService")
public class EstadoComprobanteVentaServiceImpl extends CatalogoServiceImpl<EstadoComprobanteVenta> implements EstadoComprobanteVentaService {

    public EstadoComprobanteVentaServiceImpl(EstadoComprobanteVentaRepository repository) {
        super(repository, "Estados de comprobante");
    }
}
