package com.example.TrujillanosSAC.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationProvider authenticationProvider,
                                                   com.example.TrujillanosSAC.Service.AdminAccessService acceso) throws Exception {

        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**", "/inventario-app/**").permitAll()
                        .requestMatchers("/login", "/ventas/catalogo", "/ventas/producto/**").permitAll()
                        // Documentacion de la API (solo se publica en dev: en prod springdoc esta desactivado)
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("EMPLEADO")
                        .requestMatchers("/proveedor/**").hasRole("PROVEEDOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean esProveedor = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_PROVEEDOR".equals(authority.getAuthority()));
                            boolean esEmpleado = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_EMPLEADO".equals(authority.getAuthority()));
                            if (esProveedor) {
                                response.sendRedirect("/proveedor/solicitudes");
                            } else if (esEmpleado) {
                                String destino = acceso.obtenerModulos(authentication.getName()).stream()
                                        .map(com.example.TrujillanosSAC.Dto.AdminModuleDto::url)
                                        .findFirst().orElse("/ventas/catalogo");
                                response.sendRedirect(destino);
                            } else {
                                response.sendRedirect("/ventas/catalogo");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/ventas/catalogo")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
