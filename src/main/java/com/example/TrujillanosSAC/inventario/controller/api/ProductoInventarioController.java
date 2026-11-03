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
@RequestMapping("/api/v1/inventario/productos")
@PreAuthorize("@inventarioPermisos.inventario(authentication)")
public class ProductoInventarioController {
    private final CatalogoInventarioService catalogo;
    @GetMapping
    public PaginaResponse<ItemResponse> listar(@RequestParam(defaultValue="") String buscar,
            @RequestParam(required=false) Long idAlmacen, @RequestParam(required=false) Boolean activo, @RequestParam(required=false) EstadoStock estado,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanio) {
        return PaginaResponse.de(catalogo.listar(TipoItem.PRODUCTO,buscar,idAlmacen,estado).stream().filter(i -> activo==null || i.activo()==activo).toList(),pagina,tamanio);
    }
    @GetMapping("/{id}")
    public ItemResponse obtener(@PathVariable @Positive Long id) { return catalogo.obtener(TipoItem.PRODUCTO,id); }
    @PostMapping
    public ResponseEntity<ItemResponse> crear(@Valid @RequestBody ProductoRequest request, Principal principal) {
        ItemResponse item = catalogo.crearProducto(request,principal.getName());
        return ResponseEntity.created(URI.create("/api/v1/inventario/productos/"+item.id())).body(item);
    }
    @PutMapping("/{id}")
    public ItemResponse editar(@PathVariable @Positive Long id, @Valid @RequestBody ProductoRequest request) {
        return catalogo.editarProducto(id,request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        catalogo.eliminar(TipoItem.PRODUCTO,id); return ResponseEntity.noContent().build();
    }
}
