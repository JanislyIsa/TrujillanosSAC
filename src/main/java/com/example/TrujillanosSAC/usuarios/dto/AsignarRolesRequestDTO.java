package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AsignarRolesRequestDTO {
    @NotEmpty private Set<@NotNull @Positive Long> rolIds;
}
