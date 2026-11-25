package com.example.TrujillanosSAC.inventario.dto.response;

import java.io.Serializable;

public record CatalogoResponse(
        Long id,
        String nombre
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
