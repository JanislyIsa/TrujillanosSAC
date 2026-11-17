package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.model.MetodoPago;
import com.example.TrujillanosSAC.ventas.repository.MetodoPagoRepository;
import com.example.TrujillanosSAC.ventas.service.service.MetodoPagoService;
import org.springframework.stereotype.Service;

@Service("ventasMetodoPagoService")
public class MetodoPagoServiceImpl extends CatalogoServiceImpl<MetodoPago> implements MetodoPagoService {

    public MetodoPagoServiceImpl(MetodoPagoRepository repository) {
        super(repository, "Métodos de pago");
    }
}
