package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.OrdenProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.dto.SolicitudProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.repository.CotizacionRepository;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
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

@RestController
@RequestMapping("/api/v1/compras/proveedor")
@PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
public class PortalProveedorController {
    private final UsuarioRepository usuarios;
    private final ProveedorRepository proveedores;
    private final SolicitudProveedorRepository solicitudes;
    private final DetalleSolicitudRepository detallesSolicitud;
    private final CotizacionRepository cotizaciones;
    private final OrdenCompraRepository ordenes;
    private final ComprobanteCompraRepository comprobantes;

    public PortalProveedorController(UsuarioRepository usuarios, ProveedorRepository proveedores,
            SolicitudProveedorRepository solicitudes, DetalleSolicitudRepository detallesSolicitud,
            CotizacionRepository cotizaciones, OrdenCompraRepository ordenes,
            ComprobanteCompraRepository comprobantes) {
        this.usuarios = usuarios;
        this.proveedores = proveedores;
        this.solicitudes = solicitudes;
        this.detallesSolicitud = detallesSolicitud;
        this.cotizaciones = cotizaciones;
        this.ordenes = ordenes;
        this.comprobantes = comprobantes;
    }

    @GetMapping("/solicitudes")
    @Transactional(readOnly = true)
    public List<SolicitudProveedorResponseDTO> solicitudes(Principal principal) {
        Long proveedorId = proveedor(principal).getIdPersona();
        return solicitudes.findAllByProveedor(proveedorId).stream()
                .filter(s -> !cotizaciones.existsBySolicitudCompraIdSolicitudAndProveedorIdPersona(s.getIdSolicitud(), proveedorId))
                .map(s -> new SolicitudProveedorResponseDTO(s.getIdSolicitud(), s.getFechaSolicitud(),
                        s.getEstadoSolicitudCompra().getNombre(), detallesSolicitud.findBySolicitudParaProveedor(s.getIdSolicitud()).stream()
                        .map(d -> new SolicitudProveedorResponseDTO.InsumoSolicitado(d.getInsumo().getIdInsumo(),
                                d.getInsumo().getNombre(), d.getCantidad(), d.getInsumo().getUnidadMedida().getNombre()))
                        .toList()))
                .toList();
    }

    @GetMapping("/ordenes")
    @Transactional(readOnly = true)
    public List<OrdenProveedorResponseDTO> ordenes(Principal principal) {
        Long proveedorId = proveedor(principal).getIdPersona();
        return ordenes.findAllByProveedor(proveedorId).stream().map(o -> {
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
