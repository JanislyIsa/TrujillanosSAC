package com.example.TrujillanosSAC.usuarios.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TipoDocumentoResponseDTO {
    private Long id;
    private String nombre;
    private String abreviatura;
    private Integer longitud;
}
