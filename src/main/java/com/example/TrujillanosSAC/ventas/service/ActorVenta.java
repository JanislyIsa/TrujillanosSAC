package com.example.TrujillanosSAC.ventas.service;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Quién hace la operación: un empleado (ve y gestiona todo) o un cliente (solo lo suyo). */
public record ActorVenta(String username, boolean empleado) {

    /** Lo usa la tarea automática que cancela pedidos vencidos. */
    public static final ActorVenta SISTEMA = new ActorVenta("sistema", true);

    public static ActorVenta actual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Se requiere autenticación");
        }
        boolean empleado = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_EMPLEADO".equals(a.getAuthority()));
        return new ActorVenta(auth.getName(), empleado);
    }
}
