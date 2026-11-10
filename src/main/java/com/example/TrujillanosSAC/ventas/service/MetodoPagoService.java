package com.example.TrujillanosSAC.ventas.service;

import com.example.TrujillanosSAC.ventas.model.MetodoPago;
import com.example.TrujillanosSAC.ventas.repository.MetodoPagoRepository;
import org.springframework.stereotype.Service;

@Service("ventasMetodoPagoService")
public class MetodoPagoService extends CatalogoService<MetodoPago> {

    public MetodoPagoService(MetodoPagoRepository repository) {
        super(repository, "Métodos de pago");
    }
}
