package com.example.TrujillanosSAC.inventario.service;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.model.*;
import java.util.List;

public interface CatalogoInventarioService {
    List<ItemResponse> listar(TipoItem tipo, String buscar, Long almacen, EstadoStock estado);
    ItemResponse obtener(TipoItem tipo, Long id);
    ItemResponse crearProducto(ProductoRequest request, String responsable);
    ItemResponse editarProducto(Long id, ProductoRequest request);
    ItemResponse crearInsumo(InsumoRequest request, String responsable);
    ItemResponse editarInsumo(Long id, InsumoRequest request);
    void eliminar(TipoItem tipo, Long id);
}
