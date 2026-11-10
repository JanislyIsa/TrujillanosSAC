package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.RolRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.RolResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.RolService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/roles") @PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class RolController {
    private final RolService service;
    public RolController(RolService service) { this.service=service; }
    @PostMapping public ResponseEntity<RolResponseDTO> create(@Valid @RequestBody RolRequestDTO d) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(d)); }
    @GetMapping public ResponseEntity<List<RolResponseDTO>> readAll() { return ResponseEntity.ok((List<RolResponseDTO>)service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<RolResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<RolResponseDTO> update(@PathVariable @Positive Long id,@Valid @RequestBody RolRequestDTO d) { return ResponseEntity.ok(service.update(id,d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
