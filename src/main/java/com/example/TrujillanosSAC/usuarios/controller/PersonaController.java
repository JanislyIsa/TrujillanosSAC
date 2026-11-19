package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.PersonaRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.PersonaResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.PersonaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/personas")
@PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class PersonaController {
    private final PersonaService service;
    public PersonaController(PersonaService service) { this.service=service; }
    @PostMapping public ResponseEntity<PersonaResponseDTO> create(@Valid @RequestBody PersonaRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @GetMapping public ResponseEntity<List<PersonaResponseDTO>> readAll() { return ResponseEntity.ok((List<PersonaResponseDTO>) service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<PersonaResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @GetMapping("/buscar") public ResponseEntity<PersonaResponseDTO> buscar(@RequestParam @NotBlank String numeroDocumento) {
        return service.buscarPorNumeroDocumento(numeroDocumento).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}") public ResponseEntity<PersonaResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody PersonaRequestDTO request) { return ResponseEntity.ok(service.update(id,request)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
