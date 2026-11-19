package com.example.TrujillanosSAC.usuarios.service.service;

import com.example.TrujillanosSAC.usuarios.dto.AsignarRolesRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.UsuarioRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.UsuarioResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.generic.CrudService;

public interface UsuarioService extends CrudService<UsuarioRequestDTO, UsuarioResponseDTO, Long> {
    UsuarioResponseDTO asignarRoles(Long id, AsignarRolesRequestDTO request);
}
