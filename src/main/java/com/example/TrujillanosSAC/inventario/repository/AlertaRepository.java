package com.example.TrujillanosSAC.inventario.repository;
import com.example.TrujillanosSAC.inventario.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByTipoItemAndIdItemAndResueltaEnIsNull(String tipoItem, Long idItem);
    List<Alerta> findAllByOrderByIdAlertaDesc();
    boolean existsByTipoItemAndIdItem(String tipoItem, Long idItem);
}
