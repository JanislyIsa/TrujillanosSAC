package com.example.TrujillanosSAC.inventario.exception;

import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.*;
import java.util.*;

@RestControllerAdvice(basePackages = "com.example.TrujillanosSAC.inventario.controller.api")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InventarioExceptionHandler {
    private final Clock clock;
    public InventarioExceptionHandler(Clock inventarioClock) { this.clock = inventarioClock; }
    @ExceptionHandler(InventarioException.class)
    ResponseEntity<ErrorResponse> negocio(InventarioException ex, HttpServletRequest request) {
        return error(ex.getEstado(), ex.getMessage(), request, Map.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validar(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String,String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Datos de inventario invalidos", request, errores);
    }
    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class,
            org.springframework.web.method.annotation.HandlerMethodValidationException.class})
    ResponseEntity<ErrorResponse> entrada(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Solicitud invalida: revise campos, tipos y valores", request, Map.of());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> integridad(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "El registro tiene referencias o datos incompatibles", request, Map.of());
    }
    @ExceptionHandler({PessimisticLockingFailureException.class, OptimisticLockingFailureException.class})
    ResponseEntity<ErrorResponse> concurrencia(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "El inventario esta siendo modificado; reintente la operacion", request, Map.of());
    }
    private ResponseEntity<ErrorResponse> error(HttpStatus estado, String mensaje,
            HttpServletRequest request, Map<String,String> errores) {
        return ResponseEntity.status(estado).body(new ErrorResponse(estado.value(), mensaje,
                request.getRequestURI(), LocalDateTime.now(clock), errores));
    }
}
