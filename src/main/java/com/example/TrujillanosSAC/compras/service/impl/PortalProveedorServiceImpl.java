package com.example.TrujillanosSAC.compras.service.impl;

import com.example.TrujillanosSAC.compras.dto.CotizacionProveedorRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.dto.SolicitudProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.CotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.repository.SolicitudProveedorRepository;
import com.example.TrujillanosSAC.compras.service.service.CotizacionService;
import com.example.TrujillanosSAC.compras.service.service.PortalProveedorService;
import com.example.TrujillanosSAC.compras.util.PaginacionComprasUtil;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.model.DetalleSolicitud;
import com.example.TrujillanosSAC.inventario.repository.DetalleSolicitudRepository;
import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortalProveedorServiceImpl implements PortalProveedorService {
    private final UsuarioRepository usuarios;
    private final ProveedorRepository proveedores;
    private final SolicitudProveedorRepository solicitudes;
    private final CotizacionRepository cotizaciones;
    private final DetalleSolicitudRepository detallesSolicitud;
    private final OrdenCompraRepository ordenes;
    private final ComprobanteCompraRepository comprobantes;
    private final CotizacionService cotizacionService;

    public PortalProveedorServiceImpl(UsuarioRepository usuarios, ProveedorRepository proveedores,
            SolicitudProveedorRepository solicitudes, CotizacionRepository cotizaciones,
            DetalleSolicitudRepository detallesSolicitud, OrdenCompraRepository ordenes,
            ComprobanteCompraRepository comprobantes, CotizacionService cotizacionService) {
        this.usuarios = usuarios;
        this.proveedores = proveedores;
        this.solicitudes = solicitudes;
        this.cotizaciones = cotizaciones;
        this.detallesSolicitud = detallesSolicitud;
        this.ordenes = ordenes;
        this.comprobantes = comprobantes;
        this.cotizacionService = cotizacionService;
    }

    @Override @Transactional(readOnly = true)
    public Long proveedorId(String username) { return proveedor(username).getIdPersona(); }

    @Override @Transactional
    public CotizacionResponseDTO enviarCotizacion(CotizacionProveedorRequestDTO request, String username) {
        return cotizacionService.create(new CotizacionRequestDTO(request.solicitudId(), proveedorId(username),
                request.fechaEntrega(), request.detalles(), request.descripcion()));
    }

    @Override @Transactional(readOnly = true)
    public Page<SolicitudProveedorResponseDTO> solicitudes(String username, String buscar, Pageable pageable) {
        Long proveedorId = proveedor(username).getIdPersona();
        var page = solicitudes.findDisponiblesPorProveedor(proveedorId,
                PaginacionComprasUtil.normalizarBusqueda(buscar), pageable);
        var ids = page.getContent().stream().map(s -> s.getIdSolicitud()).toList();
        Map<Long, List<DetalleSolicitud>> porSolicitud = ids.isEmpty() ? Map.of()
                : detallesSolicitud.findBySolicitudesParaProveedor(ids).stream()
                    .collect(Collectors.groupingBy(d -> d.getSolicitudCompra().getIdSolicitud()));
        return page.map(s -> new SolicitudProveedorResponseDTO(s.getIdSolicitud(), s.getFechaSolicitud(),
                s.getEstadoSolicitudCompra().getNombre(), porSolicitud.getOrDefault(s.getIdSolicitud(), List.of()).stream()
                    .map(d -> new SolicitudProveedorResponseDTO.InsumoSolicitado(d.getInsumo().getIdInsumo(),
                            d.getInsumo().getNombre(), d.getCantidad(), d.getInsumo().getUnidadMedida().getNombre()))
                    .toList()));
    }

    @Override @Transactional(readOnly = true)
    public SolicitudProveedorResponseDTO solicitud(Long id, String username) {
        Long proveedorId = proveedor(username).getIdPersona();
        var solicitud = solicitudes.findById(id).orElseThrow(() ->
                new RecursosNoEncontradosException("Solicitud de compra no encontrada"));
        if (solicitud.getProveedor() == null || !solicitud.getProveedor().getIdPersona().equals(proveedorId))
            throw new ReglaNegocioException("La solicitud no pertenece al proveedor autenticado");
        if (cotizaciones.existsBySolicitudCompraIdSolicitudAndProveedorIdPersona(id, proveedorId))
            throw new ReglaNegocioException("Ya enviaste una cotización para esta solicitud");
        return new SolicitudProveedorResponseDTO(id, solicitud.getFechaSolicitud(),
                solicitud.getEstadoSolicitudCompra().getNombre(), detallesSolicitud.findBySolicitudParaProveedor(id).stream()
                    .map(d -> new SolicitudProveedorResponseDTO.InsumoSolicitado(d.getInsumo().getIdInsumo(),
                            d.getInsumo().getNombre(), d.getCantidad(), d.getInsumo().getUnidadMedida().getNombre()))
                    .toList());
    }

    @Override @Transactional(readOnly = true)
    public List<OrdenProveedorResponseDTO> ordenes(List<Long> cotizacionIds, String username) {
        Long proveedorId = proveedor(username).getIdPersona();
        if (cotizacionIds == null || cotizacionIds.isEmpty()) return List.of();
        return ordenes.findByCotizacionIdCotizacionInAndCotizacionProveedorIdPersona(cotizacionIds, proveedorId)
                .stream().map(o -> {
                    var factura = comprobantes.findFirstByOrdenCompraIdOrdenCompraOrderByFechaEmisionDesc(o.getIdOrdenCompra())
                            .map(c -> new OrdenProveedorResponseDTO.Factura(c.getIdComprobante(), c.getNumeroFactura(),
                                    c.getFechaEmision(), c.getMonto(), c.getEstadoComprobanteCompra().getNombre()))
                            .orElse(null);
                    BigDecimal total = o.getDetalles().stream().map(d -> d.getSubtotal())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new OrdenProveedorResponseDTO(o.getIdOrdenCompra(), o.getFechaEmision(),
                            o.getCotizacion().getIdCotizacion(), total,
                            o.getCotizacion().getEstadoCotizacion().getNombre(), factura);
                }).toList();
    }

    private Proveedor proveedor(String username) {
        Usuario usuario = usuarios.findByUsernameConRolesYPersona(username)
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        if (usuario.getPersona() == null)
            throw new RecursosNoEncontradosException("El usuario no tiene persona asociada");
        return proveedores.findByIdPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException("El usuario no está registrado como proveedor"));
    }
}
