package com.example.TrujillanosSAC.ventas.exception;

import com.example.TrujillanosSAC.exception.dto.ErrorResponseDTO;
import com.example.TrujillanosSAC.inventario.exception.InventarioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;

/**
 * Errores propios de ventas que el GlobalExceptionHandler no cubre. Los demás
 * (no encontrado, regla de negocio, validación) siguen saliendo por el handler global.
 */
@RestControllerAdvice(basePackages = "com.example.TrujillanosSAC.ventas.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class VentasExceptionHandler {

    /** Lo lanza inventario al registrar el movimiento de stock (por ejemplo, stock insuficiente). */
    @ExceptionHandler(InventarioException.class)
    public ResponseEntity<ErrorResponseDTO> inventario(InventarioException ex, HttpServletRequest request) {
        return respuesta(ex.getEstado(), ex.getMessage(), request);
    }

    /** Por ejemplo, borrar un método de pago que ya tiene pagos registrados. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "El registro está en uso o ya existe", request);
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponseDTO> bloqueo(PessimisticLockingFailureException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "Otro usuario está modificando el mismo pedido; intente nuevamente", request);
    }

    /** Parámetros de la URL inválidos, por ejemplo /pedidos/-1. */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDTO> parametros(HandlerMethodValidationException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "Parámetros inválidos", request);
    }

    private static ResponseEntity<ErrorResponseDTO> respuesta(HttpStatus status, String mensaje, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensaje, request.getRequestURI(), null));
    }
}
