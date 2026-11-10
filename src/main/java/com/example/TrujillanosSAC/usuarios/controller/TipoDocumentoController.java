package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.TipoDocumentoRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.TipoDocumentoResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.TipoDocumentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/tipos-documento") @PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class TipoDocumentoController {
    private final TipoDocumentoService service;
    public TipoDocumentoController(TipoDocumentoService service) { this.service=service; }
    @PostMapping public ResponseEntity<TipoDocumentoResponseDTO> create(@Valid @RequestBody TipoDocumentoRequestDTO d) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(d)); }
    @GetMapping public ResponseEntity<List<TipoDocumentoResponseDTO>> readAll() { return ResponseEntity.ok((List<TipoDocumentoResponseDTO>)service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<TipoDocumentoResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<TipoDocumentoResponseDTO> update(@PathVariable @Positive Long id,@Valid @RequestBody TipoDocumentoRequestDTO d) { return ResponseEntity.ok(service.update(id,d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
