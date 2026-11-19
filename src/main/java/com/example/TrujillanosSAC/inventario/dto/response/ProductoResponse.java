package com.example.TrujillanosSAC.inventario.dto.response;

import com.example.TrujillanosSAC.inventario.model.EstadoStock;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import java.math.BigDecimal;

public record ProductoResponse(
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
    public static ProductoResponse de(ItemResponse item) {
        return new ProductoResponse(
                item.id(),
                item.tipoItem(),
                item.nombre(),
                item.descripcion(),
                item.precio(),
                item.stock(),
                item.stockMinimo(),
                item.stockLimite(),
                item.estadoStock(),
                item.idAlmacen(),
                item.almacen(),
                item.idCategoria(),
                item.categoria(),
                item.idUnidad(),
                item.unidad(),
                item.imagenUrl(),
                item.activo()
        );
    }
}
