package com.example.TrujillanosSAC.Repository;

import com.example.TrujillanosSAC.Model.Pedido;
import com.example.TrujillanosSAC.Dto.ReporteVentasClienteDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.idPedido = :id")
    java.util.Optional<Pedido> findByIdForUpdate(@Param("id") Long id);
    @Query("""
            select p
            from Pedido p
            join fetch p.estado
            where p.cliente.idPersona = (
                select u.persona.idPersona
                from Usuario u
                where u.username = :username
            )
            order by p.fechaEmision desc, p.hora desc
            """)
    List<Pedido> findByUsername(@Param("username") String username);

    @Query("""
            select p
            from Pedido p
            join fetch p.estado
            join fetch p.cliente c
            join fetch c.persona
            order by p.fechaEmision desc, p.hora desc
            """)
    List<Pedido> findAllParaGestion();

    @Query("""
            select new com.example.TrujillanosSAC.Dto.ReporteVentasClienteDto(
                concat(concat(pe.nombre, ' '), pe.apellido),
                pe.telefono,
                count(p.idPedido),
                sum(p.total),
                max(p.fechaEmision)
            )
            from Pedido p
            join p.cliente c
            join c.persona pe
            group by pe.nombre, pe.apellido, pe.telefono
            order by sum(p.total) desc
            """)
    List<ReporteVentasClienteDto> reporteVentasPorCliente();
}
