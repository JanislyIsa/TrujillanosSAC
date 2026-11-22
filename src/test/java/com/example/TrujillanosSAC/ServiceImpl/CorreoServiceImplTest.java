package com.example.TrujillanosSAC.ServiceImpl;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CorreoServiceImplTest {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final ITemplateEngine plantillas = mock(ITemplateEngine.class);

    private CorreoServiceImpl servicio(String usuario, String clave) {
        return new CorreoServiceImpl(mailSender, plantillas, usuario, clave, "", "El Trujillano");
    }

    @Test
    void sinCuentaConfiguradaNoEnviaNada() {
        CorreoServiceImpl correo = servicio("", "");

        assertThat(correo.configurado()).isFalse();
        correo.enviarHtml("cliente@correo.com", "Asunto", "mail/prueba", Map.of());

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void conCuentaEnviaElHtmlDeLaPlantilla() throws Exception {
        CorreoServiceImpl correo = servicio("negocio@gmail.com", "clave-de-aplicacion");
        when(plantillas.process(eq("mail/prueba"), any(IContext.class))).thenReturn("<p>Hola</p>");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));

        correo.enviarHtml("cliente@correo.com", "Asunto", "mail/prueba", Map.of("nombre", "Ana"));

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void unFalloDelServidorDeCorreoNoSePropaga() {
        CorreoServiceImpl correo = servicio("negocio@gmail.com", "clave-de-aplicacion");
        when(plantillas.process(eq("mail/prueba"), any(IContext.class))).thenReturn("<p>Hola</p>");
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((jakarta.mail.Session) null));
        doThrow(new MailSendException("SMTP caído")).when(mailSender).send(any(MimeMessage.class));

        assertThatCode(() -> correo.enviarHtml("cliente@correo.com", "Asunto", "mail/prueba", Map.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void enmascaraElCorreoEnLosLogs() {
        assertThat(CorreoServiceImpl.enmascarar("juan.perez@gmail.com")).isEqualTo("j***@gmail.com");
        assertThat(CorreoServiceImpl.enmascarar("sin-arroba")).isEqualTo("***");
    }
}
