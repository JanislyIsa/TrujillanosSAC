package com.example.TrujillanosSAC.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lee "Authorization: Bearer &lt;token&gt;", lo valida y autentica al usuario. Basado en el de sysventas.
 * <p>
 * No es un @Component a propósito: se crea solo dentro de la cadena de seguridad de la API
 * (VentasSecurityConfig). Como bean, Spring lo registraría también para inventario y las páginas Thymeleaf.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Motivo del rechazo, para que RestAuthenticationEntryPoint lo incluya en el 401. */
    public static final String ATRIBUTO_ERROR = "jwt.error";

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String encabezado = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (encabezado == null || !encabezado.startsWith(PREFIJO)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = encabezado.substring(PREFIJO.length()).trim();
        try {
            String username = jwtService.extraerUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Se consulta la base en cada petición: los roles salen de la base (ROLE_CLIENTE, ROLE_EMPLEADO)
                // y, si al usuario le quitan un rol o lo eliminan, el cambio aplica de inmediato.
                UserDetails usuario = userDetailsService.loadUserByUsername(username);
                if (usuario.isEnabled()) {
                    java.util.List<GrantedAuthority> authorities = new java.util.ArrayList<>();
                    authorities.addAll(usuario.getAuthorities());
                    var roles = usuario.getAuthorities().stream()
                            .map(authority -> authority.getAuthority())
                            .filter(authority -> authority.startsWith("ROLE_"))
                            .map(authority -> authority.substring("ROLE_".length()))
                            .toList();
                    PermisosPorRol.permisos(roles).stream()
                            .map(SimpleGrantedAuthority::new).forEach(authorities::add);
                    var autenticacion = UsernamePasswordAuthenticationToken.authenticated(
                            usuario, null, authorities);
                    autenticacion.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContext contexto = SecurityContextHolder.createEmptyContext();
                    contexto.setAuthentication(autenticacion);
                    SecurityContextHolder.setContext(contexto);
                } else {
                    request.setAttribute(ATRIBUTO_ERROR, "El usuario está deshabilitado");
                }
            }
        } catch (ExpiredJwtException ex) {
            log.debug("Token vencido en {}", request.getRequestURI());
            request.setAttribute(ATRIBUTO_ERROR, "El token ha expirado. Inicie sesión nuevamente");
        } catch (UsernameNotFoundException ex) {
            log.warn("Token de un usuario que ya no existe en {}", request.getRequestURI());
            request.setAttribute(ATRIBUTO_ERROR, "El usuario del token no existe");
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Token inválido en {}: {}", request.getRequestURI(), ex.getMessage());
            request.setAttribute(ATRIBUTO_ERROR, "El token no es válido");
        }
        filterChain.doFilter(request, response);
    }
}
