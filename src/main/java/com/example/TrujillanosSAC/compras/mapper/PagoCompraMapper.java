package com.example.TrujillanosSAC.compras.mapper;

import com.example.TrujillanosSAC.compras.dto.PagoCompraResponseDTO;
import com.example.TrujillanosSAC.compras.model.PagoCompra;
import org.springframework.stereotype.Component;

@Component
public class PagoCompraMapper {
    public PagoCompraResponseDTO toResponse(PagoCompra pago) {
        return new PagoCompraResponseDTO(
                pago.getIdPago(), pago.getMonto(), pago.getFechaPago(),
                pago.getComprobanteCompra().getIdComprobante(), pago.getMetodoPago().getIdMetodo(),
                pago.getMetodoPago().getNombre());
    }
}
