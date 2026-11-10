package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ClienteRequestDTO {
    @NotNull @Positive private Long personaId;
}
