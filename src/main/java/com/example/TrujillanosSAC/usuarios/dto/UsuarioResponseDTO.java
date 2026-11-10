package com.example.TrujillanosSAC.usuarios.dto;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private Long personaId;
    private String personaNombre;
    private List<String> roles;
}
