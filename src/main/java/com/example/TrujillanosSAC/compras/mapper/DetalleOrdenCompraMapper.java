package com.example.TrujillanosSAC.compras.mapper;

import com.example.TrujillanosSAC.compras.dto.DetalleOrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.model.DetalleOrdenCompra;
import org.springframework.stereotype.Component;

@Component
public class DetalleOrdenCompraMapper {
    public DetalleOrdenCompraResponseDTO toResponse(DetalleOrdenCompra detalle) {
        var insumo = detalle.getDetalleCotizacion().getInsumo();
        return new DetalleOrdenCompraResponseDTO(
                detalle.getIdDetalleOrden(), insumo.getIdInsumo(), insumo.getNombre(),
                insumo.getUnidadMedida().getNombre(), detalle.getCantidad(),
                detalle.getPrecioUnitario(), detalle.getSubtotal());
    }
}
