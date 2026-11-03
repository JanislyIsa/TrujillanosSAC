package com.example.TrujillanosSAC.inventario.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.*;
import java.util.*;

@Configuration @EnableMethodSecurity
public class InventarioSecurityConfig {
    @Bean @Order(1)
    public SecurityFilterChain inventarioApiSecurity(HttpSecurity http, AuthenticationProvider provider,
            @Value("${inventario.cors.origenes:http://localhost:5173,http://localhost:3000}") String origenes) throws Exception {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origenes.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type","Authorization","X-XSRF-TOKEN"));
        cors.setAllowCredentials(true);
        cors.setExposedHeaders(List.of("Location"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/inventario/**",cors);
        http.securityMatcher("/api/v1/inventario/**").authenticationProvider(provider)
                .cors(c -> c.configurationSource(source))
                .csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(a -> a.requestMatchers("/api/v1/inventario/csrf","/api/v1/inventario/sesion/login").permitAll().anyRequest().authenticated())
                .httpBasic(b -> b.authenticationEntryPoint((req,res,ex) -> {
                    res.setStatus(401); res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"estado\":401,\"mensaje\":\"Autenticacion requerida\",\"errores\":{}}");
                }))
                .exceptionHandling(e -> e.accessDeniedHandler((req,res,ex) -> {
                    res.setStatus(403); res.setContentType("application/json;charset=UTF-8");
                    res.getWriter().write("{\"estado\":403,\"mensaje\":\"Acceso denegado o token CSRF invalido\",\"errores\":{}}");
                }));
        return http.build();
    }
}
