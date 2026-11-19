package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.EmpleadoRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.EmpleadoResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.EmpleadoMapper;
import com.example.TrujillanosSAC.usuarios.model.Empleado;
import com.example.TrujillanosSAC.usuarios.repository.*;
import com.example.TrujillanosSAC.usuarios.service.service.EmpleadoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository repository;
    private final PersonaRepository personaRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoMapper mapper;
    public EmpleadoServiceImpl(EmpleadoRepository repository, PersonaRepository personaRepository,
                               CargoRepository cargoRepository, UsuarioRepository usuarioRepository, EmpleadoMapper mapper) {
        this.repository = repository; this.personaRepository = personaRepository; this.cargoRepository = cargoRepository;
        this.usuarioRepository = usuarioRepository; this.mapper = mapper;
    }
    @Override @Transactional
    public EmpleadoResponseDTO create(EmpleadoRequestDTO d) {
        if (repository.existsById(d.getPersonaId())) throw new ReglaNegocioException("La persona ya está registrada como empleado");
        Empleado e = new Empleado();
        e.setPersona(personaRepository.findById(d.getPersonaId()).orElseThrow(() -> new RecursosNoEncontradosException("Persona no encontrada: " + d.getPersonaId())));
        e.setCargo(cargoRepository.findById(d.getCargoId()).orElseThrow(() -> new RecursosNoEncontradosException("Cargo no encontrado: " + d.getCargoId())));
        e.setSalario(d.getSalario()); e.setFechaContratacion(d.getFechaContratacion());
        return mapper.toResponse(repository.save(e));
    }
    @Override @Transactional
    public EmpleadoResponseDTO update(Long id, EmpleadoRequestDTO d) {
        Empleado e = buscar(id);
        if (!id.equals(d.getPersonaId())) throw new ReglaNegocioException("No se puede cambiar la persona asociada al perfil de empleado");
        e.setCargo(cargoRepository.findById(d.getCargoId()).orElseThrow(() -> new RecursosNoEncontradosException("Cargo no encontrado: " + d.getCargoId())));
        e.setSalario(d.getSalario()); e.setFechaContratacion(d.getFechaContratacion());
        return mapper.toResponse(repository.save(e));
    }
    @Override @Transactional(readOnly = true) public EmpleadoResponseDTO read(Long id) { return mapper.toResponse(buscar(id)); }
    @Override @Transactional public void delete(Long id) {
        Empleado e = buscar(id);
        if (usuarioRepository.existsByPersonaIdPersona(id)) throw new ReglaNegocioException("El empleado tiene una cuenta de usuario; desactive o reasigne la cuenta antes de eliminar su perfil");
        repository.delete(e);
    }
    @Override @Transactional(readOnly = true)
    public Iterable<EmpleadoResponseDTO> readAll() { return repository.findAllConCargo().stream().map(mapper::toResponse).toList(); }
    private Empleado buscar(Long id) { return repository.findByIdPersonaConCargo(id).orElseThrow(() -> new RecursosNoEncontradosException("Empleado no encontrado: " + id)); }
}
