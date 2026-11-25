package com.example.TrujillanosSAC.inventario.service.impl;

import com.example.TrujillanosSAC.inventario.dto.request.InsumoRequest;
import com.example.TrujillanosSAC.inventario.dto.request.MovimientoRequest;
import com.example.TrujillanosSAC.inventario.dto.request.ProductoRequest;
import com.example.TrujillanosSAC.inventario.dto.response.ItemResponse;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import com.example.TrujillanosSAC.inventario.mapper.ItemInventarioMapper;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.repository.*;
import com.example.TrujillanosSAC.inventario.service.service.*;
import com.example.TrujillanosSAC.auditoria.EventoAuditoria;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service @RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoInventarioServiceImpl implements CatalogoInventarioService {
    private final InventarioBloqueos bloqueos;
    private final ItemInventarioMapper mapper;
    private final ProductoRepository productos;
    private final InsumoRepository insumos;
    private final CategoriaRepository categorias;
    private final PresentacionProductoRepository presentaciones;
    private final UnidadMedidaRepository unidades;
    private final AlmacenRepository almacenes;
    private final AlertaRepository alertasRepository;
    private final MovimientoInventarioService movimientos;
    private final AlertaInventarioService alertas;
    private final InventarioValidacion validacion;
    private final ApplicationEventPublisher eventos;

    @Override
    public List<ItemResponse> listar(TipoItem tipo, String buscar, Long almacen, EstadoStock estado) {
        String texto = buscar == null ? "" : buscar.trim().toLowerCase(Locale.ROOT);
        Stream<ItemResponse> p = tipo == TipoItem.INSUMO ? Stream.empty()
                : productos.findAllParaGestion().stream().map(mapper::aRespuesta);
        Stream<ItemResponse> i = tipo == TipoItem.PRODUCTO ? Stream.empty()
                : insumos.findAllParaInventario().stream().map(mapper::aRespuesta);
        return Stream.concat(p,i).filter(x -> x.nombre().toLowerCase(Locale.ROOT).contains(texto)
                || ((x.tipoItem()==TipoItem.PRODUCTO?"prod-":"ins-")+String.format("%03d",x.id())).contains(texto)
                || ((x.tipoItem()==TipoItem.PRODUCTO?"prd-":"ins-")+String.format("%03d",x.id())).contains(texto))
                .filter(x -> almacen == null || almacen.equals(x.idAlmacen()))
                .filter(x -> estado == null || estado == x.estadoStock()).toList();
    }

    @Override
    public Page<ItemResponse> listarPagina(TipoItem tipo, String buscar, Long almacen, Boolean activo,
                                           EstadoStock estado, Pageable pageable) {
        if (tipo == null) throw new IllegalArgumentException("El tipo de ítem es obligatorio para paginar");
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() > 100)
            throw new IllegalArgumentException("Use página >= 0 y tamaño entre 1 y 100");
        String texto = buscar == null || buscar.isBlank() ? null : buscar.trim().toLowerCase(Locale.ROOT);
        Long id = extraerId(texto, tipo);
        String patron = texto == null || id != null ? null : texto;
        PageRequestSeguro validar = new PageRequestSeguro(pageable.getPageSize());
        if (tipo == TipoItem.PRODUCTO) {
            return productos.buscarPagina(patron, id, almacen, activo, estado == null ? null : estado.name(),
                    validar.conOrdenNombre(pageable)).map(mapper::aRespuesta);
        }
        return insumos.buscarPagina(patron, id, almacen, activo, estado == null ? null : estado.name(),
                validar.conOrdenNombre(pageable)).map(mapper::aRespuesta);
    }

    private static Long extraerId(String texto, TipoItem tipo) {
        if (texto == null) return null;
        String prefijos = tipo == TipoItem.PRODUCTO ? "(?:prod|prd)-" : "ins-";
        var coincidencia = java.util.regex.Pattern.compile("^" + prefijos + "(\\d+)$")
                .matcher(texto);
        if (!coincidencia.matches()) return null;
        try { return Long.parseLong(coincidencia.group(1)); }
        catch (NumberFormatException ex) { return null; }
    }

    private record PageRequestSeguro(int size) {
        Pageable conOrdenNombre(Pageable pageable) {
            if (size < 1 || size > 100) throw new IllegalArgumentException("El tamaño debe estar entre 1 y 100");
            return org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), size,
                    org.springframework.data.domain.Sort.by("nombre").ascending());
        }
    }
    @Override
    public ItemResponse obtener(TipoItem tipo, Long id) {
        return tipo == TipoItem.PRODUCTO ? mapper.aRespuesta(producto(id,false)) : mapper.aRespuesta(insumo(id,false));
    }
    @Override @Transactional
    public ItemResponse crearProducto(ProductoRequest r, String responsable) {
        validacion.validar(r); validacion.limites(r.stockMinimo(),r.stockLimite());
        Producto p = new Producto();
        asignar(p,r);
        p.setStock(0);
        productos.saveAndFlush(p);
        inicial(TipoItem.PRODUCTO,p.getIdProducto(),r.stock(),responsable);
        eventos.publishEvent(EventoAuditoria.actual("ITEM_CREADO", "Producto", p.getIdProducto(), p.getNombre()));
        return mapper.aRespuesta(p);
    }
    @Override @Transactional
    public ItemResponse editarProducto(Long id, ProductoRequest r) {
        validacion.validar(r); validacion.limites(r.stockMinimo(),r.stockLimite());
        Producto p = producto(id,true);
        String precioAnterior = p.getPrecio().toPlainString();
        validarEdicion(r.stock(),p.getStock(), r.idAlmacen(),p.getAlmacen().getIdAlmacen());
        if (!p.getPresentacion().getIdPresentacion().equals(r.idPresentacion()))
            throw InventarioException.conflicto("Registre otra presentacion como producto independiente");
        asignar(p,r);
        alertas.sincronizar(TipoItem.PRODUCTO,id);
        eventos.publishEvent(EventoAuditoria.actual("ITEM_ACTUALIZADO", "Producto", id,
                p.getNombre() + "; precio " + precioAnterior + " -> " + p.getPrecio()));
        return mapper.aRespuesta(p);
    }
    @Override @Transactional
    public ItemResponse crearInsumo(InsumoRequest r, String responsable) {
        validacion.validar(r); validacion.limites(r.stockMinimo(),r.stockLimite());
        Insumo i = new Insumo();
        asignar(i,r);
        i.setStock(0);
        insumos.saveAndFlush(i);
        inicial(TipoItem.INSUMO,i.getIdInsumo(),r.stock(),responsable);
        eventos.publishEvent(EventoAuditoria.actual("ITEM_CREADO", "Insumo", i.getIdInsumo(), i.getNombre()));
        return mapper.aRespuesta(i);
    }
    @Override @Transactional
    public ItemResponse editarInsumo(Long id, InsumoRequest r) {
        validacion.validar(r); validacion.limites(r.stockMinimo(),r.stockLimite());
        Insumo i = insumo(id,true);
        validarEdicion(r.stock(),i.getStock(),r.idAlmacen(),i.getAlmacen().getIdAlmacen());
        if (!i.getUnidadMedida().getIdUnidadMedida().equals(r.idUnidadMedida()))
            throw InventarioException.conflicto("La unidad de un insumo existente no puede cambiar");
        asignar(i,r);
        alertas.sincronizar(TipoItem.INSUMO,id);
        eventos.publishEvent(EventoAuditoria.actual("ITEM_ACTUALIZADO", "Insumo", id, i.getNombre()));
        return mapper.aRespuesta(i);
    }
    private void inicial(TipoItem tipo, Long id, Integer stock, String responsable) {
        if (stock != null && stock > 0)
            movimientos.registrar(new MovimientoRequest(TipoMovimiento.INGRESO,tipo,id,stock,"Stock inicial"),responsable);
        else alertas.sincronizar(tipo,id);
    }
    private void validarEdicion(Integer stock, int actual, Long almacen, Long almacenActual) {
        if (stock != null && stock != actual)
            throw InventarioException.conflicto("Modifique el stock registrando un ingreso o salida");
        if (!almacenActual.equals(almacen))
            throw InventarioException.conflicto("El almacen de un item existente no puede cambiar");
    }
    @Override @Transactional
    public void eliminar(TipoItem tipo, Long id) {
        if (tipo == TipoItem.PRODUCTO) producto(id,true).setActivo(false);
        else insumo(id,true).setActivo(false);
        eventos.publishEvent(EventoAuditoria.actual("ITEM_DESACTIVADO", tipo.name(), id, null));
    }

    private Producto producto(Long id, boolean bloqueo) {
        return (bloqueo ? bloqueos.producto(id) : productos.findById(id))
                .orElseThrow(() -> InventarioException.noEncontrado("Producto"));
    }
    private Insumo insumo(Long id, boolean bloqueo) {
        return (bloqueo ? bloqueos.insumo(id) : insumos.findById(id))
                .orElseThrow(() -> InventarioException.noEncontrado("Insumo"));
    }
    private void asignar(Producto p, ProductoRequest r) {
        p.setNombre(r.nombre().trim()); p.setDescripcion(r.descripcion().trim()); p.setPrecio(r.precio());
        p.setStockMinimo(r.stockMinimo()); p.setStockLimite(r.stockLimite());
        p.setCategoria(categorias.findById(r.idCategoria()).orElseThrow(() -> InventarioException.noEncontrado("Categoria")));
        p.setPresentacion(presentaciones.findById(r.idPresentacion()).orElseThrow(() -> InventarioException.noEncontrado("Presentacion")));
        p.setAlmacen(almacenes.findById(r.idAlmacen()).orElseThrow(() -> InventarioException.noEncontrado("Almacen")));
        p.setImagenRuta(r.imagenRuta());
        if (r.activo() != null) p.setActivo(r.activo());
    }
    private void asignar(Insumo i, InsumoRequest r) {
        if (r.activo() != null) i.setActivo(r.activo());
        i.setNombre(r.nombre().trim()); i.setStockMinimo(r.stockMinimo()); i.setStockLimite(r.stockLimite());
        i.setUnidadMedida(unidades.findById(r.idUnidadMedida()).orElseThrow(() -> InventarioException.noEncontrado("Unidad")));
        i.setAlmacen(almacenes.findById(r.idAlmacen()).orElseThrow(() -> InventarioException.noEncontrado("Almacen")));
    }
}
