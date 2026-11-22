package com.example.TrujillanosSAC.ServiceImpl;

import com.example.TrujillanosSAC.Service.CorreoService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Service
public class CorreoServiceImpl implements CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoServiceImpl.class);
    private static final Locale ES_PE = Locale.forLanguageTag("es-PE");

    private final JavaMailSender mailSender;
    private final ITemplateEngine plantillas;
    private final String usuario;
    private final String clave;
    private final String remitente;
    private final String nombreRemitente;

    public CorreoServiceImpl(JavaMailSender mailSender,
                             ITemplateEngine plantillas,
                             @Value("${spring.mail.username:}") String usuario,
                             @Value("${spring.mail.password:}") String clave,
                             @Value("${app.correo.remitente:}") String remitente,
                             @Value("${app.correo.nombre-remitente:El Trujillano}") String nombreRemitente) {
        this.mailSender = mailSender;
        this.plantillas = plantillas;
        this.usuario = usuario == null ? "" : usuario.trim();
        this.clave = clave == null ? "" : clave;
        this.remitente = remitente == null || remitente.isBlank() ? this.usuario : remitente.trim();
        this.nombreRemitente = nombreRemitente;
    }

    @Override
    public boolean configurado() {
        return !usuario.isBlank() && !clave.isBlank();
    }

    @Override
    @Async
    public void enviarHtml(String destinatario, String asunto, String plantilla, Map<String, Object> variables) {
        if (!configurado()) {
            log.warn("Correo no enviado: falta configurar MAIL_USERNAME y MAIL_PASSWORD. plantilla={}", plantilla);
            return;
        }
        if (destinatario == null || destinatario.isBlank()) {
            log.warn("Correo no enviado: sin destinatario. plantilla={}", plantilla);
            return;
        }
        long inicio = System.currentTimeMillis();
        try {
            String html = plantillas.process(plantilla, new Context(ES_PE, variables == null ? Map.of() : variables));
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, StandardCharsets.UTF_8.name());
            helper.setFrom(remitente, nombreRemitente);
            helper.setTo(destinatario.trim());
            helper.setSubject(asunto);
            helper.setText(html, true);
            mailSender.send(mensaje);
            log.info("Correo enviado. plantilla={}, destinatario={}, duracionMs={}",
                    plantilla, enmascarar(destinatario), System.currentTimeMillis() - inicio);
        } catch (MessagingException | UnsupportedEncodingException | RuntimeException e) {
            // MailException (SMTP caído, credenciales malas) y errores de plantilla son RuntimeException.
            log.error("No se pudo enviar el correo. plantilla={}, destinatario={}",
                    plantilla, enmascarar(destinatario), e);
        }
    }

    /** Para los logs: "j***@gmail.com" en vez del correo completo. */
    static String enmascarar(String correo) {
        String c = correo.trim();
        int arroba = c.indexOf('@');
        if (arroba <= 0) return "***";
        return c.charAt(0) + "***" + c.substring(arroba);
    }
}
