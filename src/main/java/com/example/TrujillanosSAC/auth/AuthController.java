package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.auth.dto.LoginRequestDTO;
import com.example.TrujillanosSAC.auth.dto.LoginResponseDTO;
import com.example.TrujillanosSAC.exception.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/** Login de la API (JWT). Público: lo permite VentasSecurityConfig. */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /** Usuario inexistente o contraseña incorrecta: el mismo mensaje en ambos casos, para no revelar cuál fue. */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> credencialesInvalidas(AuthenticationException ex, HttpServletRequest request) {
        log.info("Inicio de sesión fallido: {}", ex.getClass().getSimpleName());
        HttpStatus estado = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(estado).body(new ErrorResponseDTO(LocalDateTime.now(), estado.value(),
                estado.getReasonPhrase(), "Usuario o contraseña incorrectos", request.getRequestURI(), null));
    }
}
