package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.ventas.dto.request.PagoCulqiRequestDTO;
import com.example.TrujillanosSAC.ventas.dto.response.PagoResponseDTO;
import com.example.TrujillanosSAC.ventas.mapper.VentaMapper;
import com.example.TrujillanosSAC.ventas.model.Pago;
import com.example.TrujillanosSAC.ventas.repository.PagoRepository;
import com.example.TrujillanosSAC.ventas.service.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.PagoService;
import com.example.TrujillanosSAC.ventas.service.pasarela.PasarelaPago;
import com.example.TrujillanosSAC.ventas.service.pasarela.ResultadoCobro;
import com.example.TrujillanosSAC.ventas.service.pasarela.SolicitudCobro;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("ventasPagoService")
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final PagoTransacciones transacciones;
    private final PasarelaPago pasarela;
    private final PagoRepository pagos;
    private final AccesoVentas acceso;

    // Sin @Transactional a propósito: cada paso de PagoTransacciones abre y cierra su propia transacción.
    @Override
    public PagoResponseDTO pagarConCulqi(PagoCulqiRequestDTO request, ActorVenta actor) {
        // 1) Reservar: el pedido pasa a "procesando pago".
        PagoTransacciones.Intento intento = transacciones.iniciarPago(request.pedidoId(), actor);
        // 2) Cobrar en la pasarela, fuera de cualquier transacción.
        ResultadoCobro resultado;
        try {
            resultado = pasarela.cobrar(new SolicitudCobro(intento.montoCentimos(), intento.moneda(),
                    request.email(), request.token(), "Pedido #" + request.pedidoId()));
        } catch (RuntimeException e) {
            // Pase lo que pase, el pedido no debe quedar trabado en "procesando pago".
            log.error("Fallo inesperado al cobrar el pedido {}", request.pedidoId(), e);
            resultado = ResultadoCobro.error("Error inesperado al procesar el pago");
        }
        // 3) Guardar el resultado.
        return transacciones.finalizar(intento.idPago(), resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> listar(Long pedidoId, ActorVenta actor) {
        List<Pago> lista;
        if (pedidoId != null) {
            lista = pagos.listarPorPedido(pedidoId);
            lista.stream().findFirst().ifPresent(p -> acceso.verificarPedido(p.getPedido(), actor));
        } else if (actor.empleado()) {
            lista = pagos.listarTodos();
        } else {
            lista = pagos.listarPorCliente(acceso.idPersona(actor));
        }
        return lista.stream().map(VentaMapper::pago).toList();
    }
}
