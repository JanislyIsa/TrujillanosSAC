package com.example.TrujillanosSAC.auth;

import com.example.TrujillanosSAC.Service.CorreoService;
import com.example.TrujillanosSAC.auth.model.PasswordResetToken;
import com.example.TrujillanosSAC.auth.repository.PasswordResetTokenRepository;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.model.Rol;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.ClienteRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClaveServiceImplTest {

    private static final LocalDateTime AHORA = LocalDateTime.now(ZoneId.of("America/Lima"));

    private final ClienteRepository clientes = mock(ClienteRepository.class);
    private final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    private final PasswordResetTokenRepository tokens = mock(PasswordResetTokenRepository.class);
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(4);
    private final CorreoService correo = mock(CorreoService.class);
    private final ClaveServiceImpl servicio = new ClaveServiceImpl(clientes, usuarios, tokens, encoder, correo,
            "http://localhost:4300/", 30, 60);

    private Usuario cliente(String clave) {
        Persona persona = Persona.builder().idPersona(7L).nombre("Ana").build();
        Rol rol = new Rol();
        rol.setNombre("CLIENTE");
        return Usuario.builder().idUsuario(70L).username("ana").password(encoder.encode(clave))
                .persona(persona).roles(new HashSet<>(Set.of(rol))).build();
    }

    @Test
    void correoNoRegistradoNoEnviaNadaNiFalla() {
        when(clientes.findByCorreoIgnoreCase("nadie@correo.com")).thenReturn(Optional.empty());

        servicio.solicitarRestablecimiento("  Nadie@Correo.com ");

        verify(tokens, never()).save(any());
        verify(correo, never()).enviarHtml(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    @SuppressWarnings("unchecked")
    void correoDeClienteGuardaSoloElHashYEnviaElEnlace() {
        Usuario usuario = cliente("ClaveVieja1");
        when(clientes.findByCorreoIgnoreCase("ana@correo.com"))
                .thenReturn(Optional.of(Cliente.builder().idPersona(7L).correo("ana@correo.com").build()));
        when(usuarios.findByPersonaIdPersona(7L)).thenReturn(Optional.of(usuario));
        when(tokens.findFirstByUsuarioIdUsuarioOrderByFechaCreacionDesc(70L)).thenReturn(Optional.empty());

        servicio.solicitarRestablecimiento("ana@correo.com");

        ArgumentCaptor<PasswordResetToken> guardado = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokens).save(guardado.capture());
        ArgumentCaptor<Map<String, Object>> variables = ArgumentCaptor.forClass(Map.class);
        verify(correo).enviarHtml(eq("ana@correo.com"), anyString(), eq("mail/restablecer-clave"), variables.capture());

        String enlace = (String) variables.getValue().get("enlace");
        assertThat(enlace).startsWith("http://localhost:4300/restablecer-clave?token=");
        String token = enlace.substring(enlace.indexOf("token=") + 6);
        assertThat(guardado.getValue().getTokenHash()).isEqualTo(ClaveServiceImpl.hash(token)).isNotEqualTo(token);
        assertThat(guardado.getValue().getFechaExpiracion()).isAfter(AHORA.plusMinutes(29));
    }

    @Test
    void noEnviaOtroCorreoAntesDeUnMinuto() {
        Usuario usuario = cliente("ClaveVieja1");
        PasswordResetToken anterior = new PasswordResetToken();
        anterior.setFechaCreacion(LocalDateTime.now(ZoneId.of("America/Lima")).minusSeconds(10));
        when(clientes.findByCorreoIgnoreCase("ana@correo.com"))
                .thenReturn(Optional.of(Cliente.builder().idPersona(7L).build()));
        when(usuarios.findByPersonaIdPersona(7L)).thenReturn(Optional.of(usuario));
        when(tokens.findFirstByUsuarioIdUsuarioOrderByFechaCreacionDesc(70L)).thenReturn(Optional.of(anterior));

        servicio.solicitarRestablecimiento("ana@correo.com");

        verify(tokens, never()).save(any());
        verify(correo, never()).enviarHtml(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void restableceConTokenVigenteYLoMarcaUsado() {
        Usuario usuario = cliente("ClaveVieja1");
        PasswordResetToken registro = new PasswordResetToken();
        registro.setUsuario(usuario);
        registro.setUsado(false);
        registro.setFechaExpiracion(AHORA.plusMinutes(10));
        when(tokens.findByTokenHash(ClaveServiceImpl.hash("tok"))).thenReturn(Optional.of(registro));

        servicio.restablecer("tok", "ClaveNueva2");

        assertThat(encoder.matches("ClaveNueva2", usuario.getPassword())).isTrue();
        assertThat(registro.getUsado()).isTrue();
        verify(tokens).invalidarPendientes(70L);
    }

    @Test
    void rechazaTokenVencidoOUsado() {
        PasswordResetToken vencido = new PasswordResetToken();
        vencido.setUsado(false);
        vencido.setFechaExpiracion(AHORA.minusMinutes(1));
        when(tokens.findByTokenHash(ClaveServiceImpl.hash("viejo"))).thenReturn(Optional.of(vencido));
        PasswordResetToken usado = new PasswordResetToken();
        usado.setUsado(true);
        usado.setFechaExpiracion(AHORA.plusMinutes(10));
        when(tokens.findByTokenHash(ClaveServiceImpl.hash("usado"))).thenReturn(Optional.of(usado));

        assertThatThrownBy(() -> servicio.restablecer("viejo", "ClaveNueva2"))
                .isInstanceOf(ReglaNegocioException.class).hasMessage(ClaveServiceImpl.TOKEN_INVALIDO);
        assertThatThrownBy(() -> servicio.restablecer("usado", "ClaveNueva2"))
                .isInstanceOf(ReglaNegocioException.class).hasMessage(ClaveServiceImpl.TOKEN_INVALIDO);
        assertThatThrownBy(() -> servicio.restablecer("no-existe", "ClaveNueva2"))
                .isInstanceOf(ReglaNegocioException.class).hasMessage(ClaveServiceImpl.TOKEN_INVALIDO);
    }

    @Test
    void cambiarExigeLaClaveActual() {
        Usuario usuario = cliente("ClaveVieja1");
        when(usuarios.findByUsernameConRoles("ana")).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> servicio.cambiar("ana", "incorrecta", "ClaveNueva2"))
                .isInstanceOf(ReglaNegocioException.class).hasMessage("La contraseña actual no es correcta");

        servicio.cambiar("ana", "ClaveVieja1", "ClaveNueva2");
        assertThat(encoder.matches("ClaveNueva2", usuario.getPassword())).isTrue();
    }
}
