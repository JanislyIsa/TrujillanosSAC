package com.example.TrujillanosSAC.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Botón "Authorize" de Swagger con dos esquemas:
 * - "jwt": para /api/v1/ventas/** (pegar el token de POST /api/v1/auth/login, sin la palabra Bearer).
 * - "usuario": usuario y contraseña (Basic) para la API de inventario.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "API Trujillanos SAC", version = "v1"),
        security = @SecurityRequirement(name = "usuario"))
@SecuritySchemes({
        @SecurityScheme(name = "usuario", type = SecuritySchemeType.HTTP, scheme = "basic"),
        @SecurityScheme(name = "jwt", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
})
public class OpenApiConfig {

    /** Ventas usa el token JWT; el login no pide nada. */
    @Bean
    public OpenApiCustomizer seguridadJwtEnVentas() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((ruta, item) -> {
                if (ruta.startsWith("/api/v1/ventas/") || ruta.startsWith("/api/v1/auth/")) {
                    List<io.swagger.v3.oas.models.security.SecurityRequirement> seguridad = new ArrayList<>();
                    if (!ruta.equals("/api/v1/auth/login")) {
                        seguridad.add(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("jwt"));
                    }
                    item.readOperations().forEach(operacion -> operacion.setSecurity(seguridad));
                }
            });
        };
    }
}
