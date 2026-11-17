package com.example.TrujillanosSAC.compras.mapper;

import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.model.Cotizacion;
import org.springframework.stereotype.Component;

@Component
public class CotizacionMapper {
    private final DetalleCotizacionMapper detalleMapper;

    public CotizacionMapper(DetalleCotizacionMapper detalleMapper) {
        this.detalleMapper = detalleMapper;
    }

    public CotizacionResponseDTO toResponse(Cotizacion cotizacion) {
        var proveedor = cotizacion.getProveedor();
        String proveedorNombre = proveedor.getPersona().getNombre() + " "
                + proveedor.getPersona().getApellido();
        return new CotizacionResponseDTO(
                cotizacion.getIdCotizacion(), cotizacion.getFechaCot(), cotizacion.getFechaEntrega(),
                cotizacion.getEstadoCotizacion().getNombre(),
                cotizacion.getSolicitudCompra().getIdSolicitud(), proveedor.getIdPersona(),
                proveedorNombre, cotizacion.getMonto(), cotizacion.getDetalles().stream()
                .map(detalleMapper::toResponse).toList(), cotizacion.getDescripcion());
    }
}
