package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.OrdenCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenCompraListadoResponseDTO;
import com.example.TrujillanosSAC.compras.dto.OrdenesResumenResponseDTO;
import com.example.TrujillanosSAC.compras.dto.PaginaResponseDTO;
import com.example.TrujillanosSAC.compras.service.service.OrdenCompraService;
import com.example.TrujillanosSAC.compras.util.PaginacionComprasUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Set;
import org.springframework.data.domain.Pageable;

@RestController
@Validated
@RequestMapping("/api/v1/compras/ordenes")
@PreAuthorize("hasAuthority('COMPRAS_ORDENES_GESTIONAR')")
public class OrdenCompraController {
    private final OrdenCompraService ordenCompraService;

    public OrdenCompraController(OrdenCompraService ordenCompraService) {
        this.ordenCompraService = ordenCompraService;
    }

    @PostMapping
    public ResponseEntity<OrdenCompraResponseDTO> generar(@Valid @RequestBody OrdenCompraRequestDTO request) {
        OrdenCompraResponseDTO response = ordenCompraService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PaginaResponseDTO<OrdenCompraListadoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int pagina,
            @RequestParam(defaultValue = "10") @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) int tamanio,
            @RequestParam(defaultValue = "fechaEmision") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion,
            @RequestParam(defaultValue = "") String buscar) {
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, ordenarPor, direccion,
                Set.of("idOrdenCompra", "fechaEmision"), "fechaEmision");
        return ResponseEntity.ok(PaginaResponseDTO.de(ordenCompraService.listarPagina(
                PaginacionComprasUtil.normalizarBusqueda(buscar), pageable)));
    }

    @GetMapping("/resumen")
    public ResponseEntity<OrdenesResumenResponseDTO> resumen() {
        return ResponseEntity.ok(ordenCompraService.resumen());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDTO> buscar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ordenCompraService.read(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDTO> actualizar(@PathVariable @Positive Long id,
                                                             @Valid @RequestBody OrdenCompraRequestDTO request) {
        return ResponseEntity.ok(ordenCompraService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        ordenCompraService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
