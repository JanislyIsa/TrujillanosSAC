package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.RolRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.RolResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.RolMapper;
import com.example.TrujillanosSAC.usuarios.model.Rol;
import com.example.TrujillanosSAC.usuarios.repository.RolRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.usuarios.service.service.RolService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RolServiceImpl implements RolService {
    private final RolRepository repository; private final UsuarioRepository usuarioRepository; private final RolMapper mapper;
    public RolServiceImpl(RolRepository repository, UsuarioRepository usuarioRepository, RolMapper mapper) { this.repository=repository; this.usuarioRepository=usuarioRepository; this.mapper=mapper; }
    @Override @Transactional public RolResponseDTO create(RolRequestDTO d) {
        String nombre=d.getNombre().trim().toUpperCase(); if(repository.existsByNombreIgnoreCase(nombre)) throw new ReglaNegocioException("Ya existe el rol " + nombre);
        return mapper.toResponse(repository.save(Rol.builder().nombre(nombre).build()));
    }
    @Override @Transactional public RolResponseDTO update(Long id, RolRequestDTO d) {
        Rol r=buscar(id); String nombre=d.getNombre().trim().toUpperCase();
        if(repository.existsByNombreIgnoreCaseAndIdRolNot(nombre,id)) throw new ReglaNegocioException("Ya existe el rol " + nombre);
        r.setNombre(nombre); return mapper.toResponse(repository.save(r));
    }
    @Override @Transactional(readOnly=true) public RolResponseDTO read(Long id) { return mapper.toResponse(buscar(id)); }
    @Override @Transactional public void delete(Long id) { Rol r=buscar(id); if(usuarioRepository.existsByRolesIdRol(id)) throw new ReglaNegocioException("No se puede eliminar un rol asignado a usuarios"); repository.delete(r); }
    @Override @Transactional(readOnly=true) public Iterable<RolResponseDTO> readAll() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    private Rol buscar(Long id) { return repository.findById(id).orElseThrow(() -> new RecursosNoEncontradosException("Rol no encontrado: " + id)); }
}
