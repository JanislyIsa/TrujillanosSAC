package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.EmpleadoResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Empleado;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {
    private final UsuarioRepository usuarioRepository;
    public EmpleadoMapper(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }
    public EmpleadoResponseDTO toResponse(Empleado empleado) {
        var p = empleado.getPersona();
        return new EmpleadoResponseDTO(p.getIdPersona(), p.getNombre(), p.getApellido(), p.getNumDoc(),
                empleado.getSalario(), empleado.getFechaContratacion(), empleado.getCargo().getIdCargo(),
                empleado.getCargo().getNombre(), usuarioRepository.existsByPersonaIdPersona(p.getIdPersona()));
    }
}
