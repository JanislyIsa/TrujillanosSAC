package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.inventario.service.SolicitudInventarioService;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.SolicitudRequest;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.SolicitudLineaRequest;

import com.example.TrujillanosSAC.Dto.CarritoCompraItemDto;
import com.example.TrujillanosSAC.Model.Cotizacion;
import com.example.TrujillanosSAC.Model.DetalleCotizacion;
import com.example.TrujillanosSAC.Model.DetalleOrdenCompra;
import com.example.TrujillanosSAC.inventario.model.DetalleSolicitud;
import com.example.TrujillanosSAC.Model.Empleado;
import com.example.TrujillanosSAC.inventario.model.Insumo;
import com.example.TrujillanosSAC.Model.OrdenCompra;
import com.example.TrujillanosSAC.Model.Proveedor;
import com.example.TrujillanosSAC.inventario.model.SolicitudCompra;
import com.example.TrujillanosSAC.Repository.CotizacionRepository;
import com.example.TrujillanosSAC.Repository.DetalleCotizacionRepository;
import com.example.TrujillanosSAC.Repository.DetalleOrdenCompraRepository;
import com.example.TrujillanosSAC.inventario.repository.DetalleSolicitudRepository;
import com.example.TrujillanosSAC.Repository.EmpleadoRepository;
import com.example.TrujillanosSAC.Repository.EstadoCotizacionRepository;
import com.example.TrujillanosSAC.inventario.repository.EstadoSolicitudCompraRepository;
import com.example.TrujillanosSAC.inventario.repository.InsumoRepository;
import com.example.TrujillanosSAC.Repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.Repository.ProveedorRepository;
import com.example.TrujillanosSAC.inventario.repository.SolicitudCompraRepository;
import com.example.TrujillanosSAC.Repository.UsuarioRepository;
import com.example.TrujillanosSAC.Service.AdminAccessService;
import jakarta.servlet.http.HttpSession;
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
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/compras")
public class AdminComprasController {

    private static final String CART_SESSION_KEY = "carritoCompraInsumos";

    private final InsumoRepository insumoRepository;
    private final SolicitudInventarioService solicitudesInventario;
    private final SolicitudCompraRepository solicitudCompraRepository;
    private final DetalleSolicitudRepository detalleSolicitudRepository;
    private final EstadoSolicitudCompraRepository estadoSolicitudCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final EstadoCotizacionRepository estadoCotizacionRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;
    private final AdminAccessService adminAccessService;

    public AdminComprasController(InsumoRepository insumoRepository,
                                  SolicitudCompraRepository solicitudCompraRepository,
                                  SolicitudInventarioService solicitudesInventario,
                                  DetalleSolicitudRepository detalleSolicitudRepository,
                                  EstadoSolicitudCompraRepository estadoSolicitudCompraRepository,
                                  ProveedorRepository proveedorRepository,
                                  UsuarioRepository usuarioRepository,
                                  EmpleadoRepository empleadoRepository,
                                  CotizacionRepository cotizacionRepository,
                                  DetalleCotizacionRepository detalleCotizacionRepository,
                                  EstadoCotizacionRepository estadoCotizacionRepository,
                                  OrdenCompraRepository ordenCompraRepository,
                                  DetalleOrdenCompraRepository detalleOrdenCompraRepository,
                                  AdminAccessService adminAccessService) {
        this.insumoRepository = insumoRepository;
        this.solicitudCompraRepository = solicitudCompraRepository;
        this.solicitudesInventario = solicitudesInventario;
        this.detalleSolicitudRepository = detalleSolicitudRepository;
        this.estadoSolicitudCompraRepository = estadoSolicitudCompraRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.estadoCotizacionRepository = estadoCotizacionRepository;
        this.ordenCompraRepository = ordenCompraRepository;
        this.detalleOrdenCompraRepository = detalleOrdenCompraRepository;
        this.adminAccessService = adminAccessService;
    }

    @GetMapping("/solicitudes")
    @Transactional(readOnly = true)
    public String solicitudes(Model model, Principal principal) {
        validarAccesoCompras(principal);
        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("solicitudes", solicitudCompraRepository.findAllParaProveedor().stream()
                .map(solicitud -> new SolicitudVista(
                        solicitud.getIdSolicitud(),
                        "SC_%02d".formatted(solicitud.getIdSolicitud()),
                        solicitud.getFechaSolicitud(),
                        solicitud.getProveedor().getPersona().getNombre() + " " + solicitud.getProveedor().getPersona().getApellido(),
                        ordenCompraRepository.existsByCotizacion_SolicitudCompra_IdSolicitud(solicitud.getIdSolicitud())
                                ? "Cotizado"
                                : solicitud.getEstadoSolicitudCompra().getNombre(),
                        detalleSolicitudRepository.findBySolicitudParaProveedor(solicitud.getIdSolicitud())
                ))
                .toList());
        model.addAttribute("currentAdminPath", "/admin/compras/solicitudes");
        return "admin/solicitudes-compra";
    }

