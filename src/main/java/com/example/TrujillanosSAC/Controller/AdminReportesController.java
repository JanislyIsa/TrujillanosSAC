package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.inventario.dto.ReporteMovimientoInventarioDto;
import com.example.TrujillanosSAC.inventario.model.DetalleMovInsumo;
import com.example.TrujillanosSAC.inventario.model.DetalleMovProducto;
import com.example.TrujillanosSAC.inventario.model.Movimiento;
import com.example.TrujillanosSAC.inventario.repository.DetalleMovInsumoRepository;
import com.example.TrujillanosSAC.inventario.repository.DetalleMovProductoRepository;
import com.example.TrujillanosSAC.Repository.DetalleOrdenCompraRepository;
import com.example.TrujillanosSAC.Repository.PedidoRepository;
import com.example.TrujillanosSAC.Service.AdminAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReportesController {

    private final DetalleMovProductoRepository detalleMovProductoRepository;
    private final DetalleMovInsumoRepository detalleMovInsumoRepository;
    private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;
    private final PedidoRepository pedidoRepository;
    private final AdminAccessService adminAccessService;

    public AdminReportesController(DetalleMovProductoRepository detalleMovProductoRepository,
                                   DetalleMovInsumoRepository detalleMovInsumoRepository,
                                   DetalleOrdenCompraRepository detalleOrdenCompraRepository,
                                   PedidoRepository pedidoRepository,
                                   AdminAccessService adminAccessService) {
        this.detalleMovProductoRepository = detalleMovProductoRepository;
        this.detalleMovInsumoRepository = detalleMovInsumoRepository;
        this.detalleOrdenCompraRepository = detalleOrdenCompraRepository;
        this.pedidoRepository = pedidoRepository;
        this.adminAccessService = adminAccessService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String reportes(Model model, Principal principal) {
        if (principal == null || !adminAccessService.puedeAccederA(principal.getName(), "/admin/reportes")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        model.addAttribute("usuarioNombre", principal.getName());
        model.addAttribute("moduloActual", "reportes");
        model.addAttribute("reporteMovimientos", reporteMovimientosUltimos30Dias());
        model.addAttribute("reporteCompras", detalleOrdenCompraRepository.reporteComprasPorProveedor());
        model.addAttribute("reporteVentas", pedidoRepository.reporteVentasPorCliente());
        return "admin/reportes";
    }

    private List<ReporteMovimientoInventarioDto> reporteMovimientosUltimos30Dias() {
        LocalDate desde = LocalDate.now().minusDays(30);
        Map<Long, MovimientoAcumulado> acumulados = new LinkedHashMap<>();

        for (DetalleMovProducto detalle : detalleMovProductoRepository.findAllParaInventario()) {
            Movimiento movimiento = detalle.getMovimiento();
            if (movimiento.getFecha().isBefore(desde)) {
                continue;
            }
            acumulados.computeIfAbsent(movimiento.getIdMovimiento(), id -> MovimientoAcumulado.desde(movimiento))
                    .sumarProducto(detalle.getCantidad());
        }

        for (DetalleMovInsumo detalle : detalleMovInsumoRepository.findAllParaInventario()) {
            Movimiento movimiento = detalle.getMovimiento();
            if (movimiento.getFecha().isBefore(desde)) {
                continue;
            }
            acumulados.computeIfAbsent(movimiento.getIdMovimiento(), id -> MovimientoAcumulado.desde(movimiento))
                    .sumarInsumo(detalle.getCantidad());
        }

        return acumulados.values().stream()
                .map(MovimientoAcumulado::toDto)
                .sorted(Comparator.comparing(ReporteMovimientoInventarioDto::fecha).reversed()
                        .thenComparing(ReporteMovimientoInventarioDto::hora, Comparator.reverseOrder()))
                .toList();
    }

    private static String codigoMovimiento(Long idMovimiento) {
        return "MOV-%03d".formatted(idMovimiento);
    }

    private static class MovimientoAcumulado {
        private final Movimiento movimiento;
        private int totalProductos;
        private int totalInsumos;
        private int totalItems;

        private MovimientoAcumulado(Movimiento movimiento) {
            this.movimiento = movimiento;
        }

        private static MovimientoAcumulado desde(Movimiento movimiento) {
            return new MovimientoAcumulado(movimiento);
        }

        private void sumarProducto(Integer cantidad) {
            totalProductos += cantidad == null ? 0 : cantidad;
            totalItems++;
        }

        private void sumarInsumo(Integer cantidad) {
            totalInsumos += cantidad == null ? 0 : cantidad;
            totalItems++;
        }

        private ReporteMovimientoInventarioDto toDto() {
            return new ReporteMovimientoInventarioDto(
                    codigoMovimiento(movimiento.getIdMovimiento()),
                    movimiento.getTipoMovimiento(),
                    movimiento.getFecha(),
                    movimiento.getHora(),
                    movimiento.getAlmacen().getNombre(),
                    totalProductos,
                    totalInsumos,
                    totalItems
            );
        }
    }
}
