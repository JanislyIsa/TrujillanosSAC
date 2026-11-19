package com.example.TrujillanosSAC.usuarios.controller;

import com.example.TrujillanosSAC.usuarios.dto.AsignarRolesRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.UsuarioRequestDTO;
import com.example.TrujillanosSAC.usuarios.dto.UsuarioResponseDTO;
import com.example.TrujillanosSAC.usuarios.service.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @Validated @RequestMapping("/api/v1/usuarios/cuentas")
@PreAuthorize("@adminAccess.puedeGestionarUsuarios(authentication.name)")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service=service; }
    @PostMapping public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @GetMapping public ResponseEntity<List<UsuarioResponseDTO>> readAll() { return ResponseEntity.ok((List<UsuarioResponseDTO>) service.readAll()); }
    @GetMapping("/{id}") public ResponseEntity<UsuarioResponseDTO> read(@PathVariable @Positive Long id) { return ResponseEntity.ok(service.read(id)); }
    @PutMapping("/{id}") public ResponseEntity<UsuarioResponseDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody UsuarioRequestDTO request) { return ResponseEntity.ok(service.update(id,request)); }
    @PutMapping("/{id}/roles") public ResponseEntity<UsuarioResponseDTO> assignRoles(@PathVariable @Positive Long id, @Valid @RequestBody AsignarRolesRequestDTO request) { return ResponseEntity.ok(service.asignarRoles(id,request)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
