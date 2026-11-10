package com.example.TrujillanosSAC.ventas;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.ventas.dto.request.CatalogoRequestDTO;
import com.example.TrujillanosSAC.ventas.model.EstadoPedido;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.repository.EstadoPedidoRepository;
import com.example.TrujillanosSAC.ventas.service.EstadoPedidoService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/** Prueba la base genérica de catálogos usando EstadoPedidoService como ejemplo. */
class CatalogoServiceTests {

    private final EstadoPedidoRepository repository = mock(EstadoPedidoRepository.class);
    private final EstadoPedidoService service = new EstadoPedidoService(repository);

    private static EstadoPedido estado(Long id, String codigo, String nombre) {
        EstadoPedido e = new EstadoPedido();
        e.setId(id);
        e.setCodigo(codigo);
        e.setNombre(nombre);
        return e;
    }

    @Test
    void renombraSinEspaciosYSinTocarElCodigo() {
        EstadoPedido pagado = estado(3L, "PAGADO", "Pagado");
        when(repository.findById(3L)).thenReturn(Optional.of(pagado));
        when(repository.findFirstByNombreIgnoreCase("Pago confirmado")).thenReturn(Optional.empty());

        var respuesta = service.actualizar(3L, new CatalogoRequestDTO("  Pago confirmado "));

        assertThat(respuesta.nombre()).isEqualTo("Pago confirmado");
        assertThat(respuesta.codigo()).isEqualTo("PAGADO");
    }

    @Test
    void permiteGuardarElMismoNombrePeroNoElDeOtroRegistro() {
        EstadoPedido pagado = estado(3L, "PAGADO", "Pagado");
        when(repository.findById(3L)).thenReturn(Optional.of(pagado));
        when(repository.findFirstByNombreIgnoreCase("pagado")).thenReturn(Optional.of(pagado));
        when(repository.findFirstByNombreIgnoreCase("Entregado")).thenReturn(Optional.of(estado(5L, "ENTREGADO", "Entregado")));

        assertThat(service.actualizar(3L, new CatalogoRequestDTO("pagado")).nombre()).isEqualTo("pagado");
        assertThatThrownBy(() -> service.actualizar(3L, new CatalogoRequestDTO("Entregado")))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("Entregado");
    }

    @Test
    void avisaSiNoExisteElIdOSiFaltaConfigurarUnCodigo() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtener(99L)).isInstanceOf(RecursosNoEncontradosException.class);

        when(repository.findByCodigo("PENDIENTE")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.porCodigo(EstadoPedidoCodigo.PENDIENTE))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("PENDIENTE");
    }
}
