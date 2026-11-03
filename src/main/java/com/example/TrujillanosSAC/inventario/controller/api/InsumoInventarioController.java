package com.example.TrujillanosSAC.inventario.controller.api;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.service.CatalogoInventarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.security.Principal;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/insumos")
@PreAuthorize("@inventarioPermisos.inventario(authentication)")
public class InsumoInventarioController {
    private final CatalogoInventarioService catalogo;
    @GetMapping
    public PaginaResponse<ItemResponse> listar(@RequestParam(defaultValue="") String buscar,
            @RequestParam(required=false) Long idAlmacen, @RequestParam(required=false) Boolean activo, @RequestParam(required=false) EstadoStock estado,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanio) {
        return PaginaResponse.de(catalogo.listar(TipoItem.INSUMO,buscar,idAlmacen,estado).stream().filter(i -> activo==null || i.activo()==activo).toList(),pagina,tamanio);
    }
    @GetMapping("/{id}")
    public ItemResponse obtener(@PathVariable @Positive Long id) { return catalogo.obtener(TipoItem.INSUMO,id); }
    @PostMapping
    public ResponseEntity<ItemResponse> crear(@Valid @RequestBody InsumoRequest request, Principal principal) {
        ItemResponse item = catalogo.crearInsumo(request,principal.getName());
        return ResponseEntity.created(URI.create("/api/v1/inventario/insumos/"+item.id())).body(item);
    }
    @PutMapping("/{id}")
    public ItemResponse editar(@PathVariable @Positive Long id, @Valid @RequestBody InsumoRequest request) {
        return catalogo.editarInsumo(id,request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        catalogo.eliminar(TipoItem.INSUMO,id); return ResponseEntity.noContent().build();
    }
}
