package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.model.EstadoPago;
import com.example.TrujillanosSAC.ventas.repository.EstadoPagoRepository;
import org.springframework.stereotype.Service;

@Service("ventasEstadoPagoService")
public class EstadoPagoService extends CatalogoService<EstadoPago> {

    public EstadoPagoService(EstadoPagoRepository repository) {
        super(repository, "Estados de pago");
    }
}
