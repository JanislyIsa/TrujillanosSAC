package com.example.TrujillanosSAC.inventario.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.frontend.empleados-url:http://127.0.0.1:4200}")
    private String frontendEmpleadosUrl;

    @Bean
    public Clock inventarioClock() {
        return Clock.system(ZoneId.of("America/Lima"));
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String frontend = quitarBarrasFinales(frontendEmpleadosUrl);
        registry.addRedirectViewController(
                "/admin/inventario",
                frontend + "/#/inventario/catalogo");
        registry.addRedirectViewController(
                "/admin/inventario/control-stock",
                frontend + "/#/inventario/stock");
        registry.addRedirectViewController(
                "/admin/inventario/alertas",
                frontend + "/#/inventario/alertas");
        registry.addRedirectViewController(
                "/admin/inventario/movimientos",
                frontend + "/#/inventario/movimientos");
        registry.addRedirectViewController(
                "/admin/productos",
                frontend + "/#/inventario/catalogo");
    }

    static String quitarBarrasFinales(String url) {
        int longitud = url.length();
        while (longitud > 0 && url.charAt(longitud - 1) == '/') {
            longitud--;
        }
        return url.substring(0, longitud);
    }
}
