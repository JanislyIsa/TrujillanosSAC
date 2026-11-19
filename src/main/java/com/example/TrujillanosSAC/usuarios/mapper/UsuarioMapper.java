package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.UsuarioResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public UsuarioResponseDTO toResponse(Usuario usuario) {
        var persona = usuario.getPersona();
        return new UsuarioResponseDTO(usuario.getIdUsuario(), usuario.getUsername(), persona.getIdPersona(),
                persona.getNombre() + " " + persona.getApellido(),
                usuario.getRoles().stream().map(r -> r.getNombre()).sorted().toList());
    }
}
