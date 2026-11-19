package com.example.TrujillanosSAC.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ErrorResponse {
    private LocalDateTime fechaHora;
    private int codigoEstado;
    private String error;
    private String mensaje;
    private String ruta;
}
