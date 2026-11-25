package com.example.TrujillanosSAC.auditoria;

import com.example.TrujillanosSAC.auditoria.service.AuditoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AuditoriaListener {
    private static final Logger log = LoggerFactory.getLogger(AuditoriaListener.class);
    private final AuditoriaService auditoria;

    public AuditoriaListener(AuditoriaService auditoria) { this.auditoria = auditoria; }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void registrarTrasConfirmar(EventoAuditoria evento) {
        try {
            auditoria.registrar(evento);
        } catch (RuntimeException ex) {
            // La auditoría no debe convertir una operación ya confirmada en una respuesta de error.
            log.error("No se pudo guardar el evento de auditoría {} para {} {}",
                    evento.accion(), evento.entidad(), evento.entidadId(), ex);
        }
    }
}
