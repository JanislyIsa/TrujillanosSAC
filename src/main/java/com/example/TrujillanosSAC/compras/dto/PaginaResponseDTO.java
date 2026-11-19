package com.example.TrujillanosSAC.compras.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PaginaResponseDTO<T>(List<T> contenido, int pagina, int tamanio,
                                   long totalElementos, int totalPaginas, boolean ultima) {
    public static <T> PaginaResponseDTO<T> de(Page<T> resultado) {
        return new PaginaResponseDTO<>(resultado.getContent(), resultado.getNumber(), resultado.getSize(),
                resultado.getTotalElements(), resultado.getTotalPages(), resultado.isLast());
    }
}
