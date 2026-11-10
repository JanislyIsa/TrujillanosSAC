package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.model.EstadoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.repository.EstadoComprobanteVentaRepository;
import org.springframework.stereotype.Service;

@Service("ventasEstadoComprobanteVentaService")
public class EstadoComprobanteVentaService extends CatalogoService<EstadoComprobanteVenta> {

    public EstadoComprobanteVentaService(EstadoComprobanteVentaRepository repository) {
        super(repository, "Estados de comprobante");
    }
}
