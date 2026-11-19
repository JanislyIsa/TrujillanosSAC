package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.OrdenCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.mapper.OrdenCompraMapper;
import com.example.TrujillanosSAC.compras.mapper.ComprobanteCompraMapper;
import com.example.TrujillanosSAC.compras.model.DetalleOrdenCompra;
import com.example.TrujillanosSAC.compras.model.OrdenCompra;
import com.example.TrujillanosSAC.compras.repository.CotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.repository.PagoCompraRepository;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraListadoResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenesResumenResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
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
    private final ComprobanteCompraMapper comprobanteMapper;
    private final PagoCompraRepository pagoRepository;

    public OrdenCompraServiceImpl(OrdenCompraRepository ordenRepository,
                                  CotizacionRepository cotizacionRepository,
                                  ComprobanteCompraRepository comprobanteRepository,
                                  OrdenCompraMapper mapper,
                                  ComprobanteCompraMapper comprobanteMapper,
                                  PagoCompraRepository pagoRepository) {
        this.ordenRepository = ordenRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.mapper = mapper;
        this.comprobanteMapper = comprobanteMapper;
        this.pagoRepository = pagoRepository;
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

    @Override @Transactional(readOnly = true)
    public Page<OrdenCompraListadoResponseDTO> listarPagina(String buscar, Pageable pageable) {
        Page<OrdenCompra> pagina = ordenRepository.buscarPagina(buscar == null ? "" : buscar.trim(), pageable);
        var ordenes = pagina.getContent();
        if (ordenes.isEmpty()) return new PageImpl<>(java.util.List.of(), pagina.getPageable(), pagina.getTotalElements());
        var idsOrden = ordenes.stream().map(OrdenCompra::getIdOrdenCompra).toList();
        var comprobantes = comprobanteRepository.findByOrdenCompraIdOrdenCompraIn(idsOrden);
        var comprobantePorOrden = comprobantes.stream().collect(java.util.stream.Collectors.toMap(
                c -> c.getOrdenCompra().getIdOrdenCompra(), java.util.function.Function.identity(), (a, b) -> a));
        var comprobanteIds = comprobantes.stream().map(com.example.TrujillanosSAC.compras.model.ComprobanteCompra::getIdComprobante).toList();
        var totalPagadoPorComprobante = comprobanteIds.isEmpty() ? java.util.Map.<Long, java.math.BigDecimal>of()
                : pagoRepository.findByComprobanteCompraIdComprobanteIn(comprobanteIds).stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                        p -> p.getComprobanteCompra().getIdComprobante(),
                        java.util.stream.Collectors.reducing(java.math.BigDecimal.ZERO,
                                com.example.TrujillanosSAC.compras.model.PagoCompra::getMonto, java.math.BigDecimal::add)));
        return pagina.map(orden -> {
            var base = mapper.toResponse(orden);
            var comprobante = comprobantePorOrden.get(orden.getIdOrdenCompra());
            var totalPagado = comprobante == null ? java.math.BigDecimal.ZERO
                    : totalPagadoPorComprobante.getOrDefault(comprobante.getIdComprobante(), java.math.BigDecimal.ZERO);
            var saldo = base.total().subtract(totalPagado).max(java.math.BigDecimal.ZERO);
            return new OrdenCompraListadoResponseDTO(base.id(), base.fechaEmision(), base.descripcion(),
                    base.cotizacionId(), base.proveedorNombre(), base.total(), base.detalles(),
                    comprobante == null ? null : comprobanteMapper.toResponse(comprobante), totalPagado, saldo);
        });
    }

    @Override @Transactional(readOnly = true)
    public OrdenesResumenResponseDTO resumen() {
        var resumen = ordenRepository.resumenOrdenes();
        return new OrdenesResumenResponseDTO(resumen.getTotal(), resumen.getPendientes(),
                resumen.getMontoPendiente() == null ? java.math.BigDecimal.ZERO : resumen.getMontoPendiente());
    }

    private OrdenCompra buscarEntidad(Long id) {
        return ordenRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + id));
    }
}
