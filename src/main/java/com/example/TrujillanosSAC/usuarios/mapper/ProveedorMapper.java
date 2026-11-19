package com.example.TrujillanosSAC.usuarios.mapper;

import com.example.TrujillanosSAC.usuarios.dto.ProveedorResponseDTO;
import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {
    private final UsuarioRepository usuarioRepository;
    public ProveedorMapper(UsuarioRepository usuarioRepository) { this.usuarioRepository = usuarioRepository; }
    public ProveedorResponseDTO toResponse(Proveedor proveedor) {
        var p = proveedor.getPersona();
        return new ProveedorResponseDTO(p.getIdPersona(), p.getNombre(), p.getApellido(), p.getNumDoc(),
                proveedor.getCorreo(), proveedor.getDireccion(), usuarioRepository.existsByPersonaIdPersona(p.getIdPersona()));
    }
}
