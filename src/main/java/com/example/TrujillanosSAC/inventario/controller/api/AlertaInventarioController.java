package com.example.TrujillanosSAC.inventario.controller.api;
import com.example.TrujillanosSAC.inventario.dto.InventarioDtos.*;
import com.example.TrujillanosSAC.inventario.service.AlertaInventarioService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario")
public class AlertaInventarioController {
    private final AlertaInventarioService alertas;
    @GetMapping("/alertas") @PreAuthorize("@inventarioPermisos.lectura(authentication)")
    public PaginaResponse<AlertaResponse> listar(@RequestParam(defaultValue="true") boolean soloActivas,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanio) {
        return PaginaResponse.de(alertas.listar(soloActivas),pagina,tamanio);
    }
    @PostMapping("/alertas/monitorear") @PreAuthorize("@inventarioPermisos.inventario(authentication)")
    public ResponseEntity<Void> monitorear() { alertas.monitorear(); return ResponseEntity.noContent().build(); }
    @GetMapping("/notificaciones") @PreAuthorize("@inventarioPermisos.compras(authentication)")
    public PaginaResponse<NotificacionResponse> notificaciones(Principal principal,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanio) {
        return PaginaResponse.de(alertas.notificaciones(principal.getName()),pagina,tamanio);
    }
    @PatchMapping("/notificaciones/{id}/leida") @PreAuthorize("@inventarioPermisos.compras(authentication)")
    public ResponseEntity<Void> leida(@PathVariable @Positive Long id, Principal principal) {
        alertas.marcarLeida(id,principal.getName()); return ResponseEntity.noContent().build();
    }
}
