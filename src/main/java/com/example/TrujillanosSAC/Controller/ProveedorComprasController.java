package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.Dto.ProveedorCotizacionDto;
import com.example.TrujillanosSAC.Dto.ProveedorSolicitudDetalleDto;
import com.example.TrujillanosSAC.Dto.ProveedorSolicitudDto;
import com.example.TrujillanosSAC.Model.Cotizacion;
import com.example.TrujillanosSAC.Model.DetalleCotizacion;
import com.example.TrujillanosSAC.inventario.model.DetalleSolicitud;
import com.example.TrujillanosSAC.Model.EstadoCotizacion;
import com.example.TrujillanosSAC.Model.OrdenCompra;
import com.example.TrujillanosSAC.Model.Proveedor;
import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import com.example.TrujillanosSAC.Model.Usuario;
import com.example.TrujillanosSAC.Repository.CotizacionRepository;
import com.example.TrujillanosSAC.Repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.Repository.DetalleCotizacionRepository;
import com.example.TrujillanosSAC.inventario.repository.DetalleSolicitudRepository;
import com.example.TrujillanosSAC.Repository.EstadoCotizacionRepository;
import com.example.TrujillanosSAC.Repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.Repository.ProveedorRepository;
import com.example.TrujillanosSAC.inventario.repository.SolicitudCompraRepository;
import com.example.TrujillanosSAC.Repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/proveedor")
public class ProveedorComprasController {

    private static final List<String> QR_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    private final SolicitudCompraRepository solicitudCompraRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ComprobanteCompraRepository comprobanteCompraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;

    public ProveedorComprasController(SolicitudCompraRepository solicitudCompraRepository,
                                      DetalleSolicitudRepository detalleSolicitudRepository,
                                      CotizacionRepository cotizacionRepository,
                                      DetalleCotizacionRepository detalleCotizacionRepository,
                                      EstadoCotizacionRepository estadoCotizacionRepository,
                                      OrdenCompraRepository ordenCompraRepository,
                                      ComprobanteCompraRepository comprobanteCompraRepository,
                                      UsuarioRepository usuarioRepository,
                                      ProveedorRepository proveedorRepository) {
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.estadoCotizacionRepository = estadoCotizacionRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.comprobanteCompraRepository = comprobanteCompraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping("/solicitudes")
    @Transactional(readOnly = true)
    public String solicitudes(Model model, Principal principal) {
        Proveedor proveedor = proveedorActual(principal);
        model.addAttribute("usuarioNombre", nombreProveedor(proveedor));
        model.addAttribute("activeProviderPath", "/proveedor/solicitudes");
        model.addAttribute("solicitudes", solicitudCompraRepository.findAllParaProveedor().stream()
                .filter(solicitud -> solicitud.getProveedor().getIdPersona().equals(proveedor.getIdPersona()))
                .map(this::mapearSolicitud)
                .toList());
        return "proveedor/solicitudes";
    }

    @GetMapping("/solicitudes/{idSolicitud}/cotizar")
    @Transactional(readOnly = true)
    public String cotizar(@PathVariable Long idSolicitud, Model model, Principal principal) {
        Proveedor proveedor = proveedorActual(principal);
        SolicitudCompra solicitud = solicitudCompraRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        validarProveedorSolicitud(solicitud, proveedor);

        model.addAttribute("usuarioNombre", nombreProveedor(proveedor));
        model.addAttribute("activeProviderPath", "/proveedor/cotizaciones");
        model.addAttribute("solicitud", mapearSolicitud(solicitud));
        return "proveedor/cotizar";
    }

    @PostMapping("/solicitudes/{idSolicitud}/cotizar")
    @Transactional
    public String guardarCotizacion(@PathVariable Long idSolicitud,
                                    @RequestParam(required = false) LocalDate fechaEntrega,
                                    @RequestParam Map<String, String> params,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {
        Proveedor proveedor = proveedorActual(principal);
        SolicitudCompra solicitud = solicitudCompraRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        validarProveedorSolicitud(solicitud, proveedor);

        List<DetalleSolicitud> detallesSolicitud = detalleSolicitudRepository.findBySolicitudParaProveedor(idSolicitud);
        if (detallesSolicitud.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "La solicitud no tiene insumos para cotizar.");
            return "redirect:/proveedor/solicitudes";
        }

        EstadoCotizacion estado = estadoCotizacionRepository.findFirstByOrderByIdEstadoCotizacionAsc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No hay estados de cotizacion configurados."));

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleSolicitud detalle : detallesSolicitud) {
            BigDecimal precio = leerPrecioSeguro(params.get("precio_" + detalle.getIdDetalleSol()));
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ingresa precios unitarios validos para todos los insumos.");
                return "redirect:/proveedor/solicitudes/" + idSolicitud + "/cotizar";
            }
            total = total.add(precio.multiply(detalle.getCantidad()));
        }

