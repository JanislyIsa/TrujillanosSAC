package com.example.TrujillanosSAC.inventario.model;

import com.example.TrujillanosSAC.Model.Empleado;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICACION_INVENTARIO", uniqueConstraints =
        @UniqueConstraint(name = "UK_NOTIF_ALERTA_DEST", columnNames = {"ID_ALERTA", "ID_DESTINATARIO"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificacionInventario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_NOTIFICACION")
    private Long idNotificacion;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ALERTA", nullable = false)
    private Alerta alerta;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DESTINATARIO", nullable = false)
    private Empleado destinatario;
    @Column(name = "CREADA_EN", nullable = false)
    private LocalDateTime creadaEn;
    @Column(name = "LEIDA_EN")
    private LocalDateTime leidaEn;
}
