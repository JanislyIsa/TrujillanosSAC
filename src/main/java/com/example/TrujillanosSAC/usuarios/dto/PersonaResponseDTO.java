package com.example.TrujillanosSAC.usuarios.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PersonaResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private Long tipoDocumentoId;
    private String tipoDocumento;
    private String numeroDocumento;
}
