package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.EmpleadoRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.EmpleadoResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.EmpleadoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/empleados") @PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class EmpleadoController {
    private final EmpleadoService service;
    public EmpleadoController(EmpleadoService service) { this.service=service; }
    @PostMapping public ResponseEntity<EmpleadoResponseDTO> create(@Valid @RequestBody EmpleadoRequestDTO d) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(d)); }
    @GetMapping public ResponseEntity<List<EmpleadoResponseDTO>> readAll() { return ResponseEntity.ok((List<EmpleadoResponseDTO>)service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<EmpleadoResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<EmpleadoResponseDTO> update(@PathVariable @Positive Long id,@Valid @RequestBody EmpleadoRequestDTO d) { return ResponseEntity.ok(service.update(id,d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
