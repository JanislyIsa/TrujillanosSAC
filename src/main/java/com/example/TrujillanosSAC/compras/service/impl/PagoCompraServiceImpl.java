package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.PagoCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.PagoCompraResponseDTO;
import com.example.TrujillanosSAC.compras.mapper.PagoCompraMapper;
import com.example.TrujillanosSAC.compras.model.PagoCompra;
import com.example.TrujillanosSAC.compras.repository.*;
import com.example.TrujillanosSAC.compras.service.service.PagoCompraService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PagoCompraServiceImpl implements PagoCompraService {
    private final PagoCompraRepository pagoRepository;
    private final ComprobanteCompraRepository comprobanteRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoComprobanteCompraRepository estadoComprobanteRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final PagoCompraMapper mapper;

    public PagoCompraServiceImpl(PagoCompraRepository pagoRepository,
                                 ComprobanteCompraRepository comprobanteRepository,
                                 MetodoPagoRepository metodoPagoRepository,
                                 EstadoComprobanteCompraRepository estadoComprobanteRepository,
                                 EstadoCotizacionRepository estadoCotizacionRepository,
                                 PagoCompraMapper mapper) {
        this.pagoRepository = pagoRepository;
        this.comprobanteRepository = comprobanteRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.estadoComprobanteRepository = estadoComprobanteRepository;
        this.estadoCotizacionRepository = estadoCotizacionRepository;
        this.mapper = mapper;
    }

    @Override @Transactional
    public PagoCompraResponseDTO create(PagoCompraRequestDTO request) {
        var comprobante = comprobanteRepository.findById(request.comprobanteId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Comprobante de compra no encontrado con id: " + request.comprobanteId()));
        var metodo = metodoPagoRepository.findById(request.metodoPagoId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Método de pago no encontrado con id: " + request.metodoPagoId()));
        if (request.fechaPago().isAfter(LocalDate.now()))
            throw new ReglaNegocioException("La fecha de pago no puede ser futura");
        BigDecimal pagado = pagoRepository.findByComprobanteCompraIdComprobante(comprobante.getIdComprobante())
                .stream().map(PagoCompra::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo = comprobante.getMonto().subtract(pagado);
        if (saldo.signum() <= 0) throw new ReglaNegocioException("El comprobante ya está pagado completamente");
        if (request.monto().compareTo(saldo) > 0)
            throw new ReglaNegocioException("El pago supera el saldo pendiente de " + saldo);

        PagoCompra pago = PagoCompra.builder().comprobanteCompra(comprobante).metodoPago(metodo)
                .monto(request.monto()).fechaPago(request.fechaPago()).build();
        PagoCompra guardado = pagoRepository.save(pago);
        BigDecimal nuevoTotalPagado = pagado.add(request.monto());
        if (nuevoTotalPagado.compareTo(comprobante.getMonto()) == 0) {
            comprobante.setEstadoComprobanteCompra(estado("Pagado"));
            comprobanteRepository.save(comprobante);
            boolean todosPagados = comprobanteRepository.findByOrdenCompraIdOrdenCompra(
                            comprobante.getOrdenCompra().getIdOrdenCompra()).stream()
                    .allMatch(c -> "Pagado".equalsIgnoreCase(c.getEstadoComprobanteCompra().getNombre()));
            if (todosPagados) {
                var cotizacion = comprobante.getOrdenCompra().getCotizacion();
                cotizacion.setEstadoCotizacion(estadoCotizacionRepository.findByNombreIgnoreCase("Pagado")
                        .orElseThrow(() -> new ReglaNegocioException("No está configurado el estado de cotización: Pagado")));
            }
        }
        return mapper.toResponse(guardado);
    }

    @Override @Transactional
    public PagoCompraResponseDTO update(Long id, PagoCompraRequestDTO request) {
        PagoCompra pago = buscarEntidad(id);
        if (!pago.getComprobanteCompra().getIdComprobante().equals(request.comprobanteId()))
            throw new ReglaNegocioException("No se puede cambiar el comprobante asociado al pago");
        BigDecimal otrosPagos = pagoRepository.findByComprobanteCompraIdComprobante(request.comprobanteId()).stream()
                .filter(p -> !p.getIdPago().equals(id)).map(PagoCompra::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (otrosPagos.add(request.monto()).compareTo(pago.getComprobanteCompra().getMonto()) > 0)
            throw new ReglaNegocioException("El pago supera el saldo pendiente del comprobante");
        var metodo = metodoPagoRepository.findById(request.metodoPagoId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Método de pago no encontrado con id: " + request.metodoPagoId()));
        pago.setMonto(request.monto());
        pago.setFechaPago(request.fechaPago());
        pago.setMetodoPago(metodo);
        PagoCompra actualizado = pagoRepository.save(pago);
        sincronizarEstadoComprobante(pago.getComprobanteCompra());
        return mapper.toResponse(actualizado);
    }

    @Override @Transactional(readOnly = true)
    public PagoCompraResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) {
        PagoCompra pago = buscarEntidad(id);
        var comprobante = pago.getComprobanteCompra();
        pagoRepository.delete(pago);
        pagoRepository.flush();
        BigDecimal totalPagado = pagoRepository.findByComprobanteCompraIdComprobante(comprobante.getIdComprobante())
                .stream().map(PagoCompra::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPagado.compareTo(comprobante.getMonto()) < 0) {
            comprobante.setEstadoComprobanteCompra(estado("Pendiente"));
            comprobanteRepository.save(comprobante);
        }
        sincronizarEstadoCotizacion(comprobante);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<PagoCompraResponseDTO> readAll() {
        return pagoRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    private PagoCompra buscarEntidad(Long id) {
        return pagoRepository.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException("Pago de compra no encontrado con id: " + id));
    }

    private com.example.TrujillanosSAC.compras.model.EstadoComprobanteCompra estado(String nombre) {
        return estadoComprobanteRepository.findByNombreIgnoreCase(nombre).orElseThrow(() ->
                new ReglaNegocioException("No está configurado el estado del comprobante: " + nombre));
    }

    private void sincronizarEstadoComprobante(com.example.TrujillanosSAC.compras.model.ComprobanteCompra comprobante) {
        BigDecimal totalPagado = pagoRepository.findByComprobanteCompraIdComprobante(comprobante.getIdComprobante())
                .stream().map(PagoCompra::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean pagado = totalPagado.compareTo(comprobante.getMonto()) >= 0;
        comprobante.setEstadoComprobanteCompra(estado(pagado ? "Pagado" : "Pendiente"));
        comprobanteRepository.save(comprobante);
        sincronizarEstadoCotizacion(comprobante);
    }

    private void sincronizarEstadoCotizacion(com.example.TrujillanosSAC.compras.model.ComprobanteCompra comprobante) {
        boolean todosPagados = comprobanteRepository.findByOrdenCompraIdOrdenCompra(
                        comprobante.getOrdenCompra().getIdOrdenCompra()).stream()
                .allMatch(c -> "Pagado".equalsIgnoreCase(c.getEstadoComprobanteCompra().getNombre()));
        if (todosPagados) {
            comprobante.getOrdenCompra().getCotizacion().setEstadoCotizacion(
                    estadoCotizacionRepository.findByNombreIgnoreCase("Pagado")
                            .orElseThrow(() -> new ReglaNegocioException("No está configurado el estado de cotización: Pagado")));
        } else if ("Pagado".equalsIgnoreCase(comprobante.getOrdenCompra().getCotizacion().getEstadoCotizacion().getNombre())) {
            comprobante.getOrdenCompra().getCotizacion().setEstadoCotizacion(
                    estadoCotizacionRepository.findByNombreIgnoreCase("Aceptada")
                            .orElseThrow(() -> new ReglaNegocioException("No está configurado el estado de cotización: Aceptada")));
        }
    }
}