        Cotizacion cotizacion = Cotizacion.builder()
                .fechaCot(LocalDate.now())
                .monto(total)
                .estadoCotizacion(estado)
                .proveedor(proveedor)
                .solicitudCompra(solicitud)
                .fechaEntrega(fechaEntrega != null ? fechaEntrega : LocalDate.now().plusDays(7))
                .build();
        cotizacionRepository.save(cotizacion);

        List<DetalleCotizacion> detallesCotizacion = detallesSolicitud.stream()
                .map(detalle -> {
                    BigDecimal precio = leerPrecioSeguro(params.get("precio_" + detalle.getIdDetalleSol()));
                    return DetalleCotizacion.builder()
                            .cantidad(detalle.getCantidad())
                            .precioUnitario(precio)
                            .subtotal(precio.multiply(detalle.getCantidad()))
                            .cotizacion(cotizacion)
                            .insumo(detalle.getInsumo())
                            .build();
                })
                .toList();
        detalleCotizacionRepository.saveAll(detallesCotizacion);

        redirectAttributes.addFlashAttribute("successMessage", "Se envio correctamente");
        return "redirect:/proveedor/cotizaciones";
    }

    @GetMapping("/cotizaciones")
    @Transactional(readOnly = true)
    public String cotizaciones(Model model, Principal principal) {
        Proveedor proveedor = proveedorActual(principal);
        model.addAttribute("usuarioNombre", nombreProveedor(proveedor));
        model.addAttribute("activeProviderPath", "/proveedor/cotizaciones");
        model.addAttribute("cotizaciones", cotizacionRepository.findByProveedorParaPanel(proveedor.getIdPersona()).stream()
                .map(this::mapearCotizacion)
                .toList());
        return "proveedor/cotizaciones";
    }

    @PostMapping("/cotizaciones/{idCotizacion}/comprobante")
    @Transactional(readOnly = true)
    public String subirComprobante(@PathVariable Long idCotizacion,
                                   @RequestParam("qrYape") MultipartFile qrYape,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        try {
            Proveedor proveedor = proveedorActual(principal);
            Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            if (!cotizacion.getProveedor().getIdPersona().equals(proveedor.getIdPersona())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            OrdenCompra orden = ordenCompraRepository.findFirstByCotizacionParaProveedor(idCotizacion)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Esta cotizacion aun no tiene orden de compra."));

            guardarQrPago(orden.getIdOrdenCompra(), qrYape);
            redirectAttributes.addFlashAttribute("successMessage", "El comprobante de pago se envio correctamente");
        } catch (IOException | IllegalArgumentException | ResponseStatusException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo guardar el QR. Usa una imagen JPG, PNG o WEBP.");
        }
        return "redirect:/proveedor/cotizaciones";
    }

    private ProveedorSolicitudDto mapearSolicitud(SolicitudCompra solicitud) {
        List<ProveedorSolicitudDetalleDto> detalles = detalleSolicitudRepository
                .findBySolicitudParaProveedor(solicitud.getIdSolicitud())
                .stream()
                .map(detalle -> new ProveedorSolicitudDetalleDto(
                        detalle.getIdDetalleSol(),
                        detalle.getInsumo().getNombre(),
                        detalle.getCantidad(),
                        detalle.getInsumo().getUnidadMedida().getNombre()
                ))
                .toList();
        return new ProveedorSolicitudDto(
                solicitud.getIdSolicitud(),
                "OR_%02d".formatted(solicitud.getIdSolicitud()),
                solicitud.getFechaSolicitud(),
                ordenCompraRepository.existsByCotizacion_SolicitudCompra_IdSolicitud(solicitud.getIdSolicitud())
                        ? "Cotizado"
                        : solicitud.getEstadoSolicitudCompra().getNombre(),
                detalles
        );
    }

    private ProveedorCotizacionDto mapearCotizacion(Cotizacion cotizacion) {
        Optional<OrdenCompra> orden = ordenCompraRepository.findFirstByCotizacionParaProveedor(cotizacion.getIdCotizacion());
        Long idOrden = orden.map(OrdenCompra::getIdOrdenCompra).orElse(null);
        boolean pagada = idOrden != null && comprobanteCompraRepository.existsByOrdenCompra_IdOrdenCompra(idOrden);
        return new ProveedorCotizacionDto(
                cotizacion.getIdCotizacion(),
                "CO_%02d".formatted(cotizacion.getIdCotizacion()),
                idOrden != null ? "OR_%02d".formatted(idOrden) : "",
                cotizacion.getFechaCot(),
                cotizacion.getFechaEntrega(),
                pagada ? "Pagado" : cotizacion.getEstadoCotizacion().getNombre(),
                cotizacion.getMonto(),
                idOrden,
                idOrden != null ? buscarQrPago(idOrden) : ""
        );
    }

    private Proveedor proveedorActual(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        return proveedorRepository.findByIdPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario actual no esta registrado como proveedor."));
    }

    private void validarProveedorSolicitud(SolicitudCompra solicitud, Proveedor proveedor) {
        if (!solicitud.getProveedor().getIdPersona().equals(proveedor.getIdPersona())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private String nombreProveedor(Proveedor proveedor) {
        return proveedor.getPersona().getNombre() + " " + proveedor.getPersona().getApellido();
    }

    private BigDecimal leerPrecio(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(rawValue.replace(",", ".").trim());
    }

    private BigDecimal leerPrecioSeguro(String rawValue) {
        try {
            return leerPrecio(rawValue);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private void guardarQrPago(Long idOrden, MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes subir una imagen del QR de Yape.");
        }

        String original = archivo.getOriginalFilename() == null ? "" : archivo.getOriginalFilename();
        String extension = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            extension = original.substring(dot + 1).toLowerCase();
        }
        if (!QR_EXTENSIONS.contains(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El QR debe ser una imagen JPG, PNG o WEBP.");
        }

        Path carpeta = Path.of("uploads", "proveedores", "qr").toAbsolutePath().normalize();
        Files.createDirectories(carpeta);
        for (String ext : QR_EXTENSIONS) {
            Files.deleteIfExists(carpeta.resolve("orden-" + idOrden + "." + ext));
        }
        Path destino = carpeta.resolve("orden-" + idOrden + "." + extension).normalize();
        if (!destino.startsWith(carpeta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        archivo.transferTo(destino);
    }

    private String buscarQrPago(Long idOrden) {
        Path carpeta = Path.of("uploads", "proveedores", "qr").toAbsolutePath().normalize();
        for (String extension : QR_EXTENSIONS) {
            Path archivo = carpeta.resolve("orden-" + idOrden + "." + extension);
            if (Files.exists(archivo)) {
                return "/uploads/proveedores/qr/" + archivo.getFileName();
            }
        }
        return "";
    }
}
