package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.PaginaResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.dto.SolicitudProveedorResponseDTO;
import com.example.TrujillanosSAC.compras.service.service.PortalProveedorService;
import com.example.TrujillanosSAC.compras.util.PaginacionComprasUtil;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@Validated
@RequestMapping("/api/v1/compras/proveedor")
@PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
public class PortalProveedorController {
    private final PortalProveedorService portalService;

    public PortalProveedorController(PortalProveedorService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/solicitudes")
    public PaginaResponseDTO<SolicitudProveedorResponseDTO> solicitudes(
            Principal principal,
            @RequestParam(defaultValue = "0") @Min(0) int pagina,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int tamanio,
            @RequestParam(defaultValue = "") String buscar) {
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, "idSolicitud", "desc",
                Set.of("idSolicitud"), "idSolicitud");
        return PaginaResponseDTO.de(portalService.solicitudes(principal.getName(), buscar, pageable));
    }

    @GetMapping("/solicitudes/{id}")
    public SolicitudProveedorResponseDTO solicitud(
            @PathVariable Long id, Principal principal) {
        return portalService.solicitud(id, principal.getName());
    }

    @GetMapping("/ordenes")
    public List<OrdenProveedorResponseDTO> ordenes(
            Principal principal, @RequestParam List<Long> cotizacionIds) {
        return portalService.ordenes(cotizacionIds, principal.getName());
    }
}
