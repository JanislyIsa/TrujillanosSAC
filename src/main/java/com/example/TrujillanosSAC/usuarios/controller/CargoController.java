package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.CargoRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.CargoResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.CargoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/cargos") @PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class CargoController {
    private final CargoService service;
    public CargoController(CargoService service) { this.service=service; }
    @PostMapping public ResponseEntity<CargoResponseDTO> create(@Valid @RequestBody CargoRequestDTO d) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(d)); }
    @GetMapping public ResponseEntity<List<CargoResponseDTO>> readAll() { return ResponseEntity.ok((List<CargoResponseDTO>)service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<CargoResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<CargoResponseDTO> update(@PathVariable @Positive Long id,@Valid @RequestBody CargoRequestDTO d) { return ResponseEntity.ok(service.update(id,d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
