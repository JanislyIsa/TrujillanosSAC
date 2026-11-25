package com.example.TrujillanosSAC.inventario.service.service;
import com.example.TrujillanosSAC.inventario.dto.request.InsumoRequest;
import com.example.TrujillanosSAC.inventario.dto.request.ProductoRequest;
import com.example.TrujillanosSAC.inventario.dto.response.ItemResponse;
import com.example.TrujillanosSAC.inventario.model.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogoInventarioService {
    List<ItemResponse> listar(TipoItem tipo, String buscar, Long almacen, EstadoStock estado);
    Page<ItemResponse> listarPagina(TipoItem tipo, String buscar, Long almacen, Boolean activo,
                                    EstadoStock estado, Pageable pageable);
    ItemResponse obtener(TipoItem tipo, Long id);
    ItemResponse crearProducto(ProductoRequest request, String responsable);
    ItemResponse editarProducto(Long id, ProductoRequest request);
    ItemResponse crearInsumo(InsumoRequest request, String responsable);
    ItemResponse editarInsumo(Long id, InsumoRequest request);
    void eliminar(TipoItem tipo, Long id);
}
