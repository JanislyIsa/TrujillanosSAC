package com.example.TrujillanosSAC.Config;

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
 * Única forma de autenticación del sistema: token JWT (POST /api/v1/auth/login) para TODA la API /api/**
 * (auth, ventas, compras, inventario y usuarios). Sin sesión, sin cookies y sin CSRF: cada petición trae
 * "Authorization: Bearer &lt;token&gt;". Los 401 y 403 se responden en JSON.
 */
@Configuration
public class ApiSecurityConfig {
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http, JwtService jwtService,
            UserDetailsService userDetailsService,
            @Value("${api.cors.origenes:http://localhost:4200}") String origenes) throws Exception {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Arrays.stream(origenes.split(","))
                .map(String::trim).filter(origin -> !origin.isEmpty()).toList());
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        // Inventario devuelve la URL del recurso creado en Location (por ejemplo, el comprobante de un movimiento).
        cors.setExposedHeaders(List.of("Location", "X-Request-Id"));
        cors.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", cors);

        http.securityMatcher("/api/**", "/actuator/**")
                .cors(config -> config.configurationSource(source))
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/registro",
                                "/api/v1/auth/olvide-clave", "/api/v1/auth/restablecer-clave").permitAll()
                        // Catálogo público: la tienda se puede ver sin iniciar sesión (solo lectura).
                        .requestMatchers(HttpMethod.GET, "/api/v1/ventas/productos", "/api/v1/ventas/productos/*").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler()))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
