package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmpleadoRequestDTO {
    @NotNull @Positive private Long personaId;
    @NotNull @PositiveOrZero private BigDecimal salario;
    @NotNull private LocalDate fechaContratacion;
    @NotNull @Positive private Long cargoId;
}
