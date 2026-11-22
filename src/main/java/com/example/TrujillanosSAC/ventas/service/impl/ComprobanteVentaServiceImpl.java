package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.request.ComprobanteVentaRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobanteVentaResponseDTO;
import com.example.TrujillanosSAC.ventas.mapper.VentaMapper;
import com.example.TrujillanosSAC.ventas.model.ComprobanteVenta;
import com.example.TrujillanosSAC.ventas.model.Pedido;
import com.example.TrujillanosSAC.ventas.model.TipoComprobanteVenta;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoComprobanteCodigo;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.model.codigo.TipoComprobanteCodigo;
import com.example.TrujillanosSAC.ventas.repository.ComprobanteVentaRepository;
import com.example.TrujillanosSAC.ventas.repository.PedidoRepository;
import com.example.TrujillanosSAC.ventas.repository.TipoComprobanteVentaRepository;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.service.ComprobanteVentaService;
import com.example.TrujillanosSAC.ventas.service.service.EstadoComprobanteVentaService;
import com.example.TrujillanosSAC.ventas.service.facturacion.FacturacionElectronica;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service("ventasComprobanteVentaService")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComprobanteVentaServiceImpl implements ComprobanteVentaService {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteVentaServiceImpl.class);

    private static final long MAX_CORRELATIVO = 99_999_999L;

    private final ComprobanteVentaRepository comprobantes;
    private final PedidoRepository pedidos;
    private final TipoComprobanteVentaRepository tipos;
    private final EstadoComprobanteVentaService estados;
    private final FacturacionElectronica facturacion;
    private final AccesoVentas acceso;

    @Override
    @Transactional
    public ComprobanteVentaResponseDTO emitir(ComprobanteVentaRequestDTO request, ActorVenta actor) {
        Pedido pedido = bloquearPedido(request.pedidoId());
        acceso.verificarPedido(pedido, actor);
        exigirPagado(pedido);
        if (comprobantes.existsByPedido_IdPedido(pedido.getIdPedido())) {
            throw new ReglaNegocioException("El pedido ya tiene comprobante");
        }
        if (request.tipo() == TipoComprobanteCodigo.FACTURA && !tieneRuc(pedido.getCliente())) {
            throw new ReglaNegocioException("Para emitir factura el cliente debe estar registrado con RUC (11 dígitos)");
        }
        return VentaMapper.comprobante(crear(pedido, request.tipo()));
    }

    @Override
    @Transactional
    public ComprobanteVentaResponseDTO emitirAutomatico(Long pedidoId) {
        Pedido pedido = bloquearPedido(pedidoId);
        exigirPagado(pedido);
        // Si ya tiene comprobante (por ejemplo, un reintento) se devuelve el mismo: nunca dos por pedido.
        Optional<ComprobanteVenta> existente = comprobantes.findByPedido_IdPedido(pedidoId);
        if (existente.isPresent()) {
            return VentaMapper.comprobante(existente.get());
        }
        // Cliente con RUC: factura; los demás (DNI, CE): boleta.
        TipoComprobanteCodigo tipo = tieneRuc(pedido.getCliente()) ? TipoComprobanteCodigo.FACTURA : TipoComprobanteCodigo.BOLETA;
        ComprobanteVenta comprobante = crear(pedido, tipo);
        log.info("Comprobante emitido automáticamente. pedidoId={}, comprobante={}-{}",
                pedidoId, comprobante.getSerie(), comprobante.getNumero());
        return VentaMapper.comprobante(comprobante);
    }

    /** Crea el comprobante con el siguiente número de su serie (B001 o F001). */
    private ComprobanteVenta crear(Pedido pedido, TipoComprobanteCodigo codigo) {
        // Bloquear el tipo evita que dos ventas simultáneas reciban el mismo número.
        TipoComprobanteVenta tipo = tipos.bloquearPorCodigo(codigo.name()).orElseThrow(() ->
                new ReglaNegocioException("Falta registrar el tipo de comprobante " + codigo));
        String serie = codigo.serie();
        long siguiente = comprobantes.findTopBySerieOrderByNumeroDesc(serie)
                .map(c -> Long.parseLong(c.getNumero()) + 1).orElse(1L);
        if (siguiente > MAX_CORRELATIVO) {
            throw new ReglaNegocioException("Se agotó la numeración de la serie " + serie);
        }
        ComprobanteVenta comprobante = new ComprobanteVenta();
        comprobante.setPedido(pedido);
        comprobante.setTipo(tipo);
        comprobante.setEstado(estados.porCodigo(EstadoComprobanteCodigo.EMITIDO));
        comprobante.setSerie(serie);
        comprobante.setNumero(String.format(Locale.ROOT, "%08d", siguiente));
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setTotal(pedido.getTotal());
        comprobantes.saveAndFlush(comprobante);
        comprobante.setEstadoSunat(facturacion.enviar(comprobante));
        return comprobante;
    }

    private Pedido bloquearPedido(Long pedidoId) {
        return pedidos.bloquear(pedidoId)
                .orElseThrow(() -> new RecursosNoEncontradosException("Pedido no encontrado"));
    }

    private static void exigirPagado(Pedido pedido) {
        if (!(pedido.estaEn(EstadoPedidoCodigo.PAGADO) || pedido.estaEn(EstadoPedidoCodigo.LISTO_PARA_RECOJO)
                || pedido.estaEn(EstadoPedidoCodigo.ENTREGADO))) {
            throw new ReglaNegocioException("Solo se emite comprobante de un pedido pagado");
        }
    }

    @Override
    public ComprobanteVentaResponseDTO obtener(Long id, ActorVenta actor) {
        ComprobanteVenta comprobante = comprobantes.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Comprobante no encontrado"));
        acceso.verificarPedido(comprobante.getPedido(), actor);
        return VentaMapper.comprobante(comprobante);
    }

    @Override
    public ComprobanteVentaResponseDTO obtenerPorPedido(Long pedidoId, ActorVenta actor) {
        ComprobanteVenta comprobante = comprobantes.findByPedido_IdPedido(pedidoId)
                .orElseThrow(() -> new RecursosNoEncontradosException("El pedido todavía no tiene comprobante"));
        acceso.verificarPedido(comprobante.getPedido(), actor);
        return VentaMapper.comprobante(comprobante);
    }

    @Override
    public List<ComprobanteVentaResponseDTO> listar() {
        return comprobantes.listarTodos().stream().map(VentaMapper::comprobante).toList();
    }

    private static boolean tieneRuc(Cliente cliente) {
        Persona persona = cliente.getPersona();
        return persona != null && persona.getDocumento() != null
                && "RUC".equalsIgnoreCase(persona.getDocumento().getAbreviatura())
                && persona.getNumDoc() != null && persona.getNumDoc().matches("\\d{11}");
    }
}
