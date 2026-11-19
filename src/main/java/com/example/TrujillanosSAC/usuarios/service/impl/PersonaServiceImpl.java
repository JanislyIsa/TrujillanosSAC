package com.example.TrujillanosSAC.usuarios.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.dto.PersonaRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.PersonaResponseDTO;
import com.example.TrujillanosSAC.usuarios.mapper.PersonaMapper;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.repository.*;
import com.example.TrujillanosSAC.usuarios.service.service.PersonaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ClienteRepository clienteRepository;
    private final ProveedorRepository proveedorRepository;
    private final PersonaMapper mapper;

    public PersonaServiceImpl(PersonaRepository personaRepository, TipoDocumentoRepository tipoDocumentoRepository,
                              UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository,
                              ClienteRepository clienteRepository, ProveedorRepository proveedorRepository,
                              PersonaMapper mapper) {
        this.personaRepository = personaRepository; this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.usuarioRepository = usuarioRepository; this.empleadoRepository = empleadoRepository;
        this.clienteRepository = clienteRepository; this.proveedorRepository = proveedorRepository; this.mapper = mapper;
    }

    @Override @Transactional
    public PersonaResponseDTO create(PersonaRequestDTO request) {
        String numero = request.getNumeroDocumento().trim();
        if (personaRepository.existsByNumDoc(numero)) throw new ReglaNegocioException("Ya existe una persona con ese número de documento");
        var tipo = tipoDocumentoRepository.findById(request.getTipoDocumentoId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Tipo de documento no encontrado: " + request.getTipoDocumentoId()));
        validarLongitud(numero, tipo.getLongitud());
        Persona persona = new Persona();
        aplicar(persona, request, tipo);
        return mapper.toResponse(personaRepository.save(persona));
    }

    @Override @Transactional
    public PersonaResponseDTO update(Long id, PersonaRequestDTO request) {
        Persona persona = buscarEntidad(id);
        String numero = request.getNumeroDocumento().trim();
        if (personaRepository.existsByNumDocAndIdPersonaNot(numero, id))
            throw new ReglaNegocioException("Ya existe otra persona con ese número de documento");
        var tipo = tipoDocumentoRepository.findById(request.getTipoDocumentoId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Tipo de documento no encontrado: " + request.getTipoDocumentoId()));
        validarLongitud(numero, tipo.getLongitud());
        aplicar(persona, request, tipo);
        return mapper.toResponse(personaRepository.save(persona));
    }

    @Override @Transactional(readOnly = true)
    public PersonaResponseDTO read(Long id) { return mapper.toResponse(buscarEntidad(id)); }

    @Override @Transactional
    public void delete(Long id) {
        Persona persona = buscarEntidad(id);
        if (usuarioRepository.existsByPersonaIdPersona(id) || empleadoRepository.existsById(id)
                || clienteRepository.existsById(id) || proveedorRepository.existsById(id))
            throw new ReglaNegocioException("No se puede eliminar una persona con cuenta o perfiles asociados");
        personaRepository.delete(persona);
    }

    @Override @Transactional(readOnly = true)
    public Iterable<PersonaResponseDTO> readAll() { return personaRepository.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional(readOnly = true)
    public Optional<PersonaResponseDTO> buscarPorNumeroDocumento(String numeroDocumento) {
        return personaRepository.findByNumDoc(numeroDocumento.trim()).map(mapper::toResponse);
    }

    private Persona buscarEntidad(Long id) {
        return personaRepository.findById(id).orElseThrow(() -> new RecursosNoEncontradosException("Persona no encontrada: " + id));
    }
    private void aplicar(Persona p, PersonaRequestDTO d, com.example.TrujillanosSAC.usuarios.model.TipoDocumento t) {
        p.setNombre(d.getNombre().trim()); p.setApellido(d.getApellido().trim());
        p.setTelefono(d.getTelefono().trim()); p.setNumDoc(d.getNumeroDocumento().trim()); p.setDocumento(t);
    }
    private void validarLongitud(String numero, Integer longitud) {
        if (longitud != null && numero.length() != longitud)
            throw new ReglaNegocioException("El documento debe tener " + longitud + " caracteres");
    }
}
