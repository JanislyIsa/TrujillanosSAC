package com.example.TrujillanosSAC.ventas.config;

import com.example.TrujillanosSAC.auth.JwtAuthenticationFilter;
import com.example.TrujillanosSAC.auth.JwtService;
import com.example.TrujillanosSAC.auth.RestAccessDeniedHandler;
import com.example.TrujillanosSAC.auth.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Seguridad de la API REST para el frontend Angular (trujillano-frontend): /api/v1/auth/** y /api/v1/ventas/**.
 * <p>
 * Sin sesión (STATELESS) y sin CSRF: cada petición trae "Authorization: Bearer &lt;token&gt;", que se obtiene en
 * POST /api/v1/auth/login. Los 401 y 403 se responden en JSON. No afecta a inventario (InventarioSecurityConfig,
 * sesión + CSRF) ni a las páginas Thymeleaf (SecurityConfig).
 */
@Configuration
public class VentasSecurityConfig {

    @Bean
    @Order(2)
    public SecurityFilterChain ventasApiSecurity(HttpSecurity http, JwtService jwtService,
            UserDetailsService userDetailsService,
            @Value("${ventas.cors.origenes:http://localhost:4200}") String origenes) throws Exception {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origenes.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        cors.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/v1/auth/**", cors);
        source.registerCorsConfiguration("/api/v1/ventas/**", cors);

        http.securityMatcher("/api/v1/auth/**", "/api/v1/ventas/**")
                .cors(c -> c.configurationSource(source))
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
