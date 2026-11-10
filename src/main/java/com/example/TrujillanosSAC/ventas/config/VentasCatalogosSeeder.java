package com.example.TrujillanosSAC.ventas.config;

import com.example.TrujillanosSAC.ventas.model.*;
import com.example.TrujillanosSAC.ventas.model.codigo.*;
import com.example.TrujillanosSAC.ventas.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

/**
 * Registra los códigos que el programa necesita (si faltan) al arrancar con app.seed.habilitado=true.
 * Reemplaza al antiguo EstadoPedidoSeeder. En Oracle lo mismo lo hace el script de catálogos de ventas.
 */
@Configuration
@ConditionalOnProperty(name = "app.seed.habilitado", havingValue = "true", matchIfMissing = true)
public class VentasCatalogosSeeder {

    @Bean
    CommandLineRunner crearCatalogosVentas(EstadoPedidoRepository estadosPedido,
                                           EstadoPagoRepository estadosPago,
                                           MetodoPagoRepository metodosPago,
                                           TipoComprobanteVentaRepository tiposComprobante,
                                           EstadoComprobanteVentaRepository estadosComprobante) {
        return args -> {
            registrar(estadosPedido, EstadoPedido::new, EstadoPedidoCodigo.values());
            registrar(estadosPago, EstadoPago::new, EstadoPagoCodigo.values());
            registrar(metodosPago, MetodoPago::new, MetodoPagoCodigo.values());
            registrar(tiposComprobante, TipoComprobanteVenta::new, TipoComprobanteCodigo.values());
            registrar(estadosComprobante, EstadoComprobanteVenta::new, EstadoComprobanteCodigo.values());
        };
    }

    static <E extends Catalogo> void registrar(CatalogoRepository<E> repository, Supplier<E> fabrica,
                                               CodigoCatalogo[] codigos) {
        for (CodigoCatalogo codigo : codigos) {
            if (repository.findByCodigo(codigo.name()).isPresent()) {
                continue;
            }
            // METODO_PAGO lo comparte compras: si ya existe un registro con ese nombre y sin código, se reutiliza.
            E entidad = repository.findFirstByNombreIgnoreCase(codigo.nombre())
                    .filter(existente -> existente.getCodigo() == null)
                    .orElseGet(fabrica);
            entidad.setCodigo(codigo.name());
            if (entidad.getNombre() == null) {
                entidad.setNombre(codigo.nombre());
            }
            repository.save(entidad);
        }
    }
}
