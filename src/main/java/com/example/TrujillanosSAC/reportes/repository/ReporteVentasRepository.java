package com.example.TrujillanosSAC.reportes.repository;

import com.example.TrujillanosSAC.reportes.dto.ProductoMasVendidoDTO;
import com.example.TrujillanosSAC.reportes.dto.VentaPorCategoriaDTO;
import com.example.TrujillanosSAC.ventas.model.Pedido;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/** Consultas agregadas para los reportes del módulo de ventas. */
public interface ReporteVentasRepository extends Repository<Pedido, Long> {

    @Query("""
            select new com.example.TrujillanosSAC.reportes.dto.VentaPorCategoriaDTO(
                categoria.idCategoria, categoria.nombre, sum(detalle.cantidad), sum(detalle.subtotal))
            from VentaDetallePedido detalle
            join detalle.producto producto
            join producto.categoria categoria
            join detalle.pedido pedido
            join pedido.estado estado
            where estado.codigo in :estados
              and (:desde is null or pedido.fechaRegistro >= :desde)
              and (:hasta is null or pedido.fechaRegistro <= :hasta)
            group by categoria.idCategoria, categoria.nombre
            order by sum(detalle.subtotal) desc
            """)
    List<VentaPorCategoriaDTO> porCategoria(@Param("estados") Collection<String> estados,
                                            @Param("desde") LocalDateTime desde,
                                            @Param("hasta") LocalDateTime hasta);

    @Query("""
            select new com.example.TrujillanosSAC.reportes.dto.ProductoMasVendidoDTO(
                producto.idProducto, producto.nombre, sum(detalle.cantidad), sum(detalle.subtotal))
            from VentaDetallePedido detalle
            join detalle.producto producto
            join detalle.pedido pedido
            join pedido.estado estado
            where estado.codigo in :estados
              and (:desde is null or pedido.fechaRegistro >= :desde)
              and (:hasta is null or pedido.fechaRegistro <= :hasta)
            group by producto.idProducto, producto.nombre
            order by sum(detalle.cantidad) desc, sum(detalle.subtotal) desc
            """)
    List<ProductoMasVendidoDTO> productosMasVendidos(@Param("estados") Collection<String> estados,
                                                      @Param("desde") LocalDateTime desde,
                                                      @Param("hasta") LocalDateTime hasta);
}
