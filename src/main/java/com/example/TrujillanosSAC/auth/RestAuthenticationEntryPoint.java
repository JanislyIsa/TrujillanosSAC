package com.example.TrujillanosSAC.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/** 401 en JSON: sin token, token vencido, token inválido o usuario inexistente. */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Object motivo = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR);
        String mensaje = motivo != null ? motivo.toString() : "Debe iniciar sesión para acceder a este recurso";
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        RespuestaErrorSeguridad.escribir(request, response, HttpStatus.UNAUTHORIZED.value(), "Unauthorized", mensaje);
    }
}
