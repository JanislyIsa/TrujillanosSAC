package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    Optional<EstadoPedido> findFirstByNombreContainingIgnoreCase(String nombre);

    Optional<EstadoPedido> findFirstByOrderByIdEstadoAsc();

    List<EstadoPedido> findAllByOrderByIdEstadoAsc();
}
