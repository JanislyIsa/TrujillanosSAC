package com.example.TrujillanosSAC.ventas;

import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.model.PresentacionProducto;
import com.example.TrujillanosSAC.inventario.model.Producto;
import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobantePagoDTO;
import com.example.TrujillanosSAC.ventas.model.*;
import com.example.TrujillanosSAC.ventas.repository.ComprobanteVentaRepository;
import com.example.TrujillanosSAC.ventas.repository.PagoRepository;
import com.example.TrujillanosSAC.ventas.repository.PedidoRepository;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.impl.AccesoVentas;
import com.example.TrujillanosSAC.ventas.service.impl.ComprobantePagoServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ComprobantePagoServiceTests {

    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final PagoRepository pagos = mock(PagoRepository.class);
    private final ComprobanteVentaRepository comprobantes = mock(ComprobanteVentaRepository.class);
    private final AccesoVentas acceso = mock(AccesoVentas.class);
    private final ComprobantePagoServiceImpl servicio = new ComprobantePagoServiceImpl(pedidos, pagos, comprobantes,
            acceso, "El Trujillano S.A.C.", "10327958441", "");
    private final ActorVenta cliente = new ActorVenta("ana", false);

    private static <E extends Catalogo> E catalogo(Supplier<E> fabrica, String codigo, String nombre) {
        E e = fabrica.get();
        e.setCodigo(codigo);
        e.setNombre(nombre);
        return e;
    }

    private Pedido pedido(String estado) {
        Persona persona = Persona.builder().idPersona(7L).nombre("Ana").apellido("Pérez")
                .documento(TipoDocumento.builder().abreviatura("DNI").build()).numDoc("12345678").build();
        Cliente duenio = Cliente.builder().idPersona(7L).persona(persona).correo("ana@correo.com").build();
        Producto producto = Producto.builder().idProducto(1L).nombre("Tetin POP")
                .presentacion(PresentacionProducto.builder().nombre("PAQUETE").build()).build();
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1080L);
        pedido.setFechaRegistro(LocalDateTime.of(2026, 11, 18, 19, 30));
        pedido.setCliente(duenio);
        pedido.setEstado(catalogo(EstadoPedido::new, estado, estado));
        pedido.setTotal(new BigDecimal("17.00"));
        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("8.50"));
        detalle.setSubtotal(new BigDecimal("17.00"));
        pedido.agregarDetalle(detalle);
        return pedido;
    }

    @Test
    void armaElTicketConBoletaPagoYCliente() {
        Pedido pedido = pedido("PAGADO");
        when(pedidos.buscarCompleto(1080L)).thenReturn(Optional.of(pedido));
        ComprobanteVenta boleta = new ComprobanteVenta();
        boleta.setTipo(catalogo(TipoComprobanteVenta::new, "BOLETA", "Boleta"));
        boleta.setSerie("B001");
        boleta.setNumero("00000012");
        when(comprobantes.findByPedido_IdPedido(1080L)).thenReturn(Optional.of(boleta));
        Pago pago = new Pago();
        pago.setEstado(catalogo(EstadoPago::new, "APROBADO", "Aprobado"));
        pago.setMetodoPago(catalogo(MetodoPago::new, "YAPE", "Yape"));
        pago.setReferenciaExterna("chr_test_123");
        pago.setMoneda("PEN");
        when(pagos.listarPorPedido(1080L)).thenReturn(List.of(pago));

        ComprobantePagoDTO ticket = servicio.obtener(1080L, cliente);

        verify(acceso).verificarPedido(pedido, cliente);
        assertThat(ticket.serie() + "-" + ticket.numero()).isEqualTo("B001-00000012");
        assertThat(ticket.nombreComprobante()).isEqualTo("Boleta de venta");
        assertThat(ticket.cliente().nombreCompleto()).isEqualTo("Ana Pérez");
        assertThat(ticket.cliente().numeroDocumento()).isEqualTo("12345678");
        assertThat(ticket.items()).singleElement().satisfies(i -> {
            assertThat(i.descripcion()).isEqualTo("Tetin POP");
            assertThat(i.presentacion()).isEqualTo("PAQUETE");
        });
        assertThat(ticket.unidades()).isEqualTo(2);
        assertThat(ticket.metodoPago()).isEqualTo("Yape");
        assertThat(ticket.referenciaOperacion()).isEqualTo("chr_test_123");
        assertThat(ticket.negocio().direccion()).isNull();
        assertThat(ticket.leyenda()).contains("No válida ante SUNAT");
    }

    @Test
    void pedidoSinPagarNoTieneTicket() {
        when(pedidos.buscarCompleto(1080L)).thenReturn(Optional.of(pedido("PENDIENTE")));

        assertThatThrownBy(() -> servicio.obtener(1080L, cliente))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
