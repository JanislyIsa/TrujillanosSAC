package com.example.TrujillanosSAC.inventario.dto.response;

import com.example.TrujillanosSAC.inventario.model.TipoItem;
import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        String codigo,
        TipoItem tipoItem,
        Long idItem,
        String tipoAlerta,
        String descripcion,
        Integer stockActual,
        Integer stockMinimo,
        Integer stockLimite,
        LocalDateTime fechaHora,
        LocalDateTime resueltaEn
) {
}
