package com.example.TrujillanosSAC.ventas.service.facturacion;

import com.example.TrujillanosSAC.ventas.model.ComprobanteVenta;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoSunat;
import org.springframework.stereotype.Component;

/** Implementación mientras no exista integración con SUNAT: el comprobante queda como NO_ENVIADO. */
@Component
public class SinFacturacionElectronica implements FacturacionElectronica {

    @Override
    public EstadoSunat enviar(ComprobanteVenta comprobante) {
        return EstadoSunat.NO_ENVIADO;
    }
}
