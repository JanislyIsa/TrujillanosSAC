package com.example.TrujillanosSAC.auditoria.controller;

import com.example.TrujillanosSAC.auditoria.dto.AuditoriaEventoDTO;
import com.example.TrujillanosSAC.auditoria.dto.PaginaAuditoriaDTO;
import com.example.TrujillanosSAC.auditoria.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/auditoria")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {
    private final AuditoriaService auditoria;

    public AuditoriaController(AuditoriaService auditoria) { this.auditoria = auditoria; }

    @GetMapping
    public PaginaAuditoriaDTO<AuditoriaEventoDTO> buscar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {
        Page<AuditoriaEventoDTO> resultado = auditoria.buscar(usuario, accion, desde, hasta, pagina, tamanio);
        return PaginaAuditoriaDTO.de(resultado);
    }
}
