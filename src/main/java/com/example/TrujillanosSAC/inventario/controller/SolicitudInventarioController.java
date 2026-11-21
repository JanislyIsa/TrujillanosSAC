package com.example.TrujillanosSAC.inventario.controller;
import com.example.TrujillanosSAC.inventario.dto.request.SolicitudAutomaticaRequest;
import com.example.TrujillanosSAC.inventario.dto.request.SolicitudRequest;
import com.example.TrujillanosSAC.inventario.dto.response.FaltanteResponse;
import com.example.TrujillanosSAC.inventario.dto.response.PaginaResponse;
import com.example.TrujillanosSAC.inventario.dto.response.SolicitudResponse;
import com.example.TrujillanosSAC.inventario.service.service.SolicitudInventarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.security.Principal;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/v1/inventario/solicitudes-compra")
@PreAuthorize("hasRole('EMPLEADO')")
public class SolicitudInventarioController {
    private final SolicitudInventarioService solicitudes;
    @GetMapping("/faltantes")
    public PaginaResponse<FaltanteResponse> faltantes(@RequestParam(defaultValue="0") int pagina,
            @RequestParam(defaultValue="20") int tamanio) { return PaginaResponse.de(solicitudes.faltantes(),pagina,tamanio); }
    @GetMapping
    public PaginaResponse<SolicitudResponse> listar(@RequestParam(defaultValue="0") int pagina,
            @RequestParam(defaultValue="20") int tamanio) { return PaginaResponse.de(solicitudes.listar(),pagina,tamanio); }
    @GetMapping("/{id}")
    public SolicitudResponse obtener(@PathVariable @Positive Long id) { return solicitudes.obtener(id); }
    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody SolicitudRequest request, Principal principal) {
        return creado(solicitudes.crear(request,principal.getName()));
    }
    @PostMapping("/automatica")
    public ResponseEntity<SolicitudResponse> automatica(@Valid @RequestBody SolicitudAutomaticaRequest request, Principal principal) {
        return creado(solicitudes.automatica(request.idProveedor(),principal.getName()));
    }
    private ResponseEntity<SolicitudResponse> creado(SolicitudResponse s) {
        return ResponseEntity.created(URI.create("/api/v1/inventario/solicitudes-compra/"+s.id())).body(s);
    }
}
