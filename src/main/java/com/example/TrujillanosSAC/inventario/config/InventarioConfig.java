package com.example.TrujillanosSAC.inventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@EnableScheduling
public class InventarioConfig implements WebMvcConfigurer {

    @Bean
    public Clock inventarioClock() {
        return Clock.system(ZoneId.of("America/Lima"));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController(
                "/admin/inventario",
                "/inventario-app/index.html#/inventario/catalogo");
        registry.addRedirectViewController(
                "/admin/inventario/control-stock",
                "/inventario-app/index.html#/inventario/stock");
        registry.addRedirectViewController(
                "/admin/inventario/alertas",
                "/inventario-app/index.html#/inventario/alertas");
        registry.addRedirectViewController(
                "/admin/inventario/movimientos",
                "/inventario-app/index.html#/inventario/movimientos");
        registry.addRedirectViewController(
                "/admin/productos",
                "/inventario-app/index.html#/inventario/catalogo");
    }
}
