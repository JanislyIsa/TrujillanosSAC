package com.example.TrujillanosSAC.auditoria.service.impl;

import com.example.TrujillanosSAC.auditoria.EventoAuditoria;
import com.example.TrujillanosSAC.auditoria.dto.AuditoriaEventoDTO;
import com.example.TrujillanosSAC.auditoria.repository.AuditoriaEventoRepository;
import com.example.TrujillanosSAC.auditoria.service.AuditoriaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {
    private static final Set<String> ACCIONES = Set.of("STOCK_MOVIMIENTO_REGISTRADO", "ITEM_CREADO",
            "ITEM_ACTUALIZADO", "ITEM_DESACTIVADO", "PEDIDO_CREADO", "PEDIDO_CANCELADO", "PEDIDO_ESTADO_CAMBIADO",
            "ORDEN_COMPRA_CREADA", "PAGO_COMPRA_REGISTRADO");
    private final AuditoriaEventoRepository repository;

    public AuditoriaServiceImpl(AuditoriaEventoRepository repository) { this.repository = repository; }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(EventoAuditoria evento) {
        var entidad = new com.example.TrujillanosSAC.auditoria.model.AuditoriaEvento();
        entidad.setFecha(LocalDateTime.now());
        entidad.setUsuario(evento.usuario());
        entidad.setAccion(evento.accion());
        entidad.setEntidad(evento.entidad());
        entidad.setEntidadId(evento.entidadId());
        entidad.setDetalle(evento.detalle());
        entidad.setRequestId(evento.requestId());
        repository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaEventoDTO> buscar(String usuario, String accion, LocalDate desde,
                                            LocalDate hasta, int pagina, int tamanio) {
        if (pagina < 0 || tamanio < 1 || tamanio > 100)
            throw new IllegalArgumentException("La página debe ser >= 0 y el tamaño debe estar entre 1 y 100");
        if (desde != null && hasta != null && desde.isAfter(hasta))
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        String filtroAccion = accion == null || accion.isBlank() ? null : accion.trim().toUpperCase();
        if (filtroAccion != null && !ACCIONES.contains(filtroAccion))
            throw new IllegalArgumentException("Acción de auditoría no válida");
        return repository.buscar(normalizar(usuario), filtroAccion,
                desde == null ? null : desde.atStartOfDay(),
                hasta == null ? null : hasta.plusDays(1).atStartOfDay(),
                PageRequest.of(pagina, tamanio, Sort.by(Sort.Direction.DESC, "fecha").and(Sort.by("id"))))
                .map(AuditoriaEventoDTO::de);
    }

    private static String normalizar(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
