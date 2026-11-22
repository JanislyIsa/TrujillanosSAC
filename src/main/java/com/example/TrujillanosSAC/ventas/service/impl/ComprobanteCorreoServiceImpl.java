package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.Service.CorreoService;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobantePagoDTO;
import com.example.TrujillanosSAC.ventas.dto.response.EnvioComprobanteResponseDTO;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.service.ComprobanteCorreoService;
import com.example.TrujillanosSAC.ventas.service.service.ComprobantePagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("ventasComprobanteCorreoService")
public class ComprobanteCorreoServiceImpl implements ComprobanteCorreoService {

    private static final Logger log = LoggerFactory.getLogger(ComprobanteCorreoServiceImpl.class);
    static final String PLANTILLA = "mail/comprobante-pago";

    private final ComprobantePagoService comprobantePago;
    private final CorreoService correoService;

    public ComprobanteCorreoServiceImpl(ComprobantePagoService comprobantePago, CorreoService correoService) {
        this.comprobantePago = comprobantePago;
        this.correoService = correoService;
    }

    @Override
    @Async
    public void enviarAlAprobarse(Long pedidoId, String correoPago) {
        try {
            if (!correoService.configurado()) {
                log.warn("Comprobante no enviado por correo: el correo no está configurado. pedidoId={}", pedidoId);
                return;
            }
            ComprobantePagoDTO comprobante = comprobantePago.armar(pedidoId);
            String destino = tieneTexto(comprobante.cliente().correo()) ? comprobante.cliente().correo() : correoPago;
            if (!tieneTexto(destino)) {
                log.warn("Comprobante no enviado por correo: el pedido no tiene correo de destino. pedidoId={}", pedidoId);
                return;
            }
            enviar(destino, comprobante);
        } catch (RuntimeException e) {
            // El pago ya está cobrado y guardado: un problema con el correo no debe afectarlo.
            log.error("No se pudo preparar el correo del comprobante. pedidoId={}", pedidoId, e);
        }
    }

    @Override
    public EnvioComprobanteResponseDTO reenviar(Long pedidoId, ActorVenta actor) {
        if (!correoService.configurado()) {
            throw new ReglaNegocioException("El envío de correos no está disponible en este momento");
        }
        // obtener() valida que el usuario pueda ver el pedido y que esté pagado.
        ComprobantePagoDTO comprobante = comprobantePago.obtener(pedidoId, actor);
        String destino = comprobante.cliente().correo();
        if (!tieneTexto(destino)) {
            throw new ReglaNegocioException("La cuenta del cliente no tiene un correo registrado");
        }
        enviar(destino, comprobante);
        log.info("Reenvío del comprobante solicitado. pedidoId={}, empleado={}", pedidoId, actor.empleado());
        return new EnvioComprobanteResponseDTO("Enviamos el comprobante a " + destino, destino);
    }

    private void enviar(String destino, ComprobantePagoDTO comprobante) {
        correoService.enviarHtml(destino, asunto(comprobante), PLANTILLA, Map.of("c", comprobante));
    }

    static String asunto(ComprobantePagoDTO c) {
        String numero = c.serie() != null && c.numero() != null
                ? c.serie() + "-" + c.numero()
                : "pedido N.º " + c.pedidoId();
        return "Tu comprobante " + numero + " · " + c.negocio().razonSocial();
    }

    private static boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
