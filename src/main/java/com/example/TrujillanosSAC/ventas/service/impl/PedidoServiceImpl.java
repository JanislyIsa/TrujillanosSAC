package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.repository.ClienteRepository;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.MovimientoRequest;
import com.example.TrujillanosSAC.inventario.model.Producto;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import com.example.TrujillanosSAC.inventario.model.TipoMovimiento;
import com.example.TrujillanosSAC.inventario.repository.ProductoRepository;
import com.example.TrujillanosSAC.inventario.service.MovimientoInventarioService;
import com.example.TrujillanosSAC.ventas.dto.request.DetallePedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.request.PedidoRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PedidoResponseDTO;
import com.example.TrujillanosSAC.ventas.mapper.VentaMapper;
import com.example.TrujillanosSAC.ventas.model.DetallePedido;
import com.example.TrujillanosSAC.ventas.model.Pedido;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.repository.PedidoRepository;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.EstadoPedidoService;
import com.example.TrujillanosSAC.ventas.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service("ventasPedidoService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidos;
    private final ProductoRepository productos;
    private final ClienteRepository clientes;
    private final EstadoPedidoService estados;
    private final MovimientoInventarioService movimientos;
    private final AccesoVentas acceso;

    @Override
    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO request, ActorVenta actor) {
        Cliente cliente = clientes.findById(acceso.idPersona(actor))
                .orElseThrow(() -> new ReglaNegocioException("El usuario no está registrado como cliente"));

        // Si un producto viene dos veces se suman sus cantidades. El TreeMap los ordena por id:
        // así dos pedidos simultáneos bloquean los productos en el mismo orden y no se traban entre sí.
        Map<Long, Integer> cantidades = new TreeMap<>();
        for (DetallePedidoRequestDTO item : request.detalles()) {
            cantidades.merge(item.productoId(), item.cantidad(), Math::addExact);
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado(estados.porCodigo(EstadoPedidoCodigo.PENDIENTE));
        pedido.setFechaRegistro(LocalDateTime.now());
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> item : cantidades.entrySet()) {
            Producto producto = productos.findByIdForUpdate(item.getKey()).orElseThrow(() ->
                    new RecursosNoEncontradosException("Producto no encontrado: " + item.getKey()));
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new ReglaNegocioException("Producto inactivo: " + producto.getNombre());
            }
            if (producto.getStock() < item.getValue()) {
                throw new ReglaNegocioException("Stock insuficiente: " + producto.getNombre());
            }
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getValue());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.getValue())));
            pedido.agregarDetalle(detalle);
            total = total.add(detalle.getSubtotal());
        }
        pedido.setTotal(total);
        pedidos.save(pedido);

        // El stock se reserva al crear el pedido. Se descuenta con un movimiento de inventario para que
        // quede en el kárdex y se actualicen las alertas de stock.
        registrarMovimientos(pedido, TipoMovimiento.SALIDA, "Venta pedido #", actor.username());
        return VentaMapper.pedido(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listar(ActorVenta actor) {
        List<Pedido> lista = actor.empleado()
                ? pedidos.listarCompletos()
                : pedidos.listarCompletosPorCliente(acceso.idPersona(actor));
        return lista.stream().map(VentaMapper::pedido).toList();
    }

    @Override
    public PedidoResponseDTO obtener(Long id, ActorVenta actor) {
        Pedido pedido = pedidos.buscarCompleto(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Pedido no encontrado"));
        acceso.verificarPedido(pedido, actor);
        return VentaMapper.pedido(pedido);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cancelar(Long id, ActorVenta actor) {
        Pedido pedido = bloquear(id);
        acceso.verificarPedido(pedido, actor);
        if (!pedido.estaEn(EstadoPedidoCodigo.PENDIENTE)) {
            // Un pedido pagado necesitaría una devolución del dinero, que todavía no está implementada.
            throw new ReglaNegocioException("Solo se puede cancelar un pedido pendiente de pago");
        }
        anular(pedido, actor.username());
        return VentaMapper.pedido(pedido);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstado(Long id, EstadoPedidoCodigo destino) {
        Pedido pedido = bloquear(id);
        EstadoPedidoCodigo actual = EstadoPedidoCodigo.valueOf(pedido.getEstado().getCodigo());
        if (!actual.puedePasarManualmenteA(destino)) {
            throw new ReglaNegocioException("No se puede pasar el pedido de " + actual + " a " + destino);
        }
        pedido.setEstado(estados.porCodigo(destino));
        return VentaMapper.pedido(pedido);
    }

    @Override
    @Transactional
    public int cancelarVencidos(LocalDateTime limite) {
        int cancelados = 0;
        for (Long id : pedidos.idsPorEstadoRegistradosAntesDe(EstadoPedidoCodigo.PENDIENTE.name(), limite)) {
            Pedido pedido = bloquear(id);
            // Se vuelve a revisar con el pedido bloqueado: pudo pasar a "procesando pago" mientras tanto.
            if (pedido.estaEn(EstadoPedidoCodigo.PENDIENTE)) {
                anular(pedido, ActorVenta.SISTEMA.username());
                cancelados++;
            }
        }
        return cancelados;
    }

    private void anular(Pedido pedido, String responsable) {
        registrarMovimientos(pedido, TipoMovimiento.INGRESO, "Devolución por pedido cancelado #", responsable);
        pedido.setEstado(estados.porCodigo(EstadoPedidoCodigo.CANCELADO));
    }

    private void registrarMovimientos(Pedido pedido, TipoMovimiento tipo, String motivo, String responsable) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            movimientos.registrar(new MovimientoRequest(tipo, TipoItem.PRODUCTO,
                    detalle.getProducto().getIdProducto(), detalle.getCantidad(),
                    motivo + pedido.getIdPedido()), responsable);
        }
    }

    private Pedido bloquear(Long id) {
        return pedidos.bloquear(id).orElseThrow(() -> new RecursosNoEncontradosException("Pedido no encontrado"));
    }
}
