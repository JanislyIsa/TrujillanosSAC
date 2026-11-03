package com.example.TrujillanosSAC.Controller;

import com.example.TrujillanosSAC.inventario.service.MovimientoInventarioService;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.MovimientoRequest;
import com.example.TrujillanosSAC.inventario.model.TipoMovimiento;
import com.example.TrujillanosSAC.inventario.model.TipoItem;

import com.example.TrujillanosSAC.Dto.AdminPedidoDto;
import com.example.TrujillanosSAC.Model.DetallePedido;
import com.example.TrujillanosSAC.Model.EstadoPedido;
import com.example.TrujillanosSAC.Model.Pedido;
import com.example.TrujillanosSAC.Repository.DetallePedidoRepository;
import com.example.TrujillanosSAC.Repository.EstadoPedidoRepository;
import com.example.TrujillanosSAC.Repository.PedidoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    private final MovimientoInventarioService movimientos;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public AdminPedidoController(PedidoRepository pedidoRepository,
                                 DetallePedidoRepository detallePedidoRepository,
                                 EstadoPedidoRepository estadoPedidoRepository,
                                 MovimientoInventarioService movimientos) {
        this.movimientos = movimientos;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listar(Model model, Principal principal) {
        List<Pedido> pedidos = pedidoRepository.findAllParaGestion();
        List<Long> pedidoIds = pedidos.stream()
                .map(Pedido::getIdPedido)
                .toList();
        Map<Long, String> productosPorPedido = pedidoIds.isEmpty()
                ? Map.of()
                : detallePedidoRepository.findByPedidoIds(pedidoIds).stream()
                .collect(Collectors.groupingBy(
                        detalle -> detalle.getPedido().getIdPedido(),
                        Collectors.mapping(this::formatearDetalle, Collectors.joining(", "))
        ));

        model.addAttribute("usuarioNombre", principal != null ? principal.getName() : "Admin");
        model.addAttribute("estados", listarEstadosSinDuplicados());
        model.addAttribute("pedidos", pedidos.stream()
                .map(pedido -> new AdminPedidoDto(
                        pedido.getIdPedido(),
                        pedido.getCliente().getPersona().getNombre() + " " + pedido.getCliente().getPersona().getApellido(),
                        pedido.getCliente().getPersona().getTelefono(),
                        pedido.getFechaEmision(),
                        pedido.getHora(),
                        pedido.getTotal(),
                        pedido.getEstado().getIdEstado(),
                        pedido.getEstado().getNombre(),
                        productosPorPedido.getOrDefault(pedido.getIdPedido(), "Sin productos")
                ))
                .toList());
        return "admin/pedidos";
    }

    @PostMapping("/{id}/estado")
    @Transactional
    public String cambiarEstado(@PathVariable Long id,
                                @RequestParam Long idEstado, Principal principal,
                                RedirectAttributes redirectAttributes) {
        Pedido pedido = pedidoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        EstadoPedido estado = estadoPedidoRepository.findById(idEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
        String estadoActual = normalizarEstado(pedido.getEstado().getNombre());
        String estadoNuevo = normalizarEstado(estado.getNombre());

        if ("ENTREGADO".equals(estadoActual) && "CANCELADO".equals(estadoNuevo)) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede cancelar un pedido entregado.");
            return "redirect:/admin/pedidos";
        }

        if ("CANCELADO".equals(estadoActual) && !"CANCELADO".equals(estadoNuevo)) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se puede reactivar un pedido cancelado.");
            return "redirect:/admin/pedidos";
        }

        if (!"CANCELADO".equals(estadoActual) && "CANCELADO".equals(estadoNuevo)) {
            devolverStock(id,principal.getName());
        }

        pedido.setEstado(estado);
        pedidoRepository.save(pedido);
        redirectAttributes.addFlashAttribute("mensajeExito", "Estado del pedido actualizado.");
        return "redirect:/admin/pedidos";
    }

    private String formatearDetalle(DetallePedido detalle) {
        String presentacion = detalle.getProducto().getPresentacion() != null
                ? detalle.getProducto().getPresentacion().getNombre()
                : "Unidad";

        return detalle.getProducto().getNombre()
                + " (" + presentacion + ") x" + detalle.getCantidad();
    }

    private List<EstadoPedido> listarEstadosSinDuplicados() {
        return estadoPedidoRepository.findAllByOrderByIdEstadoAsc().stream()
                .collect(Collectors.toMap(
                        estado -> normalizarEstado(estado.getNombre()),
                        estado -> estado,
                        (estadoExistente, estadoDuplicado) -> estadoExistente,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
    }

    private void devolverStock(Long pedidoId, String responsable) {
        detallePedidoRepository.findByPedidoIdConProducto(pedidoId).stream()
            .sorted(java.util.Comparator.comparing(d -> d.getProducto().getIdProducto()))
            .forEach(d -> movimientos.registrar(new MovimientoRequest(
                TipoMovimiento.INGRESO,
                TipoItem.PRODUCTO,
                d.getProducto().getIdProducto(),d.getCantidad(),"Cancelacion pedido #"+pedidoId),responsable));
    }

    private String normalizarEstado(String estado) {
        return estado == null ? "" : estado.trim().toUpperCase();
    }
}
