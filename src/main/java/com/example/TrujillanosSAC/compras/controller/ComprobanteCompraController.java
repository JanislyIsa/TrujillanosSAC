package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.service.service.ComprobanteCompraService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/compras/comprobantes")
public class ComprobanteCompraController {
    private final ComprobanteCompraService comprobanteService;
    private final OrdenCompraRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;

    public ComprobanteCompraController(ComprobanteCompraService comprobanteService,
                                       OrdenCompraRepository ordenRepository,
                                       UsuarioRepository usuarioRepository) {
        this.comprobanteService = comprobanteService;
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('PROVEEDOR')")
    public ResponseEntity<ComprobanteCompraResponseDTO> enviar(@Valid @RequestBody ComprobanteCompraRequestDTO request,
                                                               Principal principal) {
        Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(principal.getName())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        if (usuario.getPersona() == null) throw new RecursosNoEncontradosException("El usuario no tiene persona asociada");
        var orden = ordenRepository.findById(request.ordenCompraId()).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + request.ordenCompraId()));
        if (orden.getCotizacion().getProveedor().getIdPersona().equals(usuario.getPersona().getIdPersona()) == false)
            throw new ReglaNegocioException("La orden no pertenece al proveedor autenticado");
        ComprobanteCompraResponseDTO response = comprobanteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<List<ComprobanteCompraResponseDTO>> listar() {
        return ResponseEntity.ok((List<ComprobanteCompraResponseDTO>) comprobanteService.readAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLEADO')")
    public ResponseEntity<ComprobanteCompraResponseDTO> buscar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(comprobanteService.read(id));
    }
}
