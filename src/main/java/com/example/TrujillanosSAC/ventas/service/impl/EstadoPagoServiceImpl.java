package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.model.EstadoPago;
import com.example.TrujillanosSAC.ventas.repository.EstadoPagoRepository;
import com.example.TrujillanosSAC.ventas.service.service.EstadoPagoService;
import org.springframework.stereotype.Service;

@Service("ventasEstadoPagoService")
public class EstadoPagoServiceImpl extends CatalogoServiceImpl<EstadoPago> implements EstadoPagoService {

    public EstadoPagoServiceImpl(EstadoPagoRepository repository) {
        super(repository, "Estados de pago");
    }
}
