package com.example.TrujillanosSAC.auditoria.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PaginaAuditoriaDTO<T>(List<T> contenido, int pagina, int tamanio,
                                    long totalElementos, int totalPaginas, boolean ultima) {
    public static <T> PaginaAuditoriaDTO<T> de(Page<T> page) {
        return new PaginaAuditoriaDTO<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}
