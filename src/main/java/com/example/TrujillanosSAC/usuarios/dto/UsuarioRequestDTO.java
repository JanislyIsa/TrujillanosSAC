package com.example.TrujillanosSAC.usuarios.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank @Size(min = 4, max = 20)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$")
    private String username;

    @NotBlank @Size(min = 8, max = 72)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$")
    private String password;

    @NotNull @Positive private Long personaId;
    @NotEmpty private Set<@NotNull @Positive Long> rolIds;
}
