package com.example.TrujillanosSAC.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita {@code @Async}: los correos se envían en un hilo aparte para que la respuesta HTTP
 * (registrar un pago, pedir un enlace de recuperación) no espere al servidor SMTP.
 * Usa el ejecutor por defecto de Spring Boot (spring.task.execution.*).
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
