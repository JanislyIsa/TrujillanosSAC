package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.TipoDocumentoRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.TipoDocumentoResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.TipoDocumentoMapper;
import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import com.example.TrujillanosSAC.usuarios.repository.PersonaRepository;
import com.example.TrujillanosSAC.usuarios.repository.TipoDocumentoRepository;
import com.example.TrujillanosSAC.usuarios.service.service.TipoDocumentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoDocumentoServiceImpl implements TipoDocumentoService {
    private final TipoDocumentoRepository repository; private final PersonaRepository personaRepository; private final TipoDocumentoMapper mapper;
    public TipoDocumentoServiceImpl(TipoDocumentoRepository repository, PersonaRepository personaRepository, TipoDocumentoMapper mapper) { this.repository=repository; this.personaRepository=personaRepository; this.mapper=mapper; }
    @Override @Transactional public TipoDocumentoResponseDTO create(TipoDocumentoRequestDTO d) {
        String abreviatura=d.getAbreviatura().trim().toUpperCase(); String nombre=d.getNombre().trim();
        if(repository.existsByAbreviaturaIgnoreCase(abreviatura)) throw new ReglaNegocioException("La abreviatura del documento ya existe");
        if(repository.existsByNombreIgnoreCase(nombre)) throw new ReglaNegocioException("El tipo de documento ya existe");
        return mapper.toResponse(repository.save(TipoDocumento.builder().nombre(nombre).abreviatura(abreviatura).longitud(d.getLongitud()).build()));
    }
    @Override @Transactional public TipoDocumentoResponseDTO update(Long id, TipoDocumentoRequestDTO d) {
        TipoDocumento t=buscar(id); String abreviatura=d.getAbreviatura().trim().toUpperCase();
        if(repository.existsByAbreviaturaIgnoreCaseAndIdDocumentoNot(abreviatura,id)) throw new ReglaNegocioException("La abreviatura del documento ya existe");
        String nombre=d.getNombre().trim(); if(repository.existsByNombreIgnoreCaseAndIdDocumentoNot(nombre,id)) throw new ReglaNegocioException("El tipo de documento ya existe");
        t.setNombre(nombre); t.setAbreviatura(abreviatura); t.setLongitud(d.getLongitud()); return mapper.toResponse(repository.save(t));
    }
    @Override @Transactional(readOnly=true) public TipoDocumentoResponseDTO read(Long id) { return mapper.toResponse(buscar(id)); }
    @Override @Transactional public void delete(Long id) { TipoDocumento t=buscar(id); if(personaRepository.existsByDocumentoIdDocumento(id)) throw new ReglaNegocioException("No se puede eliminar un tipo de documento usado por personas"); repository.delete(t); }
    @Override @Transactional(readOnly=true) public Iterable<TipoDocumentoResponseDTO> readAll() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    private TipoDocumento buscar(Long id) { return repository.findById(id).orElseThrow(() -> new RecursosNoEncontradosException("Tipo de documento no encontrado: " + id)); }
}
