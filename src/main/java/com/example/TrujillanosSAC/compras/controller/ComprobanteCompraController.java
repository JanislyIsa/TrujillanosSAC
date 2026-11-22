package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.service.service.ComprobanteCompraService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/compras/comprobantes")
public class ComprobanteCompraController {
    private final ComprobanteCompraService comprobanteService;

    public ComprobanteCompraController(ComprobanteCompraService comprobanteService) {
        this.comprobanteService = comprobanteService;
    }

    @PostMapping(value = "/factura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('COMPRAS_COMPROBANTES_CREAR')")
    public ResponseEntity<ComprobanteCompraResponseDTO> enviarFactura(
            @RequestParam("ordenCompraId") @Positive Long ordenCompraId,
            @RequestParam("numeroFactura") String numeroFactura,
            @RequestParam("fechaEmision") java.time.LocalDate fechaEmision,
            @RequestParam("monto") java.math.BigDecimal monto,
            @RequestParam("archivo") MultipartFile archivo,
            Principal principal) throws java.io.IOException {
        var response = comprobanteService.enviarFacturaProveedor(ordenCompraId, numeroFactura,
                fechaEmision, monto, archivo, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/archivo")
    @PreAuthorize("hasAnyAuthority('COMPRAS_COMPROBANTES_CREAR','COMPRAS_COMPROBANTES_VER')")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable @Positive Long id, Principal principal,
                                                   Authentication authentication) {
        boolean comprasPuedeVerTodos = authentication.getAuthorities().stream()
                .anyMatch(a -> "COMPRAS_COMPROBANTES_VER".equals(a.getAuthority()));
        byte[] archivo = comprobanteService.descargarFactura(id, principal.getName(), comprasPuedeVerTodos);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura-" + id + ".pdf\"")
                .body(archivo);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_COMPROBANTES_VER')")
    public ResponseEntity<List<ComprobanteCompraResponseDTO>> listar() {
        return ResponseEntity.ok((List<ComprobanteCompraResponseDTO>) comprobanteService.readAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_COMPROBANTES_VER')")
    public ResponseEntity<ComprobanteCompraResponseDTO> buscar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(comprobanteService.read(id));
    }
}
