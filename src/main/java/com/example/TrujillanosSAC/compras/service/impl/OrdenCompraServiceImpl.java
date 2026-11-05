package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.OrdenCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.mapper.OrdenCompraMapper;
import com.example.TrujillanosSAC.compras.model.DetalleOrdenCompra;
import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import com.example.TrujillanosSAC.compras.repository.CotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.service.service.OrdenCompraService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class OrdenCompraServiceImpl implements OrdenCompraService {
    private final OrdenCompraRepository ordenRepository;
    private final CotizacionRepository cotizacionRepository;
    private final ComprobanteCompraRepository comprobanteRepository;
    private final OrdenCompraMapper mapper;

    public OrdenCompraServiceImpl(OrdenCompraRepository ordenRepository,
                                  CotizacionRepository cotizacionRepository,
                                  ComprobanteCompraRepository comprobanteRepository,
                                  OrdenCompraMapper mapper) {
        this.ordenRepository = ordenRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.mapper = mapper;
    }

    @Override @Transactional
    public OrdenCompraResponseDTO create(OrdenCompraRequestDTO request) {
        return generarDesdeCotizacion(request.cotizacionId(), request.descripcion());
    }

    @Override @Transactional
    public OrdenCompraResponseDTO generarDesdeCotizacion(Long cotizacionId, String descripcion) {
        var cotizacion = cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new RecursosNoEncontradosException("Cotización no encontrada con id: " + cotizacionId));
        if (!"Aceptada".equalsIgnoreCase(cotizacion.getEstadoCotizacion().getNombre()))
            throw new ReglaNegocioException("La cotización debe estar aceptada antes de generar la orden");
        if (ordenRepository.findByCotizacionIdCotizacion(cotizacionId).isPresent())
            throw new ReglaNegocioException("La cotización ya tiene una orden de compra");
        if (cotizacion.getDetalles().isEmpty()) throw new ReglaNegocioException("No se puede generar una orden sin detalles");

        OrdenCompra orden = OrdenCompra.builder()
                .fechaEmision(LocalDate.now())
                .descripcion(descripcion == null || descripcion.isBlank()
                        ? "Orden generada desde cotización " + cotizacionId : descripcion.trim())
                .cotizacion(cotizacion)
                .build();
        for (var detalle : cotizacion.getDetalles()) {
            orden.agregarDetalle(DetalleOrdenCompra.builder()
                    .detalleCotizacion(detalle)
                    .cantidad(detalle.getCantidad())
                    .precioUnitario(detalle.getPrecioUnitario())
                    .subtotal(detalle.getSubtotal())
                    .build());
        }
        return mapper.toResponse(ordenRepository.save(orden));
    }

    @Override @Transactional
    public OrdenCompraResponseDTO update(Long id, OrdenCompraRequestDTO request) {
        OrdenCompra orden = buscarEntidad(id);
        if (!orden.getCotizacion().getIdCotizacion().equals(request.cotizacionId()))
            throw new ReglaNegocioException("No se puede cambiar la cotización asociada a una orden existente");
        orden.setDescripcion(request.descripcion());
        return mapper.toResponse(ordenRepository.save(orden));
    }

    @Override @Transactional(readOnly = true)
    public OrdenCompraResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) {
        OrdenCompra orden = buscarEntidad(id);
        if (!comprobanteRepository.findByOrdenCompraIdOrdenCompra(id).isEmpty())
            throw new ReglaNegocioException("No se puede eliminar una orden que ya tiene comprobantes asociados");
        ordenRepository.delete(orden);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<OrdenCompraResponseDTO> readAll() {
        return ordenRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    private OrdenCompra buscarEntidad(Long id) {
        return ordenRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + id));
    }
}
