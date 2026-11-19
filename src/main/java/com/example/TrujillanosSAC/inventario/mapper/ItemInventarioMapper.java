package com.example.TrujillanosSAC.inventario.mapper;

import com.example.TrujillanosSAC.inventario.dto.response.ItemResponse;
import com.example.TrujillanosSAC.inventario.model.EstadoStock;
import com.example.TrujillanosSAC.inventario.model.Insumo;
import com.example.TrujillanosSAC.inventario.model.Producto;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import org.springframework.stereotype.Component;

/** Convierte las entidades del catálogo a la respuesta compartida de inventario. */
@Component
public class ItemInventarioMapper {
    public ItemResponse aRespuesta(Producto producto) {
        return new ItemResponse(producto.getIdProducto(), TipoItem.PRODUCTO, producto.getNombre(),
                producto.getDescripcion(), producto.getPrecio(), producto.getStock(),
                producto.getStockMinimo(), producto.getStockLimite(),
                EstadoStock.calcular(producto.getStock(), producto.getStockMinimo(), producto.getStockLimite()),
                producto.getAlmacen().getIdAlmacen(), producto.getAlmacen().getNombre(),
                producto.getCategoria().getIdCategoria(), producto.getCategoria().getNombre(),
                producto.getPresentacion().getIdPresentacion(), producto.getPresentacion().getNombre(),
                producto.getImagenUrl(), Boolean.TRUE.equals(producto.getActivo()));
    }

    public ItemResponse aRespuesta(Insumo insumo) {
        return new ItemResponse(insumo.getIdInsumo(), TipoItem.INSUMO, insumo.getNombre(), null, null,
                insumo.getStock(), insumo.getStockMinimo(), insumo.getStockLimite(),
                EstadoStock.calcular(insumo.getStock(), insumo.getStockMinimo(), insumo.getStockLimite()),
                insumo.getAlmacen().getIdAlmacen(), insumo.getAlmacen().getNombre(), null, null,
                insumo.getUnidadMedida().getIdUnidadMedida(), insumo.getUnidadMedida().getNombre(),
                null, Boolean.TRUE.equals(insumo.getActivo()));
    }
}
