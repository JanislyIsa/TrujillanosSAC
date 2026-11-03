package com.example.TrujillanosSAC.Config;

import com.example.TrujillanosSAC.Model.EstadoPedido;
import com.example.TrujillanosSAC.Repository.EstadoPedidoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="app.seed.habilitado", havingValue="true", matchIfMissing=true)
@Configuration
public class EstadoPedidoSeeder {

    @Bean
    @Transactional
    CommandLineRunner crearEstadosPedido(EstadoPedidoRepository estadoPedidoRepository) {
        return args -> {
            List<String> estados = List.of(
                    "PENDIENTE",
                    "PAGO_PENDIENTE",
                    "PAGADO",
                    "LISTO_PARA_RECOJO",
                    "ENTREGADO",
                    "CANCELADO"
            );

            for (String estado : estados) {
                if (!estadoPedidoRepository.existsByNombreIgnoreCase(estado)) {
                    estadoPedidoRepository.save(EstadoPedido.builder()
                            .nombre(estado)
                            .build());
                }
            }
        };
    }
}
