package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.auth.dto.LoginRequestDTO;
import com.example.TrujillanosSAC.auth.dto.LoginResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private static final String PREFIJO_ROL = "ROLE_";

    private final AuthenticationProvider authenticationProvider;
    private final JwtService jwtService;
    private final UsuarioRepository usuarios;

    public AuthServiceImpl(AuthenticationProvider authenticationProvider, JwtService jwtService,
                           UsuarioRepository usuarios) {
        this.authenticationProvider = authenticationProvider;
        this.jwtService = jwtService;
        this.usuarios = usuarios;
    }

    // Sin @Transactional: la consulta de la persona ya trae todo con "join fetch", y así una clave incorrecta
    // (excepción) no marca para deshacer una transacción ajena.
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.username().trim();
        // El mismo AuthenticationProvider (UsuarioDetailsService + BCrypt) que usa el resto del sistema.
        Authentication autenticacion = authenticationProvider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, request.password()));

        List<String> roles = autenticacion.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith(PREFIJO_ROL))
                .map(a -> a.substring(PREFIJO_ROL.length()))
                .sorted()
                .toList();
        String nombreUsuario = autenticacion.getName();
        String nombreCompleto = usuarios.findByUsernameConRolesYPersona(nombreUsuario)
                .map(u -> nombreCompleto(u.getPersona()))
                .orElse(nombreUsuario);

        log.info("Inicio de sesión correcto: usuario={} roles={}", nombreUsuario, roles);
        return new LoginResponseDTO(
                jwtService.generarToken(nombreUsuario, roles),
                "Bearer",
                jwtService.getExpiracionSegundos(),
                nombreUsuario,
                nombreCompleto,
                roles,
                PermisosPorRol.permisos(roles));
    }

    private static String nombreCompleto(Persona persona) {
        return persona == null ? "" : (persona.getNombre() + " " + persona.getApellido()).trim();
    }
}
