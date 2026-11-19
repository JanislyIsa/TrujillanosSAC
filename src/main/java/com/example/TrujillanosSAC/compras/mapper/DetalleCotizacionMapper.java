package com.example.TrujillanosSAC.compras.mapper;

import com.example.TrujillanosSAC.compras.dto.DetalleCotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.model.DetalleCotizacion;
import org.springframework.stereotype.Component;

@Component
public class DetalleCotizacionMapper {
    public DetalleCotizacionResponseDTO toResponse(DetalleCotizacion detalle) {
        var insumo = detalle.getInsumo();
        return new DetalleCotizacionResponseDTO(
                detalle.getIdDetalleCot(), insumo.getIdInsumo(), insumo.getNombre(),
                insumo.getUnidadMedida().getNombre(), detalle.getCantidad(),
                detalle.getPrecioUnitario(), detalle.getSubtotal());
    }
}
