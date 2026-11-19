package com.example.TrujillanosSAC.compras.controller;

import com.example.TrujillanosSAC.usuarios.model.Proveedor;
import com.example.TrujillanosSAC.usuarios.model.Usuario;
import com.example.TrujillanosSAC.usuarios.repository.ProveedorRepository;
import com.example.TrujillanosSAC.usuarios.repository.UsuarioRepository;
import com.example.TrujillanosSAC.compras.dto.CotizacionRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionProveedorRequestDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionResponseDTO;
import com.example.TrujillanosSAC.compras.dto.PaginaResponseDTO;
import com.example.TrujillanosSAC.compras.dto.CotizacionesResumenResponseDTO;
import com.example.TrujillanosSAC.compras.util.PaginacionComprasUtil;
import com.example.TrujillanosSAC.compras.service.service.CotizacionService;
import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;

@RestController
@Validated
@RequestMapping("/api/v1/compras/cotizaciones")
public class CotizacionController {
    private final CotizacionService cotizacionService;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;

    public CotizacionController(CotizacionService cotizacionService,
                                UsuarioRepository usuarioRepository,
                                ProveedorRepository proveedorRepository) {
        this.cotizacionService = cotizacionService;
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_CREAR')")
    public ResponseEntity<CotizacionResponseDTO> enviar(@Valid @RequestBody CotizacionProveedorRequestDTO request,
                                                        Principal principal) {
        Long proveedorId = proveedorActual(principal).getIdPersona();
        CotizacionRequestDTO delProveedor = new CotizacionRequestDTO(
                request.solicitudId(), proveedorId, request.fechaEntrega(), request.detalles(), request.descripcion());
        CotizacionResponseDTO response = cotizacionService.create(delProveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<PaginaResponseDTO<CotizacionResponseDTO>> listar(
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int pagina,
            @RequestParam(defaultValue = "10") @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) int tamanio,
            @RequestParam(defaultValue = "fechaCot") String ordenarPor,
            @RequestParam(defaultValue = "desc") String direccion,
            @RequestParam(defaultValue = "") String buscar) {
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, ordenarPor, direccion,
                Set.of("idCotizacion", "fechaCot", "fechaEntrega", "monto"), "fechaCot");
        return ResponseEntity.ok(PaginaResponseDTO.de(cotizacionService.listarPagina(
                PaginacionComprasUtil.normalizarBusqueda(buscar), pageable)));
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<CotizacionesResumenResponseDTO> resumen() {
        return ResponseEntity.ok(cotizacionService.resumen());
    }

    @GetMapping("/mias")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
    public ResponseEntity<PaginaResponseDTO<CotizacionResponseDTO>> listarMias(Principal principal,
            @RequestParam(defaultValue = "0") @jakarta.validation.constraints.Min(0) int pagina,
            @RequestParam(defaultValue = "10") @jakarta.validation.constraints.Min(1) @jakarta.validation.constraints.Max(100) int tamanio,
            @RequestParam(defaultValue = "") String buscar) {
        Pageable pageable = PaginacionComprasUtil.construir(pagina, tamanio, "fechaCot", "desc",
                Set.of("fechaCot"), "fechaCot");
        return ResponseEntity.ok(PaginaResponseDTO.de(cotizacionService.listarPaginaPorProveedor(
                proveedorActual(principal).getIdPersona(), PaginacionComprasUtil.normalizarBusqueda(buscar), pageable)));
    }

    @GetMapping("/mias/resumen")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_PROPIAS')")
    public ResponseEntity<CotizacionesResumenResponseDTO> resumenMias(Principal principal) {
        return ResponseEntity.ok(cotizacionService.resumenPorProveedor(proveedorActual(principal).getIdPersona()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<CotizacionResponseDTO> buscar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(cotizacionService.read(id));
    }

    @GetMapping("/solicitud/{solicitudId}")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_VER_TODAS')")
    public ResponseEntity<List<CotizacionResponseDTO>> listarPorSolicitud(@PathVariable @Positive Long solicitudId) {
        return ResponseEntity.ok(cotizacionService.listarPorSolicitud(solicitudId));
    }

    @PatchMapping("/{id}/aceptar")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_GESTIONAR')")
    public ResponseEntity<CotizacionResponseDTO> aceptar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(cotizacionService.aceptar(id));
    }

    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_GESTIONAR')")
    public ResponseEntity<CotizacionResponseDTO> rechazar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(cotizacionService.rechazar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('COMPRAS_COTIZACIONES_GESTIONAR')")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        cotizacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Proveedor proveedorActual(Principal principal) {
        if (principal == null) throw new RecursosNoEncontradosException("No se encontró el usuario autenticado");
        Usuario usuario = usuarioRepository.findByUsernameConRolesYPersona(principal.getName())
                .orElseThrow(() -> new RecursosNoEncontradosException("Usuario autenticado no encontrado"));
        if (usuario.getPersona() == null) throw new RecursosNoEncontradosException("El usuario no tiene persona asociada");
        return proveedorRepository.findByIdPersona(usuario.getPersona().getIdPersona())
                .orElseThrow(() -> new RecursosNoEncontradosException("El usuario no está registrado como proveedor"));
    }
}
