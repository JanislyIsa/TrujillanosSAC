package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.ProveedorRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.ProveedorResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.ProveedorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/proveedores") @PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class ProveedorController {
    private final ProveedorService service;
    public ProveedorController(ProveedorService service) { this.service=service; }
    @PostMapping public ResponseEntity<ProveedorResponseDTO> create(@Valid @RequestBody ProveedorRequestDTO d) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(d)); }
    @GetMapping public ResponseEntity<List<ProveedorResponseDTO>> readAll() { return ResponseEntity.ok((List<ProveedorResponseDTO>)service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<ProveedorResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<ProveedorResponseDTO> update(@PathVariable @Positive Long id,@Valid @RequestBody ProveedorRequestDTO d) { return ResponseEntity.ok(service.update(id,d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
