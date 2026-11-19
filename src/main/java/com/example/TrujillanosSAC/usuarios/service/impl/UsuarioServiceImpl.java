package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.AsignarRolesRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.UsuarioRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.UsuarioResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.UsuarioMapper;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.PersonaRepository;
import com.example.TrujillanosSAC.usuarios.repository.RolRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.usuarios.service.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PersonaRepository personaRepository,
                              RolRepository rolRepository, PasswordEncoder passwordEncoder, UsuarioMapper mapper) {
        this.usuarioRepository = usuarioRepository; this.personaRepository = personaRepository;
        this.rolRepository = rolRepository; this.passwordEncoder = passwordEncoder; this.mapper = mapper;
    }

    @Override @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO request) {
        validarLongitudPassword(request.getPassword());
        String username = request.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) throw new ReglaNegocioException("El nombre de usuario ya está registrado");
        if (usuarioRepository.existsByPersonaIdPersona(request.getPersonaId()))
            throw new ReglaNegocioException("La persona ya tiene una cuenta de usuario");
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setPersona(personaRepository.findById(request.getPersonaId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Persona no encontrada: " + request.getPersonaId())));
        usuario.setRoles(resolverRoles(request.getRolIds()));
        return mapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO request) {
        validarLongitudPassword(request.getPassword());
        Usuario usuario = buscarEntidad(id);
        String username = request.getUsername().trim();
        if (usuarioRepository.existsByUsernameAndIdUsuarioNot(username, id))
            throw new ReglaNegocioException("El nombre de usuario ya está registrado");
        if (!usuario.getPersona().getIdPersona().equals(request.getPersonaId())
                && usuarioRepository.existsByPersonaIdPersona(request.getPersonaId()))
            throw new ReglaNegocioException("La persona ya tiene otra cuenta de usuario");
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setPersona(personaRepository.findById(request.getPersonaId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Persona no encontrada: " + request.getPersonaId())));
        usuario.setRoles(resolverRoles(request.getRolIds()));
        return mapper.toResponse(usuarioRepository.save(usuario));
    }

    @Override @Transactional(readOnly = true)
    public UsuarioResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) { usuarioRepository.delete(buscarEntidad(id)); }

    @Override @Transactional(readOnly = true)
    public Iterable<UsuarioResponseDTO> readAll() { return usuarioRepository.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional
    public UsuarioResponseDTO asignarRoles(Long id, AsignarRolesRequestDTO request) {
        Usuario usuario = buscarEntidad(id);
        usuario.setRoles(resolverRoles(request.getRolIds()));
        return mapper.toResponse(usuarioRepository.save(usuario));
    }

    private Usuario buscarEntidad(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RecursosNoEncontradosException("Usuario no encontrado: " + id));
    }
    private java.util.Set<com.example.TrujillanosSAC.usuarios.model.Rol> resolverRoles(java.util.Set<Long> ids) {
        var roles = rolRepository.findAllById(ids);
        if (roles.size() != ids.size()) throw new RecursosNoEncontradosException("Uno o más roles no existen");
        return new HashSet<>(roles);
    }
    private void validarLongitudPassword(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new ReglaNegocioException("La contraseña no puede superar 72 bytes");
    }
}
