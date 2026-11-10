package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.PersonaResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import org.springframework.stereotype.Component;

@Component
public class PersonaMapper {
    public PersonaResponseDTO toResponse(Persona persona) {
        return new PersonaResponseDTO(persona.getIdPersona(), persona.getNombre(), persona.getApellido(),
                persona.getTelefono(), persona.getDocumento().getIdDocumento(),
                persona.getDocumento().getNombre(), persona.getNumDoc());
    }
}
