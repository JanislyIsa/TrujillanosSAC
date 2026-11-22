package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.CotizacionProveedorRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.dto.PaginaResponseDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionesResumenResponseDTO;
import com.example.TrujillanosSAC.compras.util.PaginacionComprasUtil;
import com.example.TrujillanosSAC.compras.service.service.CotizacionService;
import com.example.TrujillanosSAC.compras.service.service.PortalProveedorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

@RestController
@Validated
@RequestMapping("/api/v1/compras/cotizaciones")
public class CotizacionController {
    private final CotizacionService cotizacionService;
    private final PortalProveedorService portalProveedorService;

    public CotizacionController(CotizacionService cotizacionService, PortalProveedorService portalProveedorService) {
        this.cotizacionService = cotizacionService;
        this.portalProveedorService = portalProveedorService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_CREAR')")
    public ResponseEntity<CotizacionResponseDTO> enviar(@Valid @RequestBody CotizacionProveedorRequestDTO request,
                                                        Principal principal) {
        CotizacionResponseDTO response = portalProveedorService.enviarCotizacion(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<PaginaResponseDTO<CotizacionResponseDTO>> listar(
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int pagina,
            @RequestParam(defaultValue = "10") @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) int tamanio,
            @RequestParam(defaultValue = "fechaCot") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion,
            @RequestParam(defaultValue = "") String buscar) {
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, ordenarPor, direccion,
                Set.of("idCotizacion", "fechaCot", "fechaEntrega", "monto"), "fechaCot");
        return ResponseEntity.ok(PaginaResponseDTO.de(cotizacionService.listarPagina(
                PaginacionComprasUtil.normalizarBusqueda(buscar), pageable)));
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<CotizacionesResumenResponseDTO> resumen() {
        return ResponseEntity.ok(cotizacionService.resumen());
    }

    @GetMapping("/mias")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
    public ResponseEntity<PaginaResponseDTO<CotizacionResponseDTO>> listarMias(Principal principal,
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int pagina,
            @RequestParam(defaultValue = "10") @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) int tamanio,
            @RequestParam(defaultValue = "") String buscar) {
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, "fechaCot", "desc",
                Set.of("fechaCot"), "fechaCot");
        return ResponseEntity.ok(PaginaResponseDTO.de(cotizacionService.listarPaginaPorProveedor(
                portalProveedorService.proveedorId(principal.getName()), PaginacionComprasUtil.normalizarBusqueda(buscar), pageable)));
    }

    @GetMapping("/mias/resumen")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
    public ResponseEntity<CotizacionesResumenResponseDTO> resumenMias(Principal principal) {
        return ResponseEntity.ok(cotizacionService.resumenPorProveedor(portalProveedorService.proveedorId(principal.getName())));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<CotizacionResponseDTO> buscar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(cotizacionService.read(id));
    }

    @GetMapping("/solicitud/{solicitudId}")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<List<CotizacionResponseDTO>> listarPorSolicitud(@PathVariable @Positive Long solicitudId) {
        return ResponseEntity.ok(cotizacionService.listarPorSolicitud(solicitudId));
    }

    @PatchMapping("/{id}/aceptar")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_GESTIONAR')")
    public ResponseEntity<CotizacionResponseDTO> aceptar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(cotizacionService.aceptar(id));
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_GESTIONAR')")
    public ResponseEntity<CotizacionResponseDTO> rechazar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(cotizacionService.rechazar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_GESTIONAR')")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        cotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
