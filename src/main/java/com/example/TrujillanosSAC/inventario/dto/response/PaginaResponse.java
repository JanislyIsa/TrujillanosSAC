package com.example.TrujillanosSAC.inventario.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamanio,
        long totalElementos,
        int totalPaginas
) {
    public static <T> PaginaResponse<T> de(List<T> items, int pagina, int tamanio) {
        if (pagina < 0 || tamanio < 1 || tamanio > 100) {
            throw new IllegalArgumentException("Use pagina >= 0 y tamanio entre 1 y 100");
        }
        long inicio = (long) pagina * tamanio;
        return new PaginaResponse<>(
                items.subList((int) Math.min(inicio, items.size()),
                        (int) Math.min(inicio + tamanio, items.size())),
                pagina,
                tamanio,
                items.size(),
                (items.size() + tamanio - 1) / tamanio
        );
    }

    public static <T> PaginaResponse<T> de(Page<T> page) {
        return new PaginaResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
