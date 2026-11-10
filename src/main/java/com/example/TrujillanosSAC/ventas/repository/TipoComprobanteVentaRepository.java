package com.example.TrujillanosSAC.ventas.repository;

import com.example.TrujillanosSAC.ventas.model.TipoComprobanteVenta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("ventasTipoComprobanteVentaRepository")
public interface TipoComprobanteVentaRepository extends CatalogoRepository<TipoComprobanteVenta> {

    /** Bloquear el tipo hace que dos ventas no reciban el mismo correlativo al mismo tiempo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from VentaTipoComprobanteVenta t where t.codigo = :codigo")
    Optional<TipoComprobanteVenta> bloquearPorCodigo(@Param("codigo") String codigo);
}
