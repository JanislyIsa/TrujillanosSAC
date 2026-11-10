package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.ClienteRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.ClienteResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.ClienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/clientes") @PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service=service; }
    @PostMapping public ResponseEntity<ClienteResponseDTO> create(@Valid @RequestBody ClienteRequestDTO d) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(d)); }
    @GetMapping public ResponseEntity<List<ClienteResponseDTO>> readAll() { return ResponseEntity.ok((List<ClienteResponseDTO>)service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<ClienteResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<ClienteResponseDTO> update(@PathVariable @Positive Long id,@Valid @RequestBody ClienteRequestDTO d) { return ResponseEntity.ok(service.update(id,d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
