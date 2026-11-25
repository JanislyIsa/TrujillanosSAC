package com.example.TrujillanosSAC.auditoria.service;

import com.example.TrujillanosSAC.auditoria.EventoAuditoria;
import com.example.TrujillanosSAC.auditoria.dto.AuditoriaEventoDTO;
import org.springframework.data.domain.Page;
import java.time.LocalDate;

public interface AuditoriaService {
    void registrar(EventoAuditoria evento);
    Page<AuditoriaEventoDTO> buscar(String usuario, String accion, LocalDate desde, LocalDate hasta,
                                    int pagina, int tamanio);
}
