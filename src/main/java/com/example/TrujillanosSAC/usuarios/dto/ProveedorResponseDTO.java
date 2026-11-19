package com.example.TrujillanosSAC.usuarios.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProveedorResponseDTO {
    private Long personaId;
    private String nombre;
    private String apellido;
    private String numeroDocumento;
    private String correo;
    private String direccion;
    private boolean tieneUsuario;
}
