package com.example.TrujillanosSAC.compras.mapper;

import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.model.ComprobanteCompra;
import org.springframework.stereotype.Component;

@Component
public class ComprobanteCompraMapper {
    public ComprobanteCompraResponseDTO toResponse(ComprobanteCompra comprobante) {
        return new ComprobanteCompraResponseDTO(
                comprobante.getIdComprobante(), comprobante.getFechaEmision(), comprobante.getMonto(),
                comprobante.getEstadoComprobanteCompra().getNombre(),
                comprobante.getOrdenCompra().getIdOrdenCompra(), comprobante.getNumeroFactura());
    }
}
