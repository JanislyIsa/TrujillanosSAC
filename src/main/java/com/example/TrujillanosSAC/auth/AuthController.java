package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.auth.dto.CambiarClaveRequestDTO;
import com.example.TrujillanosSAC.auth.dto.LoginRequestDTO;
import com.example.TrujillanosSAC.auth.dto.LoginResponseDTO;
import com.example.TrujillanosSAC.auth.dto.MensajeResponseDTO;
import com.example.TrujillanosSAC.auth.dto.OlvideClaveRequestDTO;
import com.example.TrujillanosSAC.auth.dto.RegistroClienteRequestDTO;
import com.example.TrujillanosSAC.auth.dto.RestablecerClaveRequestDTO;
import com.example.TrujillanosSAC.exception.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Login, registro y contraseña de los usuarios de la API (JWT).
 * Públicos (ApiSecurityConfig): login, registro, olvide-clave y restablecer-clave. cambiar-clave exige sesión.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private static final String MENSAJE_OLVIDE_CLAVE =
            "Si el correo está registrado, te enviamos un enlace para restablecer tu contraseña.";

    private final AuthService authService;
    private final RegistroClienteService registroClienteService;
    private final ClaveService claveService;

    public AuthController(AuthService authService, RegistroClienteService registroClienteService,
                          ClaveService claveService) {
        this.authService = authService;
        this.registroClienteService = registroClienteService;
        this.claveService = claveService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /** Registro público de un cliente. Responde igual que el login: el cliente queda con la sesión iniciada. */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponseDTO registro(@Valid @RequestBody RegistroClienteRequestDTO request) {
        registroClienteService.registrar(request);
        return authService.login(new LoginRequestDTO(request.username().trim(), request.password()));
    }

    /** Pide el enlace para restablecer la contraseña. Responde siempre igual, exista o no el correo. */
    @PostMapping("/olvide-clave")
    public MensajeResponseDTO olvideClave(@Valid @RequestBody OlvideClaveRequestDTO request) {
        claveService.solicitarRestablecimiento(request.correo());
        return new MensajeResponseDTO(MENSAJE_OLVIDE_CLAVE);
    }

    /** Cambia la contraseña con el token del enlace enviado por correo. */
    @PostMapping("/restablecer-clave")
    public MensajeResponseDTO restablecerClave(@Valid @RequestBody RestablecerClaveRequestDTO request) {
        claveService.restablecer(request.token(), request.nuevaClave());
        return new MensajeResponseDTO("Tu contraseña se actualizó. Ya puedes iniciar sesión.");
    }

    /** Cambio de contraseña desde "Mi cuenta" (requiere token JWT). */
    @PostMapping("/cambiar-clave")
    public MensajeResponseDTO cambiarClave(@Valid @RequestBody CambiarClaveRequestDTO request,
                                           Authentication autenticacion) {
        claveService.cambiar(autenticacion.getName(), request.claveActual(), request.nuevaClave());
        return new MensajeResponseDTO("Tu contraseña se actualizó.");
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
