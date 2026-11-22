package com.example.TrujillanosSAC.auth.repository;

import com.example.TrujillanosSAC.auth.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /** La última solicitud del usuario: sirve para no enviar más de un correo por minuto. */
    Optional<PasswordResetToken> findFirstByUsuarioIdUsuarioOrderByFechaCreacionDesc(Long idUsuario);

    /** Deja sin efecto los enlaces pendientes del usuario (al pedir uno nuevo o al cambiar la clave). */
    @Modifying(flushAutomatically = true)
    @Query("update PasswordResetToken t set t.usado = true where t.usuario.idUsuario = :idUsuario and t.usado = false")
    int invalidarPendientes(@Param("idUsuario") Long idUsuario);
}
