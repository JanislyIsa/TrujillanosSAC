package com.example.TrujillanosSAC.compras.util;

import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Set;

public final class PaginacionComprasUtil {
    private PaginacionComprasUtil() {}

    public static Pageable construir(int pagina, int tamanio, String ordenarPor, String direccion,
                                     Set<String> camposOrdenables, String campoPorDefecto) {
        if (pagina < 0) throw new ReglaNegocioException("La página debe ser mayor o igual a cero");
        if (tamanio < 1 || tamanio > 100) throw new ReglaNegocioException("El tamaño debe estar entre 1 y 100");
        String campo = ordenarPor == null || ordenarPor.isBlank() ? campoPorDefecto : ordenarPor.trim();
        if (!camposOrdenables.contains(campo)) throw new ReglaNegocioException("Campo de orden no válido");
        Sort.Direction sentido;
        try { sentido = Sort.Direction.fromString(direccion == null || direccion.isBlank() ? "desc" : direccion.trim()); }
        catch (IllegalArgumentException ex) { throw new ReglaNegocioException("Dirección no válida. Use asc o desc"); }
        return PageRequest.of(pagina, tamanio, Sort.by(sentido, campo));
    }

    public static String normalizarBusqueda(String buscar) {
        return buscar == null ? "" : buscar.trim().replaceAll("(?i)\\b(co|sc|or)_0+(\\d+)\\b", "$1_$2");
    }
}
