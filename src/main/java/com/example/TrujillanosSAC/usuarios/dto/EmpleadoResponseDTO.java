package com.example.TrujillanosSAC.usuarios.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmpleadoResponseDTO {
    private Long personaId;
    private String nombre;
    private String apellido;
    private String numeroDocumento;
    private BigDecimal salario;
    private LocalDate fechaContratacion;
    private Long cargoId;
    private String cargo;
    private boolean tieneUsuario;
}
