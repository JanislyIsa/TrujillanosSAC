package com.example.TrujillanosSAC.usuarios.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClienteResponseDTO {
    private Long personaId;
    private String nombre;
    private String apellido;
    private String numeroDocumento;
    private LocalDate fechaRegistro;
    private boolean tieneUsuario;
}
