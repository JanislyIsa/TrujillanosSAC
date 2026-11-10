package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.ClienteResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {
    private final UsuarioRepository usuarioRepository;
    public ClienteMapper(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }
    public ClienteResponseDTO toResponse(Cliente cliente) {
        var p = cliente.getPersona();
        return new ClienteResponseDTO(p.getIdPersona(), p.getNombre(), p.getApellido(), p.getNumDoc(),
                cliente.getFechaRegistro(), usuarioRepository.existsByPersonaIdPersona(p.getIdPersona()));
    }
}
