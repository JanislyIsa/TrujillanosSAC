package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.dto.MovimientoInventarioDto;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import com.example.TrujillanosSAC.inventario.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {
    private final InventarioBloqueos bloqueos;
    private final ProductoRepository productos;
    private final InsumoRepository insumos;
    private final MovimientoRepository movimientos;
    private final ComprobanteMovimientoRepository comprobantes;
    private final DetalleMovProductoRepository detallesProducto;
    private final DetalleMovInsumoRepository detallesInsumo;
    private final AlertaInventarioService alertas;
    private final InventarioValidacion validacion;
    private final Clock inventarioClock;

    @Override @Transactional
    public ComprobanteResponse registrar(MovimientoRequest request, String responsable) {
        validacion.validar(request);
        if (responsable == null || responsable.isBlank() || responsable.length() > 100)
            throw new IllegalArgumentException("Responsable invalido");
        LocalDateTime ahora = LocalDateTime.now(inventarioClock);
        Movimiento movimiento = Movimiento.builder().tipoMovimiento(request.tipoMovimiento().valor())
                .fecha(ahora.toLocalDate()).hora(ahora).responsable(responsable)
                .motivo(request.motivo().trim()).build();
        MovimientoInventarioDto dto;
        if (request.tipoItem() == TipoItem.PRODUCTO) {
            Producto item = bloqueos.producto(request.idItem())
                    .orElseThrow(() -> InventarioException.noEncontrado("Producto"));
            int anterior = item.getStock();
            int nuevo = calcular(anterior, request);
            movimiento.setAlmacen(item.getAlmacen());
            movimientos.save(movimiento);
            item.setStock(nuevo);
            DetalleMovProducto detalle = detallesProducto.save(DetalleMovProducto.builder()
                    .producto(item).movimiento(movimiento).cantidad(request.cantidad())
                    .stockAnterior(anterior).stockNuevo(nuevo).nombreItem(item.getNombre())
                    .unidadItem(item.getPresentacion().getNombre()).build());
            dto = mapear(detalle);
        } else {
            Insumo item = bloqueos.insumo(request.idItem())
                    .orElseThrow(() -> InventarioException.noEncontrado("Insumo"));
            int anterior = item.getStock();
            int nuevo = calcular(anterior, request);
            movimiento.setAlmacen(item.getAlmacen());
            movimientos.save(movimiento);
            item.setStock(nuevo);
            DetalleMovInsumo detalle = detallesInsumo.save(DetalleMovInsumo.builder()
                    .insumo(item).movimiento(movimiento).cantidad(request.cantidad())
                    .stockAnterior(anterior).stockNuevo(nuevo).nombreItem(item.getNombre())
                    .unidadItem(item.getUnidadMedida().getNombre()).build());
            dto = mapear(detalle);
        }
        alertas.sincronizar(request.tipoItem(), request.idItem());
        ComprobanteMovimiento comprobante = comprobantes.save(ComprobanteMovimiento.builder()
                .movimiento(movimiento).codigo(dto.codigoComprobante()).fechaEmision(ahora)
                .responsable(responsable).motivo(movimiento.getMotivo()).build());
        return new ComprobanteResponse(dto, comprobante.getResponsable(), comprobante.getMotivo());
    }

    private int calcular(int anterior, MovimientoRequest request) {
        long nuevo = request.tipoMovimiento() == TipoMovimiento.INGRESO
                ? (long)anterior + request.cantidad() : (long)anterior - request.cantidad();
        if (nuevo < 0) throw InventarioException.conflicto("La salida supera el stock disponible");
        if (nuevo > Integer.MAX_VALUE) throw InventarioException.conflicto("El stock supera la capacidad permitida");
        return (int)nuevo;
    }

    @Override
    public List<MovimientoInventarioDto> listar(TipoItem tipo, Long idItem,
            TipoMovimiento movimiento, LocalDate desde, LocalDate hasta, Long almacen) {
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new IllegalArgumentException("El rango de fechas es invalido");
        Stream<MovimientoInventarioDto> p = tipo == TipoItem.INSUMO ? Stream.empty()
                : detallesProducto.findAllParaInventario().stream()
                .filter(d -> idItem == null || idItem.equals(d.getProducto().getIdProducto())).map(this::mapear);
        Stream<MovimientoInventarioDto> i = tipo == TipoItem.PRODUCTO ? Stream.empty()
                : detallesInsumo.findAllParaInventario().stream()
                .filter(d -> idItem == null || idItem.equals(d.getInsumo().getIdInsumo())).map(this::mapear);
        Set<Long> idsAlmacen = almacen == null ? null : new HashSet<>(movimientos.findAllParaInventario()
                .stream().filter(m -> almacen.equals(m.getAlmacen().getIdAlmacen()))
                .map(Movimiento::getIdMovimiento).toList());
        return Stream.concat(p,i)
                .filter(d -> movimiento == null || movimiento.valor().equalsIgnoreCase(d.tipoMovimiento()))
                .filter(d -> desde == null || !d.fecha().isBefore(desde))
                .filter(d -> hasta == null || !d.fecha().isAfter(hasta))
                .filter(d -> idsAlmacen == null || idsAlmacen.contains(d.idMovimiento()))
                .sorted(Comparator.comparing(MovimientoInventarioDto::idMovimiento).reversed()).toList();
    }

    @Override
    public ComprobanteResponse comprobante(Long id) {
        ComprobanteMovimiento comprobante = comprobantes.findById(id)
                .orElseThrow(() -> InventarioException.noEncontrado("Comprobante del movimiento"));
        MovimientoInventarioDto dto = listar(null,null,null,null,null,null).stream()
                .filter(d -> id.equals(d.idMovimiento())).findFirst()
                .orElseThrow(() -> InventarioException.noEncontrado("Detalle del movimiento"));
        var guardado = new MovimientoInventarioDto(dto.idMovimiento(),dto.codigoMovimiento(),
                comprobante.getCodigo(),dto.tipoMovimiento(),dto.tipoItem(),dto.nombreItem(),
                dto.cantidad(),dto.unidad(),dto.stockAnterior(),dto.stockNuevo(),dto.fecha(),dto.hora(),dto.almacen());
        return new ComprobanteResponse(guardado, comprobante.getResponsable(), comprobante.getMotivo());
    }

    private MovimientoInventarioDto mapear(DetalleMovProducto d) {
        return dto(d.getMovimiento(), "Producto",
                d.getNombreItem() != null ? d.getNombreItem() : d.getProducto().getNombre(),
                d.getCantidad(), d.getUnidadItem() != null ? d.getUnidadItem() : d.getProducto().getPresentacion().getNombre(),
                d.getStockAnterior(), d.getStockNuevo());
    }
    private MovimientoInventarioDto mapear(DetalleMovInsumo d) {
        return dto(d.getMovimiento(), "Insumo",
                d.getNombreItem() != null ? d.getNombreItem() : d.getInsumo().getNombre(),
                d.getCantidad(), d.getUnidadItem() != null ? d.getUnidadItem() : d.getInsumo().getUnidadMedida().getNombre(),
                d.getStockAnterior(), d.getStockNuevo());
    }
    private MovimientoInventarioDto dto(Movimiento m, String tipo, String nombre,
            int cantidad, String unidad, Integer anterior, Integer nuevo) {
        return new MovimientoInventarioDto(m.getIdMovimiento(), "MOV-%03d".formatted(m.getIdMovimiento()),
                "CM-%03d".formatted(m.getIdMovimiento()), m.getTipoMovimiento(), tipo, nombre,
                cantidad, unidad, anterior, nuevo, m.getFecha(), m.getHora(), m.getAlmacen().getNombre());
    }
}