    @GetMapping("/carrito")
    public String carrito(Model model, Principal principal, HttpSession session) {
        validarAccesoCompras(principal);
        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("items", obtenerCarrito(session));
        model.addAttribute("proveedores", proveedorRepository.findAllConPersona());
        model.addAttribute("currentAdminPath", "/admin/compras/carrito");
        return "admin/compras-carrito";
    }

    @PostMapping("/carrito/agregar")
    @Transactional(readOnly = true)
    public String agregarCarrito(@RequestParam Long idInsumo,
                                 @RequestParam BigDecimal cantidad,
                                 Principal principal,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        validarAccesoCompras(principal);
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("mensajeError", "La cantidad solicitada debe ser mayor a cero.");
            return "redirect:/admin/inventario";
        }

        Insumo insumo = insumoRepository.findById(idInsumo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<CarritoCompraItemDto> carrito = new ArrayList<>(obtenerCarrito(session));
        carrito.removeIf(item -> item.idInsumo().equals(idInsumo));
        carrito.add(new CarritoCompraItemDto(
                insumo.getIdInsumo(),
                insumo.getNombre(),
                insumo.getUnidadMedida().getNombre(),
                cantidad,
                insumo.getStock(),
                insumo.getStockMinimo()
        ));
        session.setAttribute(CART_SESSION_KEY, carrito);
        redirectAttributes.addFlashAttribute("mensajeExito", "Insumo agregado al carrito de compras.");
        return "redirect:/admin/compras/carrito";
    }

    @PostMapping("/carrito/{idInsumo}/quitar")
    public String quitarCarrito(@PathVariable Long idInsumo, Principal principal, HttpSession session) {
        validarAccesoCompras(principal);
        List<CarritoCompraItemDto> carrito = new ArrayList<>(obtenerCarrito(session));
        carrito.removeIf(item -> item.idInsumo().equals(idInsumo));
        session.setAttribute(CART_SESSION_KEY, carrito);
        return "redirect:/admin/compras/carrito";
    }

    @PostMapping("/carrito/registrar")
    public String registrarSolicitud(Principal principal,
                                     @RequestParam Long idProveedor,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        validarAccesoCompras(principal);
        List<CarritoCompraItemDto> carrito = obtenerCarrito(session);
        if (carrito.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Agrega insumos al carrito antes de registrar la solicitud.");
            return "redirect:/admin/compras/carrito";
        }

        try {
            solicitudesInventario.crear(new SolicitudRequest(
                    idProveedor,carrito.stream().map(item -> new SolicitudLineaRequest(
                            item.idInsumo(),item.cantidad())).toList()),principal.getName());
            session.removeAttribute(CART_SESSION_KEY);
            redirectAttributes.addFlashAttribute("mensajeExito", "Solicitud de compra registrada correctamente.");
            return "redirect:/admin/compras/solicitudes";
        } catch (RuntimeException exception) {
            redirectAttributes.addFlashAttribute("mensajeError", exception.getMessage());
            return "redirect:/admin/compras/carrito";
        }
    }

    @GetMapping("/cotizaciones")
    @Transactional(readOnly = true)
    public String cotizaciones(Model model, Principal principal) {
        validarAccesoCompras(principal);
        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("cotizaciones", cotizacionRepository.findAllParaAdmin().stream()
                .map(cotizacion -> new CotizacionVista(
                        cotizacion.getIdCotizacion(),
                        "CO_%02d".formatted(cotizacion.getIdCotizacion()),
                        "SC_%02d".formatted(cotizacion.getSolicitudCompra().getIdSolicitud()),
                        cotizacion.getProveedor().getPersona().getNombre() + " " + cotizacion.getProveedor().getPersona().getApellido(),
                        cotizacion.getFechaCot(),
                        cotizacion.getFechaEntrega(),
                        cotizacion.getEstadoCotizacion().getNombre(),
                        cotizacion.getMonto(),
                        ordenCompraRepository.findFirstByCotizacionParaProveedor(cotizacion.getIdCotizacion()).isPresent(),
                        detalleCotizacionRepository.findByCotizacionParaProveedor(cotizacion.getIdCotizacion())
                ))
                .toList());
        model.addAttribute("currentAdminPath", "/admin/compras/cotizaciones");
        return "admin/cotizaciones-compra";
    }

