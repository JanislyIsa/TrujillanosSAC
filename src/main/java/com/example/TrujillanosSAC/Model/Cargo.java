package com.example.TrujillanosSAC.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CARGO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CARGO")
    private Long idCargo;

    @NotBlank
    @Column(name = "NOMBRE", length = 50, nullable = false, unique = true)
    private String nombre;
}
