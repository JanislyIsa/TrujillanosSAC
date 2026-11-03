package com.example.TrujillanosSAC.inventario.repository;
import com.example.TrujillanosSAC.inventario.model.NotificacionInventario;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface NotificacionInventarioRepository extends JpaRepository<NotificacionInventario, Long> {
    boolean existsByAlerta_IdAlertaAndDestinatario_IdPersona(Long alerta, Long empleado);
    @EntityGraph(attributePaths = {"alerta"})
    List<NotificacionInventario> findByDestinatario_IdPersonaOrderByIdNotificacionDesc(Long empleado);
    @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NotificacionInventario n where n.idNotificacion = :id and n.destinatario.idPersona = :empleado")
    Optional<NotificacionInventario> findPropiaForUpdate(@Param("id") Long id, @Param("empleado") Long empleado);
}
