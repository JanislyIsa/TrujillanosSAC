package com.example.TrujillanosSAC.inventario.controller;
import com.example.TrujillanosSAC.inventario.dto.request.InsumoRequest;
import com.example.TrujillanosSAC.inventario.dto.response.InsumoResponse;
import com.example.TrujillanosSAC.inventario.dto.response.PaginaResponse;
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
@RequestMapping("/api/v1/inventario/insumos")
@PreAuthorize("hasRole('EMPLEADO')")
public class InsumoInventarioController {
    private final CatalogoInventarioService catalogo;
    @GetMapping
    public PaginaResponse<InsumoResponse> listar(@RequestParam(defaultValue="") String buscar,
            @RequestParam(required=false) Long idAlmacen, @RequestParam(required=false) Boolean activo, @RequestParam(required=false) EstadoStock estado,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanio) {
        var resultados = catalogo.listar(TipoItem.INSUMO, buscar, idAlmacen, activo, estado).stream()
                .map(InsumoResponse::de)
                .toList();
        return PaginaResponse.de(resultados, pagina, tamanio);
    }
    @GetMapping("/{id}")
    public InsumoResponse obtener(@PathVariable @Positive Long id) {
        return InsumoResponse.de(catalogo.obtener(TipoItem.INSUMO, id));
    }
    @PostMapping
    public ResponseEntity<InsumoResponse> crear(@Valid @RequestBody InsumoRequest request, Principal principal) {
        InsumoResponse item = InsumoResponse.de(catalogo.crearInsumo(request, principal.getName()));
        return ResponseEntity.created(URI.create("/api/v1/inventario/insumos/" + item.id())).body(item);
    }
    @PutMapping("/{id}")
    public InsumoResponse editar(@PathVariable @Positive Long id, @Valid @RequestBody InsumoRequest request) {
        return InsumoResponse.de(catalogo.editarInsumo(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        catalogo.eliminar(TipoItem.INSUMO,id); return ResponseEntity.noContent().build();
    }
}
