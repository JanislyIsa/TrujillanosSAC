package com.example.TrujillanosSAC.auditoria.dto;

import com.example.TrujillanosSAC.auditoria.model.AuditoriaEvento;
import java.time.LocalDateTime;

public record AuditoriaEventoDTO(LocalDateTime fecha, String usuario, String accion,
                                 String entidad, Long entidadId, String detalle, String requestId) {
    public static AuditoriaEventoDTO de(AuditoriaEvento e) {
        return new AuditoriaEventoDTO(e.getFecha(), e.getUsuario(), e.getAccion(),
                e.getEntidad(), e.getEntidadId(), e.getDetalle(), e.getRequestId());
    }
}
