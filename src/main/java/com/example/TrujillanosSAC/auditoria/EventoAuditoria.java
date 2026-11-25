package com.example.TrujillanosSAC.auditoria;

import org.slf4j.MDC;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Evento inmutable publicado por operaciones de negocio que ya están dentro de una transacción. */
public record EventoAuditoria(String usuario, String accion, String entidad,
                              Long entidadId, String detalle, String requestId) {
    public static EventoAuditoria actual(String accion, String entidad, Long entidadId, String detalle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)
                ? auth.getName() : "sistema";
        return new EventoAuditoria(limitar(usuario, 50), limitar(accion, 40), limitar(entidad, 40),
                entidadId, limitar(detalle, 500), limitar(MDC.get("requestId"), 36));
    }

    private static String limitar(String texto, int maximo) {
        return texto == null ? null : texto.substring(0, Math.min(texto.length(), maximo));
    }
}
