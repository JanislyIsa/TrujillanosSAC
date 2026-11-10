package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.RolResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Rol;
import org.springframework.stereotype.Component;

@Component
public class RolMapper {
    public RolResponseDTO toResponse(Rol rol) { return new RolResponseDTO(rol.getIdRol(), rol.getNombre()); }
}
