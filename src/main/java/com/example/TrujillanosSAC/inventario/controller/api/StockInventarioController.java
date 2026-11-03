package com.example.TrujillanosSAC.inventario.controller.api;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.service.CatalogoInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/stock")
@PreAuthorize("@inventarioPermisos.lectura(authentication)")
public class StockInventarioController {
    private final CatalogoInventarioService catalogo;
    @GetMapping
    public PaginaResponse<ItemResponse> listar(@RequestParam(required=false) TipoItem tipoItem,
            @RequestParam(required=false) EstadoStock estado, @RequestParam(required=false) Long idAlmacen,
            @RequestParam(defaultValue="") String buscar, @RequestParam(defaultValue="0") int pagina,
            @RequestParam(defaultValue="20") int tamanio) {
        return PaginaResponse.de(catalogo.listar(tipoItem,buscar,idAlmacen,estado),pagina,tamanio);
    }
}
