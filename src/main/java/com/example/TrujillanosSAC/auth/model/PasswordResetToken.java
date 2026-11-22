package com.example.TrujillanosSAC.auth.model;

import com.example.TrujillanosSAC.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Enlace para restablecer la contraseña. Solo se guarda el hash SHA-256 del token que va en el correo:
 * quien lea la base no puede usar los enlaces. Vence a los pocos minutos y se usa una sola vez.
 */
@Entity
@Table(name = "PASSWORD_RESET_TOKEN")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TOKEN")
    private Long idToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "TOKEN_HASH", length = 64, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_EXPIRACION", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Column(name = "USADO", nullable = false)
    private Boolean usado = false;

    /** Se puede usar si no se usó antes y todavía no vence. */
    public boolean vigente(LocalDateTime ahora) {
        return !Boolean.TRUE.equals(usado) && fechaExpiracion.isAfter(ahora);
    }
}
