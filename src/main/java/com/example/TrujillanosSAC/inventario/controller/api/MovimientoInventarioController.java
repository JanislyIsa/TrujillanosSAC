package com.example.TrujillanosSAC.inventario.controller.api;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.dto.MovimientoInventarioDto;
import com.example.TrujillanosSAC.inventario.model.*;
import com.example.TrujillanosSAC.inventario.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/movimientos")
@PreAuthorize("@inventarioPermisos.inventario(authentication)")
public class MovimientoInventarioController {
    private final MovimientoInventarioService movimientos;
    @PostMapping
    public ResponseEntity<ComprobanteResponse> registrar(@Valid @RequestBody MovimientoRequest request, Principal principal) {
        var comprobante = movimientos.registrar(request,principal.getName());
        return ResponseEntity.created(URI.create("/api/v1/inventario/movimientos/"+comprobante.movimiento().idMovimiento()+"/comprobante")).body(comprobante);
    }
    @GetMapping
    public PaginaResponse<MovimientoInventarioDto> listar(@RequestParam(required=false) TipoItem tipoItem,
            @RequestParam(required=false) Long idItem, @RequestParam(required=false) TipoMovimiento tipoMovimiento,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required=false) Long idAlmacen, @RequestParam(defaultValue="0") int pagina,
            @RequestParam(defaultValue="20") int tamanio) {
        return PaginaResponse.de(movimientos.listar(tipoItem,idItem,tipoMovimiento,desde,hasta,idAlmacen),pagina,tamanio);
    }
    @GetMapping({"/{id}","/{id}/comprobante"})
    public ComprobanteResponse comprobante(@PathVariable @Positive Long id) { return movimientos.comprobante(id); }
}
