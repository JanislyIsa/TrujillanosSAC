package com.example.TrujillanosSAC.inventario.config;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import java.time.*;

@Configuration
@EnableScheduling
public class InventarioConfig {
    @Bean
    public Clock inventarioClock() { return Clock.system(ZoneId.of("America/Lima")); }
}
