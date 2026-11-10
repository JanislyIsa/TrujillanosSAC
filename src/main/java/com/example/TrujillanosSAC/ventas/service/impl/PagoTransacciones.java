package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.response.PagoResponseDTO;
import com.example.TrujillanosSAC.ventas.mapper.VentaMapper;
import com.example.TrujillanosSAC.ventas.model.MetodoPago;
import com.example.TrujillanosSAC.ventas.model.Pago;
import com.example.TrujillanosSAC.ventas.model.Pedido;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPagoCodigo;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.model.codigo.MetodoPagoCodigo;
import com.example.TrujillanosSAC.ventas.repository.PagoRepository;
import com.example.TrujillanosSAC.ventas.repository.PedidoRepository;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.EstadoPagoService;
import com.example.TrujillanosSAC.ventas.service.EstadoPedidoService;
import com.example.TrujillanosSAC.ventas.service.MetodoPagoService;
import com.example.TrujillanosSAC.ventas.service.pasarela.ResultadoCobro;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pasos del pago que tocan la base de datos, cada uno en su propia transacción.
 * Están separados de {@link PagoServiceImpl} para que la llamada a Culqi ocurra entre dos
 * transacciones y no con el pedido bloqueado mientras la pasarela responde.
 */
@Component("ventasPagoTransacciones")
@RequiredArgsConstructor
public class PagoTransacciones {

    static final String MONEDA = "PEN";
    private static final int MAX_MENSAJE = 255;

    private final PagoRepository pagos;
    private final PedidoRepository pedidos;
    private final EstadoPedidoService estadosPedido;
    private final EstadoPagoService estadosPago;
    private final MetodoPagoService metodos;
    private final AccesoVentas acceso;

    public record Intento(Long idPago, long montoCentimos, String moneda) {
    }

    /** Paso 1: pasa el pedido a "procesando pago". Un segundo intento (doble clic) se rechaza aquí. */
    @Transactional
    public Intento iniciarPago(Long pedidoId, ActorVenta actor) {
        Pedido pedido = bloquear(pedidoId);
        acceso.verificarPedido(pedido, actor);
        exigirPendiente(pedido);
        pedido.setEstado(estadosPedido.porCodigo(EstadoPedidoCodigo.PROCESANDO_PAGO));
        // Método provisional: se corrige en finalizar() si el cliente pagó con Yape.
        Pago pago = nuevoPago(pedido, metodos.porCodigo(MetodoPagoCodigo.TARJETA), EstadoPagoCodigo.PROCESANDO);
        pagos.save(pago);
        return new Intento(pago.getIdPago(), centimos(pedido.getTotal()), MONEDA);
    }

    /** Paso 3: guarda lo que respondió la pasarela. Si no se aprobó, el pedido vuelve a "pendiente". */
    @Transactional
    public PagoResponseDTO finalizar(Long idPago, ResultadoCobro resultado) {
        Pago pago = pagos.findById(idPago).orElseThrow(() -> new RecursosNoEncontradosException("Pago no encontrado"));
        Pedido pedido = bloquear(pago.getPedido().getIdPedido());
        pago.setReferenciaExterna(resultado.referencia());
        pago.setMensaje(recortar(resultado.mensaje()));
        switch (resultado.resultado()) {
            case APROBADO -> {
                pago.setEstado(estadosPago.porCodigo(EstadoPagoCodigo.APROBADO));
                if (resultado.metodo() != null) {
                    // Al iniciar no se sabe si el cliente eligió tarjeta o Yape: lo informa la pasarela.
                    // El cobro ya se hizo: si ese método no está configurado se conserva el provisional
                    // en vez de fallar y dejar el pedido trabado.
                    metodos.buscarPorCodigo(resultado.metodo()).ifPresentOrElse(pago::setMetodoPago, () ->
                            pago.setMensaje(recortar("Método " + resultado.metodo() + " sin configurar. " + pago.getMensaje())));
                }
                pedido.setEstado(estadosPedido.porCodigo(EstadoPedidoCodigo.PAGADO));
            }
            case RECHAZADO -> {
                pago.setEstado(estadosPago.porCodigo(EstadoPagoCodigo.RECHAZADO));
                pedido.setEstado(estadosPedido.porCodigo(EstadoPedidoCodigo.PENDIENTE));
            }
            case ERROR -> {
                // Sin respuesta clara de la pasarela. Antes de reintentar conviene revisar el panel de Culqi:
                // el cargo pudo haberse hecho. (Cuando se agreguen webhooks, Culqi avisará el resultado real.)
                pago.setEstado(estadosPago.porCodigo(EstadoPagoCodigo.ERROR));
                pedido.setEstado(estadosPedido.porCodigo(EstadoPedidoCodigo.PENDIENTE));
            }
        }
        return VentaMapper.pago(pago);
    }

    /** Culqi recibe el monto en céntimos: S/ 25.50 se envía como 2550. */
    static long centimos(BigDecimal monto) {
        return monto.movePointRight(2).longValueExact();
    }

    private Pago nuevoPago(Pedido pedido, MetodoPago metodo, EstadoPagoCodigo estado) {
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(metodo);
        pago.setEstado(estadosPago.porCodigo(estado));
        pago.setMonto(pedido.getTotal());
        pago.setMoneda(MONEDA);
        pago.setFechaPago(LocalDateTime.now());
        return pago;
    }

    private void exigirPendiente(Pedido pedido) {
        if (pedido.estaEn(EstadoPedidoCodigo.PROCESANDO_PAGO)) {
            throw new ReglaNegocioException("Ya hay un pago en proceso para este pedido");
        }
        if (!pedido.estaEn(EstadoPedidoCodigo.PENDIENTE)) {
            throw new ReglaNegocioException("El pedido no está pendiente de pago");
        }
    }

    private Pedido bloquear(Long id) {
        return pedidos.bloquear(id).orElseThrow(() -> new RecursosNoEncontradosException("Pedido no encontrado"));
    }

    private static String recortar(String texto) {
        return texto == null || texto.length() <= MAX_MENSAJE ? texto : texto.substring(0, MAX_MENSAJE);
    }
}
