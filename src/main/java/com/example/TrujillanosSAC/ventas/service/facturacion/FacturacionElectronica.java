package com.example.TrujillanosSAC.ventas.service.facturacion;

import com.example.TrujillanosSAC.ventas.model.ComprobanteVenta;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoSunat;

/**
 * Punto de conexión con SUNAT. Si deciden integrarla (normalmente a través de un proveedor OSE/PSE),
 * basta con crear otra implementación de esta interfaz y quitar {@link SinFacturacionElectronica}.
 */
public interface FacturacionElectronica {
    EstadoSunat enviar(ComprobanteVenta comprobante);
}
