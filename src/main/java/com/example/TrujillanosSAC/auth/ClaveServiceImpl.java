package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.Service.CorreoService;
import com.example.TrujillanosSAC.auth.model.PasswordResetToken;
import com.example.TrujillanosSAC.auth.repository.PasswordResetTokenRepository;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.ClienteRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class ClaveServiceImpl implements ClaveService {

    private static final Logger log = LoggerFactory.getLogger(ClaveServiceImpl.class);
    private static final ZoneId LIMA = ZoneId.of("America/Lima");
    private static final String ROL_CLIENTE = "CLIENTE";
    private static final String PLANTILLA = "mail/restablecer-clave";
    static final String TOKEN_INVALIDO = "El enlace no es válido o ya venció. Solicita uno nuevo.";

    private final SecureRandom aleatorio = new SecureRandom();
    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final PasswordResetTokenRepository tokens;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;
    private final String tiendaUrl;
    private final long minutosValidez;
    private final long esperaSegundos;

    public ClaveServiceImpl(ClienteRepository clientes, UsuarioRepository usuarios,
                            PasswordResetTokenRepository tokens, PasswordEncoder passwordEncoder,
                            CorreoService correoService,
                            @Value("${app.frontend.tienda-url:http://localhost:4200}") String tiendaUrl,
                            @Value("${app.recuperacion-clave.minutos-validez:30}") long minutosValidez,
                            @Value("${app.recuperacion-clave.espera-segundos:60}") long esperaSegundos) {
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
        this.tiendaUrl = tiendaUrl.endsWith("/") ? tiendaUrl.substring(0, tiendaUrl.length() - 1) : tiendaUrl;
        this.minutosValidez = minutosValidez;
        this.esperaSegundos = esperaSegundos;
    }

    @Override
    @Transactional
    public void solicitarRestablecimiento(String correoIngresado) {
        String correo = correoIngresado.trim().toLowerCase(Locale.ROOT);
        Optional<Usuario> encontrado = clientes.findByCorreoIgnoreCase(correo)
                .flatMap(cliente -> usuarios.findByPersonaIdPersona(cliente.getIdPersona()))
                .filter(ClaveServiceImpl::esCliente);
        if (encontrado.isEmpty()) {
            // No se dice nada al usuario: así no se puede averiguar qué correos están registrados.
            log.info("Restablecimiento de clave solicitado para un correo que no es de un cliente");
            return;
        }
        Usuario usuario = encontrado.get();
        LocalDateTime ahora = LocalDateTime.now(LIMA);

        boolean reciente = tokens.findFirstByUsuarioIdUsuarioOrderByFechaCreacionDesc(usuario.getIdUsuario())
                .map(t -> t.getFechaCreacion().isAfter(ahora.minusSeconds(esperaSegundos)))
                .orElse(false);
        if (reciente) {
            log.info("Restablecimiento de clave ignorado: ya se pidió hace menos de {} s. usuarioId={}",
                    esperaSegundos, usuario.getIdUsuario());
            return;
        }

        tokens.invalidarPendientes(usuario.getIdUsuario());
        String token = generarToken();
        PasswordResetToken registro = new PasswordResetToken();
        registro.setUsuario(usuario);
        registro.setTokenHash(hash(token));
        registro.setFechaCreacion(ahora);
        registro.setFechaExpiracion(ahora.plusMinutes(minutosValidez));
        registro.setUsado(false);
        tokens.save(registro);
        log.info("Enlace de restablecimiento creado. usuarioId={}, venceEn={} min", usuario.getIdUsuario(), minutosValidez);

        Map<String, Object> variables = Map.of(
                "nombre", usuario.getPersona().getNombre(),
                "enlace", tiendaUrl + "/restablecer-clave?token=" + token,
                "minutos", minutosValidez);
        // El correo sale después del commit: si la transacción falla, no se envía un enlace que no existe.
        despuesDelCommit(() -> correoService.enviarHtml(correo, "Restablece tu contraseña · El Trujillano",
                PLANTILLA, variables));
    }

    @Override
    @Transactional
    public void restablecer(String token, String nuevaClave) {
        validarLongitud(nuevaClave);
        LocalDateTime ahora = LocalDateTime.now(LIMA);
        PasswordResetToken registro = tokens.findByTokenHash(hash(token.trim()))
                .filter(t -> t.vigente(ahora))
                .orElseThrow(() -> new ReglaNegocioException(TOKEN_INVALIDO));
        Usuario usuario = registro.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaClave));
        registro.setUsado(true);
        tokens.invalidarPendientes(usuario.getIdUsuario());
        log.info("Contraseña restablecida con enlace. usuarioId={}", usuario.getIdUsuario());
    }

    @Override
    @Transactional
    public void cambiar(String username, String claveActual, String nuevaClave) {
        validarLongitud(nuevaClave);
        Usuario usuario = usuarios.findByUsernameConRoles(username)
                .orElseThrow(() -> new ReglaNegocioException("Usuario no encontrado"));
        if (!passwordEncoder.matches(claveActual, usuario.getPassword())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta");
        }
        if (passwordEncoder.matches(nuevaClave, usuario.getPassword())) {
            throw new ReglaNegocioException("La nueva contraseña debe ser distinta de la actual");
        }
        usuario.setPassword(passwordEncoder.encode(nuevaClave));
        tokens.invalidarPendientes(usuario.getIdUsuario());
        log.info("Contraseña cambiada desde la cuenta. usuarioId={}", usuario.getIdUsuario());
    }

    private static boolean esCliente(Usuario usuario) {
        return usuario.getRoles().stream().anyMatch(r -> ROL_CLIENTE.equalsIgnoreCase(r.getNombre()));
    }

    /** BCrypt solo usa los primeros 72 bytes: una clave más larga se recortaría en silencio. */
    private static void validarLongitud(String clave) {
        if (clave.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar 72 bytes");
        }
    }

    /** 32 bytes aleatorios en Base64 URL (43 caracteres): imposible de adivinar. */
    private String generarToken() {
        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private static void despuesDelCommit(Runnable accion) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    accion.run();
                }
            });
        } else {
            accion.run();
        }
    }
}
