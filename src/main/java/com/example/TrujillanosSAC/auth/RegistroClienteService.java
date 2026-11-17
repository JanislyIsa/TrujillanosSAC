package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.auth.dto.RegistroClienteRequestDTO;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.model.Rol;
import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.ClienteRepository;
import com.example.TrujillanosSAC.usuarios.repository.PersonaRepository;
import com.example.TrujillanosSAC.usuarios.repository.RolRepository;
import com.example.TrujillanosSAC.usuarios.repository.TipoDocumentoRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Registro público de clientes: crea la persona, su perfil de cliente y su usuario con el rol CLIENTE,
 * todo en una sola transacción (si algo falla no queda nada a medias).
 */
@Service
public class RegistroClienteService {

    private static final String ROL_CLIENTE = "CLIENTE";

    private final PersonaRepository personas;
    private final TipoDocumentoRepository tiposDocumento;
    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PasswordEncoder passwordEncoder;

    public RegistroClienteService(PersonaRepository personas, TipoDocumentoRepository tiposDocumento,
                                  ClienteRepository clientes, UsuarioRepository usuarios, RolRepository roles,
                                  PasswordEncoder passwordEncoder) {
        this.personas = personas;
        this.tiposDocumento = tiposDocumento;
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registrar(RegistroClienteRequestDTO request) {
        String username = request.username().trim();
        String numero = request.numeroDocumento().trim();
        String correo = request.correo().trim().toLowerCase(Locale.ROOT);

        // BCrypt solo usa los primeros 72 bytes: una clave más larga se recortaría en silencio.
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña no puede superar 72 bytes");
        }
        TipoDocumento tipo = tiposDocumento.findByAbreviaturaIgnoreCase(request.tipoDocumento().trim())
                .orElseThrow(() -> new ReglaNegocioException("El tipo de documento no es válido"));
        if (tipo.getLongitud() != null && numero.length() != tipo.getLongitud()) {
            throw new ReglaNegocioException("El documento debe tener " + tipo.getLongitud() + " caracteres");
        }
        // DNI y RUC son numéricos; solo el carnet de extranjería admite letras.
        if (!"CE".equalsIgnoreCase(tipo.getAbreviatura()) && !numero.matches("\\d+")) {
            throw new ReglaNegocioException("El documento solo debe tener números");
        }
        if (usuarios.existsByUsername(username)) {
            throw new ReglaNegocioException("El nombre de usuario ya está registrado");
        }
        if (personas.existsByNumDoc(numero)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese número de documento");
        }
        if (clientes.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con ese correo");
        }
        Rol rolCliente = roles.findByNombreIgnoreCase(ROL_CLIENTE)
                .orElseThrow(() -> new ReglaNegocioException("Falta registrar el rol CLIENTE"));

        Persona persona = personas.save(Persona.builder()
                .nombre(request.nombre().trim())
                .apellido(request.apellido().trim())
                .telefono(request.telefono().trim())
                .documento(tipo)
                .numDoc(numero)
                .build());
        clientes.save(Cliente.builder()
                .persona(persona)
                .fechaRegistro(LocalDate.now(ZoneId.of("America/Lima")))
                .correo(correo)
                .build());
        usuarios.save(Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(request.password()))
                .persona(persona)
                .roles(new HashSet<>(Set.of(rolCliente)))
                .build());
    }
}
