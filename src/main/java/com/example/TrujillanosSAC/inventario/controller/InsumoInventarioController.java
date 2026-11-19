package com.example.TrujillanosSAC.inventario.controller;

import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.InsumoRequest;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.ItemResponse;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.PaginaResponse;
import com.example.TrujillanosSAC.inventario.model.EstadoStock;
import com.example.TrujillanosSAC.inventario.model.TipoItem;
import com.example.TrujillanosSAC.inventario.service.service.CatalogoInventarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/insumos")
@PreAuthorize("hasRole('EMPLEADO')")
public class InsumoInventarioController {
    private final CatalogoInventarioService catalogo;

    @GetMapping
    public PaginaResponse<ItemResponse> listar(@RequestParam(defaultValue="") String buscar,
                                               @RequestParam(required=false) Long idAlmacen,
                                               @RequestParam(required=false) Boolean activo,
                                               @RequestParam(required=false) EstadoStock estado,
                                               @RequestParam(defaultValue="0") int pagina,
                                               @RequestParam(defaultValue="20") int tamanio) {
        var items = catalogo.listar(TipoItem.INSUMO, buscar, idAlmacen, estado).stream()
                .filter(item -> activo == null || item.activo() == activo)
                .toList();
        return PaginaResponse.de(items, pagina, tamanio);
    }

    @GetMapping("/{id}")
    public ItemResponse obtener(@PathVariable @Positive Long id) {
        return catalogo.obtener(TipoItem.INSUMO, id);
    }

    @PostMapping
    public ResponseEntity<ItemResponse> crear(@Valid @RequestBody InsumoRequest request, Principal principal) {
        ItemResponse item = catalogo.crearInsumo(request, principal.getName());
        URI location = URI.create("/api/v1/inventario/insumos/" + item.id());
        return ResponseEntity.created(location).body(item);
    }

    @PutMapping("/{id}")
    public ItemResponse editar(@PathVariable @Positive Long id, @Valid @RequestBody InsumoRequest request) {
        return catalogo.editarInsumo(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        catalogo.eliminar(TipoItem.INSUMO, id);
        return ResponseEntity.noContent().build();
    }
}
