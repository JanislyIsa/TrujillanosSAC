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

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {
    @ExceptionHandler(RecursosNoEncontradosException.class)
    public ResponseEntity<ErrorResponseDTO> notFound(RecursosNoEncontradosException ex, HttpServletRequest request) {
        return body(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, null);
    }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> businessRule(ReglaNegocioException ex, HttpServletRequest request) {
        return body(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request, null);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> invalidBody(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String,String> errors=new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Bad Request", "Existen errores de validación", request, errors);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> invalidParameter(ConstraintViolationException ex, HttpServletRequest request) {
        Map<String,String> errors=new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> { String path=v.getPropertyPath().toString(); errors.put(path.substring(path.lastIndexOf('.')+1),v.getMessage()); });
        return body(HttpStatus.BAD_REQUEST, "Bad Request", "Existen errores de validación", request, errors);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> unauthenticated(AuthenticationException ex, HttpServletRequest request) {
        return body(HttpStatus.UNAUTHORIZED, "Unauthorized", "Se requiere autenticación válida", request, null);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> forbidden(AccessDeniedException ex, HttpServletRequest request) {
        return body(HttpStatus.FORBIDDEN, "Forbidden", "No tiene permisos para realizar esta operación", request, null);
    }
    private ResponseEntity<ErrorResponseDTO> body(HttpStatus status, String error, String message,
                                                   HttpServletRequest request, Map<String,String> validationErrors) {
        return ResponseEntity.status(status).body(new ErrorResponseDTO(LocalDateTime.now(), status.value(), error,
                message, request.getRequestURI(), validationErrors));
    }
}
