package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.OrdenProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.dto.SolicitudProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.dto.PaginaResponseDTO;
import com.example.TrujillanosSAC.compras.util.PaginacionComprasUtil;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.CotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.repository.DetalleSolicitudRepository;
import com.example.TrujillanosSAC.compras.repository.SolicitudProveedorRepository;
import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("/api/v1/compras/proveedor")
@PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
public class PortalProveedorController {
    private final UsuarioRepository usuarios;
    private final ProveedorRepository proveedores;
    private final SolicitudProveedorRepository solicitudes;
    private final CotizacionRepository cotizaciones;
    private final DetalleSolicitudRepository detallesSolicitud;
    private final OrdenCompraRepository ordenes;
    private final ComprobanteCompraRepository comprobantes;

    public PortalProveedorController(UsuarioRepository usuarios, ProveedorRepository proveedores,
            SolicitudProveedorRepository solicitudes, CotizacionRepository cotizaciones,
            DetalleSolicitudRepository detallesSolicitud,
            OrdenCompraRepository ordenes,
            ComprobanteCompraRepository comprobantes) {
        this.usuarios = usuarios;
        this.proveedores = proveedores;
        this.solicitudes = solicitudes;
        this.cotizaciones = cotizaciones;
        this.detallesSolicitud = detallesSolicitud;
        this.ordenes = ordenes;
        this.comprobantes = comprobantes;
    }

    @GetMapping("/solicitudes")
    @Transactional(readOnly = true)
    public PaginaResponseDTO<SolicitudProveedorResponseDTO> solicitudes(Principal principal,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int pagina,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) int tamanio,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "") String buscar) {
        Long proveedorId = proveedor(principal).getIdPersona();
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, "idSolicitud", "desc",
                Set.of("idSolicitud"), "idSolicitud");
        var paginaSolicitudes = solicitudes.findDisponiblesPorProveedor(proveedorId,
                PaginacionComprasUtil.normalizarBusqueda(buscar), pageable);
        var idsSolicitud = paginaSolicitudes.getContent().stream().map(s -> s.getIdSolicitud()).toList();
        var detallesPorSolicitud = idsSolicitud.isEmpty() ? Map.<Long, List<com.example.TrujillanosSAC.inventario.model.DetalleSolicitud>>of()
                : detallesSolicitud.findBySolicitudesParaProveedor(idsSolicitud).stream()
                    .collect(Collectors.groupingBy(d -> d.getSolicitudCompra().getIdSolicitud()));
        Page<SolicitudProveedorResponseDTO> resultado = paginaSolicitudes
                .map(s -> new SolicitudProveedorResponseDTO(s.getIdSolicitud(), s.getFechaSolicitud(),
                        s.getEstadoSolicitudCompra().getNombre(), detallesPorSolicitud.getOrDefault(s.getIdSolicitud(), List.of()).stream()
                        .map(d -> new SolicitudProveedorResponseDTO.InsumoSolicitado(d.getInsumo().getIdInsumo(),
                                d.getInsumo().getNombre(), d.getCantidad(), d.getInsumo().getUnidadMedida().getNombre()))
                        .toList()));
        return PaginaResponseDTO.de(resultado);
    }

    @GetMapping("/solicitudes/{id}")
    @Transactional(readOnly = true)
    public SolicitudProveedorResponseDTO solicitud(@org.springframework.web.bind.annotation.PathVariable Long id, Principal principal) {
        Long proveedorId = proveedor(principal).getIdPersona();
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

    @GetMapping("/ordenes")
    @Transactional(readOnly = true)
    public List<OrdenProveedorResponseDTO> ordenes(Principal principal,
            @org.springframework.web.bind.annotation.RequestParam List<Long> cotizacionIds) {
        Long proveedorId = proveedor(principal).getIdPersona();
        if (cotizacionIds.isEmpty()) return List.of();
        var registros = ordenes.findByCotizacionIdCotizacionInAndCotizacionProveedorIdPersona(cotizacionIds, proveedorId);
        return registros.stream().map(o -> {
            var invoice = comprobantes.findFirstByOrdenCompraIdOrdenCompraOrderByFechaEmisionDesc(o.getIdOrdenCompra())
                    .map(c -> new OrdenProveedorResponseDTO.Factura(c.getIdComprobante(), c.getNumeroFactura(),
                            c.getFechaEmision(), c.getMonto(), c.getEstadoComprobanteCompra().getNombre()))
                    .orElse(null);
            var total = o.getDetalles().stream().map(d -> d.getSubtotal())
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            return new OrdenProveedorResponseDTO(o.getIdOrdenCompra(), o.getFechaEmision(),
                    o.getCotizacion().getIdCotizacion(), total,
                    o.getCotizacion().getEstadoCotizacion().getNombre(), invoice);
        }).toList();
    }

    private Proveedor proveedor(Principal principal) {
        if (principal == null) throw new RecursosNoEncontradosException("Usuario no autenticado");
        Usuario usuario = usuarios.findByUsernameConRolesYPersona(principal.getName())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        if (usuario.getPersona() == null) throw new RecursosNoEncontradosException("El usuario no tiene persona asociada");
        return proveedores.findByIdPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException("El usuario no está registrado como proveedor"));
    }
}
