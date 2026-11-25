package com.example.TrujillanosSAC.auditoria.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "AUDITORIA_EVENTOS")
@Getter @Setter @NoArgsConstructor
public class AuditoriaEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EVENTO")
    private Long id;

    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "USUARIO", nullable = false, length = 50)
    private String usuario;

    @Column(name = "ACCION", nullable = false, length = 40)
    private String accion;

    @Column(name = "ENTIDAD", nullable = false, length = 40)
    private String entidad;

    @Column(name = "ENTIDAD_ID")
    private Long entidadId;

    @Column(name = "DETALLE", length = 500)
    private String detalle;

    @Column(name = "REQUEST_ID", length = 36)
    private String requestId;
}
