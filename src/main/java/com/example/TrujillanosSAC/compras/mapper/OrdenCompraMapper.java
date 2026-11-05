package com.example.TrujillanosSAC.compras.mapper;

import com.example.TrujillanosSAC.compras.dto.OrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.dto.DetalleOrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class OrdenCompraMapper {
    private final DetalleOrdenCompraMapper detalleMapper;

    public OrdenCompraMapper(DetalleOrdenCompraMapper detalleMapper) {
        this.detalleMapper = detalleMapper;
    }

    public OrdenCompraResponseDTO toResponse(OrdenCompra orden) {
        var proveedor = orden.getCotizacion().getProveedor();
        String proveedorNombre = proveedor.getPersona().getNombre() + " "
                + proveedor.getPersona().getApellido();
        var detalles = orden.getDetalles().stream().map(detalleMapper::toResponse).toList();
        BigDecimal total = detalles.stream().map(DetalleOrdenCompraResponseDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrdenCompraResponseDTO(
                orden.getIdOrdenCompra(), orden.getFechaEmision(), orden.getDescripcion(),
                orden.getCotizacion().getIdCotizacion(), proveedorNombre, total, detalles);
    }
}
