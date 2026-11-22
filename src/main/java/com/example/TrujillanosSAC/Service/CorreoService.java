package com.example.TrujillanosSAC.Service;

import java.util.Map;

/**
 * Envío de correos HTML armados con plantillas Thymeleaf (src/main/resources/templates/mail).
 * Lo usan auth (restablecer contraseña) y ventas (comprobante de pago).
 */
public interface CorreoService {

    /** {@code true} si hay cuenta de correo configurada (MAIL_USERNAME y MAIL_PASSWORD). */
    boolean configurado();

    /**
     * Envía el correo en segundo plano. Un fallo solo se registra en el log: nunca debe deshacer
     * ni ocultar la operación que lo pidió (por ejemplo, un pago ya cobrado).
     *
     * @param plantilla nombre de la plantilla sin extensión, por ejemplo {@code "mail/restablecer-clave"}
     */
    void enviarHtml(String destinatario, String asunto, String plantilla, Map<String, Object> variables);
}