    @PostMapping("/cotizaciones/{idCotizacion}/generar-orden")
    @Transactional
    public String generarOrden(@PathVariable Long idCotizacion,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        validarAccesoCompras(principal);
        if (ordenCompraRepository.findFirstByCotizacionParaProveedor(idCotizacion).isPresent()) {
            redirectAttributes.addFlashAttribute("mensajeError", "Esta cotizacion ya tiene una orden de compra.");
            return "redirect:/admin/compras/cotizaciones";
        }

        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if ("Rechazada".equalsIgnoreCase(cotizacion.getEstadoCotizacion().getNombre())) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede generar una orden desde una cotizacion rechazada.");
            return "redirect:/admin/compras/cotizaciones";
        }

        cotizacion.setEstadoCotizacion(estadoCotizacionRepository.findByNombreIgnoreCase("Aceptada")
                .orElseThrow(() -> new IllegalStateException("No existe el estado Aceptada para cotizaciones.")));
        cotizacionRepository.save(cotizacion);

        OrdenCompra orden = ordenCompraRepository.save(OrdenCompra.builder()
                .fechaEmision(LocalDate.now())
                .descripcion("Orden generada desde cotizacion CO_%02d".formatted(cotizacion.getIdCotizacion()))
                .cotizacion(cotizacion)
                .build());

        List<DetalleCotizacion> detalles = detalleCotizacionRepository.findByCotizacionParaProveedor(idCotizacion);
        for (DetalleCotizacion detalle : detalles) {
            detalleOrdenCompraRepository.save(DetalleOrdenCompra.builder()
                    .ordenCompra(orden)
                    .detalleCotizacion(detalle)
                    .cantidad(detalle.getCantidad())
                    .precioUnitario(detalle.getPrecioUnitario())
                    .subtotal(detalle.getSubtotal())
                    .build());
        }
        SolicitudCompra solicitud = cotizacion.getSolicitudCompra();
        solicitud.setEstadoSolicitudCompra(estadoSolicitudCompraRepository.findByNombreIgnoreCase("Cotizado")
                .orElseThrow(() -> new IllegalStateException("No existe el estado Cotizado para solicitudes.")));
        solicitudCompraRepository.save(solicitud);

        redirectAttributes.addFlashAttribute("mensajeExito", "Orden de compra generada correctamente.");
        return "redirect:/admin/compras/ordenes";
    }

    @PostMapping("/cotizaciones/{idCotizacion}/rechazar")
    @Transactional
    public String rechazarCotizacion(@PathVariable Long idCotizacion,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        validarAccesoCompras(principal);
        if (ordenCompraRepository.findFirstByCotizacionParaProveedor(idCotizacion).isPresent()) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede rechazar una cotizacion que ya genero orden.");
            return "redirect:/admin/compras/cotizaciones";
        }

        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        cotizacion.setEstadoCotizacion(estadoCotizacionRepository.findByNombreIgnoreCase("Rechazada")
                .orElseThrow(() -> new IllegalStateException("No existe el estado Rechazada para cotizaciones.")));
        cotizacionRepository.save(cotizacion);

        redirectAttributes.addFlashAttribute("mensajeExito", "Cotizacion rechazada correctamente.");
        return "redirect:/admin/compras/cotizaciones";
    }

    @SuppressWarnings("unchecked")
    private List<CarritoCompraItemDto> obtenerCarrito(HttpSession session) {
        Object carrito = session.getAttribute(CART_SESSION_KEY);
        if (carrito instanceof List<?>) {
            return (List<CarritoCompraItemDto>) carrito;
        }
        return List.of();
    }

    private void validarAccesoCompras(Principal principal) {
        if (principal == null || !adminAccessService.puedeAccederA(principal.getName(), "/admin/compras")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private Empleado obtenerEmpleadoActual(String username) {
        return usuarioRepository.findByUsernameConRolesYPersona(username)
                .flatMap(usuario -> empleadoRepository.findByIdPersonaConCargo(usuario.getPersona().getIdPersona()))
                .orElseThrow(() -> new IllegalStateException("No se encontro empleado para registrar la solicitud"));
    }

    public record SolicitudVista(Long idSolicitud, String codigo, LocalDate fecha, String proveedor, String estado,
                                 List<DetalleSolicitud> detalles) {
    }

    public record CotizacionVista(Long idCotizacion, String codigo, String solicitud, String proveedor,
                                  LocalDate fechaCotizacion, LocalDate fechaEntrega, String estado,
                                  BigDecimal monto, boolean tieneOrden, List<DetalleCotizacion> detalles) {
    }
}
