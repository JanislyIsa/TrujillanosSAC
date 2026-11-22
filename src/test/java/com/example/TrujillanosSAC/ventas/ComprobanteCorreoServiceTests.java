package com.example.TrujillanosSAC.ventas;

import com.example.TrujillanosSAC.Service.CorreoService;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobantePagoDTO;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.impl.ComprobanteCorreoServiceImpl;
import com.example.TrujillanosSAC.ventas.service.service.ComprobantePagoService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComprobanteCorreoServiceTests {

    private final ComprobantePagoService comprobantePago = mock(ComprobantePagoService.class);
    private final CorreoService correo = mock(CorreoService.class);
    private final ComprobanteCorreoServiceImpl servicio = new ComprobanteCorreoServiceImpl(comprobantePago, correo);
    private final ActorVenta cliente = new ActorVenta("ana", false);

    private static ComprobantePagoDTO ticket(String correoCliente) {
        return new ComprobantePagoDTO(
                new ComprobantePagoDTO.Negocio("El Trujillano S.A.C.", "10327958441", null),
                "BOLETA", "Boleta de venta", "B001", "00000012", null,
                1080L, null, "Pagado",
                new ComprobantePagoDTO.ClienteComprobante("Ana Pérez", "DNI", "12345678", correoCliente),
                List.of(), 2, new BigDecimal("17.00"), "PEN", "Yape", "chr_test_123", null, "NO_ENVIADO", "leyenda");
    }

    @Test
    void alAprobarseEnviaAlCorreoDeLaCuenta() {
        when(correo.configurado()).thenReturn(true);
        when(comprobantePago.armar(1080L)).thenReturn(ticket("ana@correo.com"));

        servicio.enviarAlAprobarse(1080L, "otro@correo.com");

        verify(correo).enviarHtml(eq("ana@correo.com"), startsWith("Tu comprobante B001-00000012"),
                eq("mail/comprobante-pago"), anyMap());
    }

    @Test
    void sinCorreoEnLaCuentaUsaElDelPago() {
        when(correo.configurado()).thenReturn(true);
        when(comprobantePago.armar(1080L)).thenReturn(ticket(null));

        servicio.enviarAlAprobarse(1080L, "pago@correo.com");

        verify(correo).enviarHtml(eq("pago@correo.com"), anyString(), anyString(), anyMap());
    }

    @Test
    void unErrorAlArmarElComprobanteNoSePropaga() {
        when(correo.configurado()).thenReturn(true);
        when(comprobantePago.armar(1080L)).thenThrow(new IllegalStateException("BD caída"));

        assertThatCode(() -> servicio.enviarAlAprobarse(1080L, "pago@correo.com")).doesNotThrowAnyException();
        verify(correo, never()).enviarHtml(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void reenviarDevuelveElDestinatario() {
        when(correo.configurado()).thenReturn(true);
        when(comprobantePago.obtener(1080L, cliente)).thenReturn(ticket("ana@correo.com"));

        var respuesta = servicio.reenviar(1080L, cliente);

        assertThat(respuesta.destinatario()).isEqualTo("ana@correo.com");
        verify(correo).enviarHtml(eq("ana@correo.com"), anyString(), anyString(), anyMap());
    }

    @Test
    void reenviarFallaSiNoHayCorreoConfiguradoOEnLaCuenta() {
        when(correo.configurado()).thenReturn(false);
        assertThatThrownBy(() -> servicio.reenviar(1080L, cliente)).isInstanceOf(ReglaNegocioException.class);

        when(correo.configurado()).thenReturn(true);
        when(comprobantePago.obtener(1080L, cliente)).thenReturn(ticket(" "));
        assertThatThrownBy(() -> servicio.reenviar(1080L, cliente))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no tiene un correo");
        verify(correo, never()).enviarHtml(anyString(), anyString(), anyString(), any());
    }
}
