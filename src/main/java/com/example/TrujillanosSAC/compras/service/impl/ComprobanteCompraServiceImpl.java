package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.mapper.ComprobanteCompraMapper;
import com.example.TrujillanosSAC.compras.model.ComprobanteCompra;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.EstadoComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.service.service.ComprobanteCompraService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ComprobanteCompraServiceImpl implements ComprobanteCompraService {
    private final ComprobanteCompraRepository comprobanteRepository;
    private final OrdenCompraRepository ordenRepository;
    private final EstadoComprobanteCompraRepository estadoRepository;
    private final ComprobanteCompraMapper mapper;

    public ComprobanteCompraServiceImpl(ComprobanteCompraRepository comprobanteRepository,
                                        OrdenCompraRepository ordenRepository,
                                        EstadoComprobanteCompraRepository estadoRepository,
                                        ComprobanteCompraMapper mapper) {
        this.comprobanteRepository = comprobanteRepository;
        this.ordenRepository = ordenRepository;
        this.estadoRepository = estadoRepository;
        this.mapper = mapper;
    }

    @Override @Transactional
    public ComprobanteCompraResponseDTO create(ComprobanteCompraRequestDTO request) {
        var orden = ordenRepository.findById(request.ordenCompraId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + request.ordenCompraId()));
        if (comprobanteRepository.existsByOrdenCompraIdOrdenCompra(request.ordenCompraId()))
            throw new ReglaNegocioException("La orden ya tiene un comprobante registrado");
        BigDecimal totalOrden = orden.getDetalles().stream().map(d -> d.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.monto().compareTo(totalOrden) != 0)
            throw new ReglaNegocioException("El monto del comprobante debe coincidir con el total de la orden");
        if (request.fechaEmision().isAfter(LocalDate.now()))
            throw new ReglaNegocioException("La fecha de emisión del comprobante no puede ser futura");

        ComprobanteCompra comprobante = ComprobanteCompra.builder()
                .ordenCompra(orden)
                .fechaEmision(request.fechaEmision())
                .monto(request.monto())
                .estadoComprobanteCompra(estado("Pendiente"))
                .build();
        return mapper.toResponse(comprobanteRepository.save(comprobante));
    }

    @Override @Transactional
    public ComprobanteCompraResponseDTO update(Long id, ComprobanteCompraRequestDTO request) {
        ComprobanteCompra comprobante = buscarEntidad(id);
        if (!comprobante.getOrdenCompra().getIdOrdenCompra().equals(request.ordenCompraId()))
            throw new ReglaNegocioException("No se puede cambiar la orden asociada al comprobante");
        if ("Pagado".equalsIgnoreCase(comprobante.getEstadoComprobanteCompra().getNombre()))
            throw new ReglaNegocioException("No se puede modificar un comprobante pagado");
        BigDecimal totalOrden = comprobante.getOrdenCompra().getDetalles().stream().map(d -> d.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.monto().compareTo(totalOrden) != 0)
            throw new ReglaNegocioException("El monto del comprobante debe coincidir con el total de la orden");
        comprobante.setFechaEmision(request.fechaEmision());
        comprobante.setMonto(request.monto());
        return mapper.toResponse(comprobanteRepository.save(comprobante));
    }

    @Override @Transactional(readOnly = true)
    public ComprobanteCompraResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) {
        ComprobanteCompra comprobante = buscarEntidad(id);
        if ("Pagado".equalsIgnoreCase(comprobante.getEstadoComprobanteCompra().getNombre()))
            throw new ReglaNegocioException("No se puede eliminar un comprobante pagado");
        comprobanteRepository.delete(comprobante);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<ComprobanteCompraResponseDTO> readAll() {
        return comprobanteRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    private ComprobanteCompra buscarEntidad(Long id) {
        return comprobanteRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException("Comprobante de compra no encontrado con id: " + id));
    }

    private com.example.TrujillanosSAC.compras.model.EstadoComprobanteCompra estado(String nombre) {
        return estadoRepository.findByNombreIgnoreCase(nombre).orElseThrow(() ->
                new ReglaNegocioException("No está configurado el estado del comprobante: " + nombre));
    }
}
