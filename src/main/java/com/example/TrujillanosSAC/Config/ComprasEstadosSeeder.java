package com.example.TrujillanosSAC.Config;

import com.example.TrujillanosSAC.Model.EstadoCotizacion;
import com.example.TrujillanosSAC.Model.EstadoComprobanteCompra;
import com.example.TrujillanosSAC.inventario.model.EstadoSolicitudCompra;
import com.example.TrujillanosSAC.Model.MetodoPago;
import com.example.TrujillanosSAC.Repository.EstadoComprobanteCompraRepository;
import com.example.TrujillanosSAC.Repository.EstadoCotizacionRepository;
import com.example.TrujillanosSAC.inventario.repository.EstadoSolicitudCompraRepository;
import com.example.TrujillanosSAC.Repository.MetodoPagoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="app.seed.habilitado", havingValue="true", matchIfMissing=true)
@Configuration
public class ComprasEstadosSeeder {

    @Bean
    @Transactional
    CommandLineRunner crearEstadosCompras(EstadoSolicitudCompraRepository estadoSolicitudCompraRepository,
                                          EstadoCotizacionRepository estadoCotizacionRepository,
                                          EstadoComprobanteCompraRepository estadoComprobanteCompraRepository,
                                          MetodoPagoRepository metodoPagoRepository) {
        return args -> {
            crearEstadoSolicitud(estadoSolicitudCompraRepository, "Pendiente");
            crearEstadoSolicitud(estadoSolicitudCompraRepository, "Aprobada");
            crearEstadoSolicitud(estadoSolicitudCompraRepository, "Rechazada");
            crearEstadoSolicitud(estadoSolicitudCompraRepository, "Cotizado");

            crearEstadoCotizacion(estadoCotizacionRepository, "Pendiente");
            crearEstadoCotizacion(estadoCotizacionRepository, "En revision");
            crearEstadoCotizacion(estadoCotizacionRepository, "Aceptada");
            crearEstadoCotizacion(estadoCotizacionRepository, "Rechazada");
            crearEstadoCotizacion(estadoCotizacionRepository, "Pagado");

            crearEstadoComprobante(estadoComprobanteCompraRepository, "Pagado");
            crearEstadoComprobante(estadoComprobanteCompraRepository, "Pendiente");

            crearMetodoPago(metodoPagoRepository, "Yape");
            crearMetodoPago(metodoPagoRepository, "Efectivo");
        };
    }

    private void crearEstadoSolicitud(EstadoSolicitudCompraRepository repository, String nombre) {
        repository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> repository.save(EstadoSolicitudCompra.builder()
                        .nombre(nombre)
                        .build()));
    }

    private void crearEstadoCotizacion(EstadoCotizacionRepository repository, String nombre) {
        repository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> repository.save(EstadoCotizacion.builder()
                        .nombre(nombre)
                        .build()));
    }

    private void crearEstadoComprobante(EstadoComprobanteCompraRepository repository, String nombre) {
        repository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> repository.save(EstadoComprobanteCompra.builder()
                        .nombre(nombre)
                        .build()));
    }

    private void crearMetodoPago(MetodoPagoRepository repository, String nombre) {
        repository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> repository.save(MetodoPago.builder()
                        .nombre(nombre)
                        .build()));
    }
}
