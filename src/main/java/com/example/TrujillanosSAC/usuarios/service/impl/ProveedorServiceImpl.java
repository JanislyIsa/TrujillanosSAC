package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.ProveedorRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.ProveedorResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.ProveedorMapper;
import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.repository.PersonaRepository;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.usuarios.service.service.ProveedorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProveedorServiceImpl implements ProveedorService {
    private final ProveedorRepository repository; private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository; private final ProveedorMapper mapper;
    public ProveedorServiceImpl(ProveedorRepository repository, PersonaRepository personaRepository,
                                UsuarioRepository usuarioRepository, ProveedorMapper mapper) {
        this.repository=repository; this.personaRepository=personaRepository; this.usuarioRepository=usuarioRepository; this.mapper=mapper;
    }
    @Override @Transactional public ProveedorResponseDTO create(ProveedorRequestDTO d) {
        if(repository.existsById(d.getPersonaId())) throw new ReglaNegocioException("La persona ya está registrada como proveedor");
        String correo=d.getCorreo().trim().toLowerCase(); if(repository.existsByCorreoIgnoreCase(correo)) throw new ReglaNegocioException("El correo ya está registrado para otro proveedor");
        Proveedor p=new Proveedor(); p.setPersona(personaRepository.findById(d.getPersonaId()).orElseThrow(() -> new RecursosNoEncontradosException("Persona no encontrada: " + d.getPersonaId())));
        p.setCorreo(correo); p.setDireccion(d.getDireccion().trim()); return mapper.toResponse(repository.save(p));
    }
    @Override @Transactional public ProveedorResponseDTO update(Long id, ProveedorRequestDTO d) {
        Proveedor p=buscar(id); if(!id.equals(d.getPersonaId())) throw new ReglaNegocioException("No se puede cambiar la persona asociada al perfil de proveedor");
        String correo=d.getCorreo().trim().toLowerCase(); if(repository.existsByCorreoIgnoreCaseAndIdPersonaNot(correo,id)) throw new ReglaNegocioException("El correo ya está registrado para otro proveedor");
        p.setCorreo(correo); p.setDireccion(d.getDireccion().trim()); return mapper.toResponse(repository.save(p));
    }
    @Override @Transactional(readOnly=true) public ProveedorResponseDTO read(Long id) { return mapper.toResponse(buscar(id)); }
    @Override @Transactional public void delete(Long id) {
        Proveedor p=buscar(id); if(usuarioRepository.existsByPersonaIdPersona(id)) throw new ReglaNegocioException("El proveedor tiene una cuenta de usuario; elimine la cuenta antes de eliminar el perfil");
        repository.delete(p);
    }
    @Override @Transactional(readOnly=true) public Iterable<ProveedorResponseDTO> readAll() { return repository.findAllConPersona().stream().map(mapper::toResponse).toList(); }
    private Proveedor buscar(Long id) { return repository.findByIdPersona(id).orElseThrow(() -> new RecursosNoEncontradosException("Proveedor no encontrado: " + id)); }
}
