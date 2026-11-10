package com.example.TrujillanosSAC.auth.dto;

import java.util.List;

/** El frontend guarda el token y lo envía en cada petición como: Authorization: Bearer &lt;token&gt; */
public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEnSegundos,
        String username,
        String nombreCompleto,
        List<String> roles,
        List<String> permisos) {
}
