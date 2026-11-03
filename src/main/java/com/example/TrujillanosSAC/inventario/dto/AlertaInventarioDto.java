package com.example.TrujillanosSAC.inventario.dto;

import java.time.LocalDateTime;

public record AlertaInventarioDto(
        String codigoAlerta,
        String tipoAlerta,
        String tipoItem,
        String nombre,
        Integer stockActual,
        Integer stockMinimo,
        Integer stockLimite,
        String unidad,
        LocalDateTime fechaHora
) {
}
