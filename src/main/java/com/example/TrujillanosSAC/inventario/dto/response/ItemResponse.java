package com.example.TrujillanosSAC.inventario.dto.response;

import com.example.TrujillanosSAC.inventario.model.EstadoStock;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import java.math.BigDecimal;

public record ItemResponse(
        Long id,
        TipoItem tipoItem,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        Integer stockMinimo,
        Integer stockLimite,
        EstadoStock estadoStock,
        Long idAlmacen,
        String almacen,
        Long idCategoria,
        String categoria,
        Long idUnidad,
        String unidad,
        String imagenUrl,
        boolean activo
) {
}
