package com.example.TrujillanosSAC.exception;

import com.example.TrujillanosSAC.exception.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shared REST error mapping for application modules. Module-specific advice can take precedence. */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursosNoEncontradosException.class)
    public ResponseEntity<ErrorResponseDTO> recursoNoEncontrado(
            RecursosNoEncontradosException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> reglaDeNegocio(
            ReglaNegocioException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validacionRequest(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "Bad Request",
                "Existen errores de validación", request, errores);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> validacionParametros(
            ConstraintViolationException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            errores.put(path.substring(path.lastIndexOf('.') + 1), violation.getMessage());
        });
        return respuesta(HttpStatus.BAD_REQUEST, "Bad Request",
                "Existen errores de validación", request, errores);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> noAutenticado(
            AuthenticationException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Unauthorized",
                "Se requiere autenticación válida", request, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> accesoDenegado(
            AccessDeniedException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.FORBIDDEN, "Forbidden",
                "No tiene permisos para realizar esta operación", request, null);
    }

    private ResponseEntity<ErrorResponseDTO> respuesta(
            HttpStatus status, String error, String message,
            HttpServletRequest request, Map<String, String> validationErrors) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(), status.value(), error, message,
                request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}
