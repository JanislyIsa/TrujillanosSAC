package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraRequestDTO;
import com.example.TrujillanosSAC.compras.dto.ComprobanteCompraResponseDTO;
import com.example.TrujillanosSAC.compras.repository.OrdenCompraRepository;
import com.example.TrujillanosSAC.compras.repository.ComprobanteCompraRepository;
import com.example.TrujillanosSAC.compras.service.service.ComprobanteCompraService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/v1/compras/comprobantes")
public class ComprobanteCompraController {
    private final ComprobanteCompraService comprobanteService;
    private final OrdenCompraRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComprobanteCompraRepository comprobanteRepository;

    public ComprobanteCompraController(ComprobanteCompraService comprobanteService,
                                       OrdenCompraRepository ordenRepository,
                                       UsuarioRepository usuarioRepository,
                                       ComprobanteCompraRepository comprobanteRepository) {
        this.comprobanteService = comprobanteService;
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.comprobanteRepository = comprobanteRepository;
    }

    @PostMapping(value = "/factura", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('COMPRAS_COMPROBANTES_CREAR')")
    @Transactional
    public ResponseEntity<ComprobanteCompraResponseDTO> enviarFactura(
            @RequestParam("ordenCompraId") @Positive Long ordenCompraId,
            @RequestParam("numeroFactura") String numeroFactura,
            @RequestParam("fechaEmision") java.time.LocalDate fechaEmision,
            @RequestParam("monto") java.math.BigDecimal monto,
            @RequestParam("archivo") MultipartFile archivo,
            Principal principal) throws java.io.IOException {
        if (numeroFactura == null || numeroFactura.isBlank() || numeroFactura.length() > 50)
            throw new ReglaNegocioException("El número de factura es obligatorio y admite hasta 50 caracteres");
        if (archivo == null || archivo.isEmpty() || archivo.getSize() > 10 * 1024 * 1024)
            throw new ReglaNegocioException("Adjunta un PDF de factura de hasta 10 MB");
        byte[] bytes = archivo.getBytes();
        if (bytes.length < 5 || bytes[0] != '%' || bytes[1] != 'P' || bytes[2] != 'D' || bytes[3] != 'F' || bytes[4] != '-')
            throw new ReglaNegocioException("El archivo adjunto no es un PDF válido");
        Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(principal.getName())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        if (usuario.getPersona() == null) throw new RecursosNoEncontradosException("El usuario no tiene persona asociada");
        var orden = ordenRepository.findById(ordenCompraId).orElseThrow(() ->
                new RecursosNoEncontradosException("Orden de compra no encontrada con id: " + ordenCompraId));
        if (!orden.getCotizacion().getProveedor().getIdPersona().equals(usuario.getPersona().getIdPersona()))
            throw new ReglaNegocioException("La orden no pertenece al proveedor autenticado");
        var created = comprobanteService.create(new ComprobanteCompraRequestDTO(ordenCompraId, fechaEmision, monto));
        var comprobante = comprobanteRepository.findById(created.id()).orElseThrow();
        comprobante.setNumeroFactura(numeroFactura.trim());
        comprobante.setArchivoFactura(bytes);
        return ResponseEntity.status(HttpStatus.CREATED).body(comprobanteService.read(created.id()));
    }

    @GetMapping("/{id}/archivo")
    @PreAuthorize("hasAuthority('COMPRAS_COMPROBANTES_CREAR')")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> verFactura(@PathVariable @Positive Long id, Principal principal) {
        Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(principal.getName())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        var comprobante = comprobanteRepository.findById(id)
                .orElseThrow(() -> new RecursosNoEncontradosException("Comprobante no encontrado"));
        if (usuario.getPersona() == null || !comprobante.getOrdenCompra().getCotizacion().getProveedor()
                .getIdPersona().equals(usuario.getPersona().getIdPersona()))
            throw new ReglaNegocioException("El comprobante no pertenece al proveedor autenticado");
        if (comprobante.getArchivoFactura() == null || comprobante.getArchivoFactura().length == 0)
            throw new RecursosNoEncontradosException("El comprobante no tiene archivo adjunto");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"factura.pdf\"")
                .body(comprobante.getArchivoFactura());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_COMPROBANTES_CREAR')")
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
