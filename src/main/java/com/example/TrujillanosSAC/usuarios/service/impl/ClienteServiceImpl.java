package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.ClienteRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.ClienteResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.ClienteMapper;
import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.repository.ClienteRepository;
import com.example.TrujillanosSAC.usuarios.repository.PersonaRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.usuarios.service.service.ClienteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository; private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository; private final ClienteMapper mapper;
    public ClienteServiceImpl(ClienteRepository repository, PersonaRepository personaRepository,
                              UsuarioRepository usuarioRepository, ClienteMapper mapper) {
        this.repository=repository; this.personaRepository=personaRepository; this.usuarioRepository=usuarioRepository; this.mapper=mapper;
    }
    @Override @Transactional public ClienteResponseDTO create(ClienteRequestDTO d) {
        if (repository.existsById(d.getPersonaId())) throw new ReglaNegocioException("La persona ya está registrada como cliente");
        Cliente c = new Cliente(); c.setPersona(personaRepository.findById(d.getPersonaId()).orElseThrow(() -> new RecursosNoEncontradosException("Persona no encontrada: " + d.getPersonaId())));
        c.setFechaRegistro(LocalDate.now(java.time.ZoneId.of("America/Lima"))); return mapper.toResponse(repository.save(c));
    }
    @Override @Transactional public ClienteResponseDTO update(Long id, ClienteRequestDTO d) {
        Cliente c=buscar(id); if(!id.equals(d.getPersonaId())) throw new ReglaNegocioException("No se puede cambiar la persona asociada al perfil de cliente");
        return mapper.toResponse(c);
    }
    @Override @Transactional(readOnly=true) public ClienteResponseDTO read(Long id) { return mapper.toResponse(buscar(id)); }
    @Override @Transactional public void delete(Long id) {
        Cliente c=buscar(id); if(usuarioRepository.existsByPersonaIdPersona(id)) throw new ReglaNegocioException("El cliente tiene una cuenta de usuario; elimine la cuenta antes de eliminar el perfil");
        repository.delete(c);
    }
    @Override @Transactional(readOnly=true) public Iterable<ClienteResponseDTO> readAll() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    private Cliente buscar(Long id) { return repository.findById(id).orElseThrow(() -> new RecursosNoEncontradosException("Cliente no encontrado: " + id)); }
}
