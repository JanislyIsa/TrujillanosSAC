package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.Dto.OrdenCompraAdminDto;
import com.example.TrujillanosSAC.Dto.OrdenCompraDetalleDto;
import com.example.TrujillanosSAC.Model.Cotizacion;
import com.example.TrujillanosSAC.Model.ComprobanteCompra;
import com.example.TrujillanosSAC.Model.DetalleOrdenCompra;
import com.example.TrujillanosSAC.Model.OrdenCompra;
import com.example.TrujillanosSAC.Model.PagoCompra;
import com.example.TrujillanosSAC.Repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.Repository.DetalleOrdenCompraRepository;
import com.example.TrujillanosSAC.Repository.EstadoComprobanteCompraRepository;
import com.example.TrujillanosSAC.Repository.EstadoCotizacionRepository;
import com.example.TrujillanosSAC.Repository.MetodoPagoRepository;
import com.example.TrujillanosSAC.Repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.Repository.PagoCompraRepository;
import com.example.TrujillanosSAC.Service.AdminAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/compras/ordenes")
public class AdminOrdenCompraController {

    private final OrdenCompraRepository ordenCompraRepository;
    private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;
    private final ComprobanteCompraRepository comprobanteCompraRepository;
    private final PagoCompraRepository pagoCompraRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoComprobanteCompraRepository estadoComprobanteCompraRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final AdminAccessService adminAccessService;

    public AdminOrdenCompraController(OrdenCompraRepository ordenCompraRepository,
                                      DetalleOrdenCompraRepository detalleOrdenCompraRepository,
                                      ComprobanteCompraRepository comprobanteCompraRepository,
                                      PagoCompraRepository pagoCompraRepository,
                                      MetodoPagoRepository metodoPagoRepository,
                                      EstadoComprobanteCompraRepository estadoComprobanteCompraRepository,
                                      EstadoCotizacionRepository estadoCotizacionRepository,
                                      AdminAccessService adminAccessService) {
        this.ordenCompraRepository = ordenCompraRepository;
        this.detalleOrdenCompraRepository = detalleOrdenCompraRepository;
        this.comprobanteCompraRepository = comprobanteCompraRepository;
        this.pagoCompraRepository = pagoCompraRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.estadoComprobanteCompraRepository = estadoComprobanteCompraRepository;
        this.estadoCotizacionRepository = estadoCotizacionRepository;
        this.adminAccessService = adminAccessService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String ordenes(Model model, Principal principal) {
        if (principal == null || !adminAccessService.puedeAccederA(principal.getName(), "/admin/compras")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("ordenes", ordenCompraRepository.findAllParaAdmin().stream()
                .map(this::mapearOrden)
                .toList());
        model.addAttribute("currentAdminPath", "/admin/compras/ordenes");
        return "admin/ordenes-compra";
    }

    @PostMapping("/{idOrden}/pagar")
    @Transactional
    public String pagarOrden(@PathVariable Long idOrden,
                             @RequestParam String metodoPago,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal == null || !adminAccessService.puedeAccederA(principal.getName(), "/admin/compras")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (comprobanteCompraRepository.existsByOrdenCompra_IdOrdenCompra(idOrden)) {
            redirectAttributes.addFlashAttribute("mensajeError", "Esta orden ya tiene un pago registrado.");
            return "redirect:/admin/compras/ordenes";
        }

        OrdenCompra orden = ordenCompraRepository.findById(idOrden)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        BigDecimal total = detalleOrdenCompraRepository.findByOrdenParaAdmin(idOrden).stream()
                .map(DetalleOrdenCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ComprobanteCompra comprobante = comprobanteCompraRepository.save(ComprobanteCompra.builder()
                .fechaEmision(LocalDate.now())
                .monto(total)
                .estadoComprobanteCompra(estadoComprobanteCompraRepository.findByNombreIgnoreCase("Pagado")
                        .orElseThrow(() -> new IllegalStateException("No existe el estado Pagado para comprobantes de compra.")))
                .ordenCompra(orden)
                .build());

        pagoCompraRepository.save(PagoCompra.builder()
                .monto(total)
                .fechaPago(LocalDate.now())
                .comprobanteCompra(comprobante)
                .metodoPago(metodoPagoRepository.findByNombreIgnoreCase(metodoPago)
                        .orElseThrow(() -> new IllegalStateException("Metodo de pago no configurado: " + metodoPago)))
                .build());
        orden.getCotizacion().setEstadoCotizacion(estadoCotizacionRepository.findByNombreIgnoreCase("Pagado")
                .orElseThrow(() -> new IllegalStateException("No existe el estado Pagado para cotizaciones.")));

        redirectAttributes.addFlashAttribute("mensajeExito", "Pago registrado correctamente.");
        return "redirect:/admin/compras/ordenes";
    }

    private OrdenCompraAdminDto mapearOrden(OrdenCompra orden) {
        Cotizacion cotizacion = orden.getCotizacion();
        List<OrdenCompraDetalleDto> detalles = detalleOrdenCompraRepository
                .findByOrdenParaAdmin(orden.getIdOrdenCompra())
                .stream()
                .map(this::mapearDetalle)
                .toList();
        BigDecimal total = detalles.stream()
                .map(OrdenCompraDetalleDto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        String proveedor = cotizacion.getProveedor().getPersona().getNombre() + " "
                + cotizacion.getProveedor().getPersona().getApellido();
        boolean pagada = comprobanteCompraRepository.existsByOrdenCompra_IdOrdenCompra(orden.getIdOrdenCompra());

        return new OrdenCompraAdminDto(
                orden.getIdOrdenCompra(),
                "OR_%02d".formatted(orden.getIdOrdenCompra()),
                orden.getFechaEmision(),
                proveedor,
                pagada ? "Pagado" : cotizacion.getEstadoCotizacion().getNombre(),
                total,
                orden.getDescripcion(),
                buscarQrPago(orden.getIdOrdenCompra()),
                pagada,
                detalles
        );
    }

    private String buscarQrPago(Long idOrden) {
        Path carpeta = Path.of("uploads", "proveedores", "qr").toAbsolutePath().normalize();
        for (String extension : List.of("jpg", "jpeg", "png", "webp")) {
            Path archivo = carpeta.resolve("orden-" + idOrden + "." + extension);
            if (Files.exists(archivo)) {
                return "/uploads/proveedores/qr/" + archivo.getFileName();
            }
        }
        return "";
    }

    private OrdenCompraDetalleDto mapearDetalle(DetalleOrdenCompra detalle) {
        return new OrdenCompraDetalleDto(
                detalle.getDetalleCotizacion().getInsumo().getNombre(),
                detalle.getCantidad(),
                detalle.getDetalleCotizacion().getInsumo().getUnidadMedida().getNombre(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
        );
    }
}
