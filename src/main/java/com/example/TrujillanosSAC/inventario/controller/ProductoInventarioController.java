package com.example.TrujillanosSAC.inventario.controller;
import com.example.TrujillanosSAC.inventario.dto.request.ProductoRequest;
import com.example.TrujillanosSAC.inventario.dto.response.PaginaResponse;
import com.example.TrujillanosSAC.inventario.dto.response.ProductoResponse;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.service.service.CatalogoInventarioService;
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
@PreAuthorize("hasRole('EMPLEADO')")
public class ProductoInventarioController {
    private final CatalogoInventarioService catalogo;
    @GetMapping
    public PaginaResponse<ProductoResponse> listar(@RequestParam(defaultValue="") String buscar,
            @RequestParam(required=false) Long idAlmacen, @RequestParam(required=false) Boolean activo, @RequestParam(required=false) EstadoStock estado,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanio) {
        var resultados = catalogo.listar(TipoItem.PRODUCTO, buscar, idAlmacen, estado).stream()
                .filter(item -> activo == null || item.activo() == activo)
                .map(ProductoResponse::de)
                .toList();
        return PaginaResponse.de(resultados, pagina, tamanio);
    }
    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable @Positive Long id) {
        return ProductoResponse.de(catalogo.obtener(TipoItem.PRODUCTO, id));
    }
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request, Principal principal) {
        ProductoResponse item = ProductoResponse.de(catalogo.crearProducto(request, principal.getName()));
        return ResponseEntity.created(URI.create("/api/v1/inventario/productos/" + item.id())).body(item);
    }
    @PutMapping("/{id}")
    public ProductoResponse editar(@PathVariable @Positive Long id, @Valid @RequestBody ProductoRequest request) {
        return ProductoResponse.de(catalogo.editarProducto(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        catalogo.eliminar(TipoItem.PRODUCTO,id); return ResponseEntity.noContent().build();
    }
}
