package com.example.TrujillanosSAC.ventas;

import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.usuarios.model.TipoDocumento;
import com.example.TrujillanosSAC.usuarios.repository.ClienteRepository;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.inventario.model.Producto;
import com.example.TrujillanosSAC.inventario.model.TipoMovimiento;
import com.example.TrujillanosSAC.inventario.repository.ProductoRepository;
import com.example.TrujillanosSAC.inventario.service.MovimientoInventarioService;
import com.example.TrujillanosSAC.ventas.dto.request.*;
import com.example.TrujillanosSAC.ventas.model.*;
import com.example.TrujillanosSAC.ventas.model.codigo.*;
import com.example.TrujillanosSAC.ventas.repository.*;
import com.example.TrujillanosSAC.ventas.service.*;
import com.example.TrujillanosSAC.ventas.service.facturacion.SinFacturacionElectronica;
import com.example.TrujillanosSAC.ventas.service.impl.*;
import com.example.TrujillanosSAC.ventas.service.pasarela.PasarelaPago;
import com.example.TrujillanosSAC.ventas.service.pasarela.PasarelaSimulada;
import com.example.TrujillanosSAC.ventas.service.pasarela.ResultadoCobro;
import com.example.TrujillanosSAC.ventas.service.pasarela.SolicitudCobro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class VentasServiceTests {

    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final ProductoRepository productos = mock(ProductoRepository.class);
    private final ClienteRepository clientes = mock(ClienteRepository.class);
    private final PagoRepository pagos = mock(PagoRepository.class);
    private final ComprobanteVentaRepository comprobantes = mock(ComprobanteVentaRepository.class);
    private final TipoComprobanteVentaRepository tipos = mock(TipoComprobanteVentaRepository.class);
    private final EstadoPedidoService estadosPedido = mock(EstadoPedidoService.class);
    private final EstadoPagoService estadosPago = mock(EstadoPagoService.class);
    private final MetodoPagoService metodos = mock(MetodoPagoService.class);
    private final EstadoComprobanteVentaService estadosComprobante = mock(EstadoComprobanteVentaService.class);
    private final MovimientoInventarioService movimientos = mock(MovimientoInventarioService.class);
    private final AccesoVentas acceso = mock(AccesoVentas.class);

    private final ActorVenta clienteActor = new ActorVenta("cliente", false);
    private final ActorVenta empleadoActor = new ActorVenta("empleado", true);
    private final AtomicReference<Pago> pagoGuardado = new AtomicReference<>();

    private static <E extends Catalogo> E catalogo(Supplier<E> fabrica, String codigo) {
        E e = fabrica.get();
        e.setCodigo(codigo);
        e.setNombre(codigo);
        return e;
    }

    @BeforeEach
    void configurar() {
        when(estadosPedido.porCodigo(any())).thenAnswer(i -> catalogo(EstadoPedido::new, ((CodigoCatalogo) i.getArgument(0)).name()));
        when(estadosPago.porCodigo(any())).thenAnswer(i -> catalogo(EstadoPago::new, ((CodigoCatalogo) i.getArgument(0)).name()));
        when(estadosComprobante.porCodigo(any())).thenAnswer(i -> catalogo(EstadoComprobanteVenta::new, ((CodigoCatalogo) i.getArgument(0)).name()));
        when(metodos.porCodigo(MetodoPagoCodigo.TARJETA)).thenReturn(catalogo(MetodoPago::new, "TARJETA"));
        when(metodos.buscarPorCodigo(MetodoPagoCodigo.TARJETA)).thenReturn(Optional.of(catalogo(MetodoPago::new, "TARJETA")));
        when(metodos.buscarPorCodigo(MetodoPagoCodigo.YAPE)).thenReturn(Optional.of(catalogo(MetodoPago::new, "YAPE")));
        when(acceso.idPersona(clienteActor)).thenReturn(1L);
        when(clientes.findById(1L)).thenReturn(Optional.of(cliente("DNI", "12345678")));
        when(pedidos.save(any())).thenAnswer(i -> {
            Pedido p = i.getArgument(0);
            p.setIdPedido(50L);
            return p;
        });
        when(pagos.save(any())).thenAnswer(i -> {
            Pago p = i.getArgument(0);
            p.setIdPago(7L);
            pagoGuardado.set(p);
            return p;
        });
        when(pagos.findById(7L)).thenAnswer(i -> Optional.ofNullable(pagoGuardado.get()));
    }

    private static Cliente cliente(String tipoDocumento, String numero) {
        TipoDocumento documento = TipoDocumento.builder().nombre(tipoDocumento).abreviatura(tipoDocumento)
                .longitud(numero.length()).build();
        Persona persona = Persona.builder().idPersona(1L).nombre("Ana").apellido("Prueba").telefono("999999999")
                .documento(documento).numDoc(numero).build();
        return Cliente.builder().idPersona(1L).persona(persona).build();
    }

    private static Producto producto(Long id, int stock, String precio) {
        Producto p = new Producto();
        p.setIdProducto(id);
        p.setNombre("Producto " + id);
        p.setActivo(true);
        p.setStock(stock);
        p.setPrecio(new BigDecimal(precio));
        return p;
    }

    private Pedido pedido(Long id, EstadoPedidoCodigo estado, Cliente cliente) {
        Pedido p = new Pedido();
        p.setIdPedido(id);
        p.setCliente(cliente);
        p.setEstado(catalogo(EstadoPedido::new, estado.name()));
        p.setFechaRegistro(LocalDateTime.now());
        p.setTotal(new BigDecimal("37.50"));
        DetallePedido d = new DetallePedido();
        d.setProducto(producto(10L, 5, "12.50"));
        d.setCantidad(3);
        d.setPrecioUnitario(new BigDecimal("12.50"));
        d.setSubtotal(new BigDecimal("37.50"));
        p.agregarDetalle(d);
        when(pedidos.bloquear(id)).thenReturn(Optional.of(p));
        return p;
    }

    private PedidoServiceImpl pedidoService() {
        return new PedidoServiceImpl(pedidos, productos, clientes, estadosPedido, movimientos, acceso);
    }

    private PagoServiceImpl pagoService(PasarelaPago pasarela) {
        var transacciones = new PagoTransacciones(pagos, pedidos, estadosPedido, estadosPago, metodos, acceso);
        return new PagoServiceImpl(transacciones, pasarela, pagos, acceso);
    }

    private ComprobanteVentaServiceImpl comprobanteService() {
        return new ComprobanteVentaServiceImpl(comprobantes, pedidos, tipos, estadosComprobante,
                new SinFacturacionElectronica(), acceso);
    }

    // ---------- Pedidos ----------

    @Test
    void pedidoAgrupaProductosCalculaTotalYRegistraLaSalidaEnInventario() {
        when(productos.findByIdForUpdate(10L)).thenReturn(Optional.of(producto(10L, 5, "12.50")));
        var request = new PedidoRequestDTO(List.of(
                new DetallePedidoRequestDTO(10L, 2), new DetallePedidoRequestDTO(10L, 1)));

        var pedido = pedidoService().crear(request, clienteActor);

        assertThat(pedido.total()).isEqualByComparingTo("37.50");
        assertThat(pedido.detalles()).hasSize(1);
        assertThat(pedido.detalles().get(0).cantidad()).isEqualTo(3);
        assertThat(pedido.estado().codigo()).isEqualTo("PENDIENTE");
        assertThat(pedido.clienteId()).isEqualTo(1L);
        verify(movimientos).registrar(argThat(m -> m.tipoMovimiento() == TipoMovimiento.SALIDA
                && m.idItem() == 10L && m.cantidad() == 3 && m.motivo().equals("Venta pedido #50")), eq("cliente"));
    }

    @Test
    void pedidoSinStockNoSeGuardaNiMueveInventario() {
        when(productos.findByIdForUpdate(10L)).thenReturn(Optional.of(producto(10L, 2, "12.50")));
        var request = new PedidoRequestDTO(List.of(new DetallePedidoRequestDTO(10L, 3)));
        assertThatThrownBy(() -> pedidoService().crear(request, clienteActor))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("Stock insuficiente");
        verify(pedidos, never()).save(any());
        verifyNoInteractions(movimientos);
    }

    @Test
    void usuarioQueNoEsClienteNoPuedeCrearPedidos() {
        ActorVenta otro = new ActorVenta("proveedor", false);
        when(acceso.idPersona(otro)).thenReturn(2L);
        when(clientes.findById(2L)).thenReturn(Optional.empty());
        var request = new PedidoRequestDTO(List.of(new DetallePedidoRequestDTO(10L, 1)));
        assertThatThrownBy(() -> pedidoService().crear(request, otro))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("cliente");
        verifyNoInteractions(movimientos);
    }

    @Test
    void cancelarDevuelveElStockUnaSolaVez() {
        Pedido pedido = pedido(50L, EstadoPedidoCodigo.PENDIENTE, cliente("DNI", "12345678"));
        var service = pedidoService();

        assertThat(service.cancelar(50L, clienteActor).estado().codigo()).isEqualTo("CANCELADO");
        assertThatThrownBy(() -> service.cancelar(50L, clienteActor)).isInstanceOf(ReglaNegocioException.class);

        verify(acceso, times(2)).verificarPedido(pedido, clienteActor);
        verify(movimientos, times(1)).registrar(argThat(m -> m.tipoMovimiento() == TipoMovimiento.INGRESO
                && m.cantidad() == 3), eq("cliente"));
    }

    @Test
    void tareaAutomaticaSoloCancelaPedidosQueSiguenPendientes() {
        pedido(50L, EstadoPedidoCodigo.PENDIENTE, cliente("DNI", "12345678"));
        pedido(51L, EstadoPedidoCodigo.PROCESANDO_PAGO, cliente("DNI", "12345678"));
        when(pedidos.idsPorEstadoRegistradosAntesDe(eq("PENDIENTE"), any())).thenReturn(List.of(50L, 51L));

        assertThat(pedidoService().cancelarVencidos(LocalDateTime.now())).isEqualTo(1);
        verify(movimientos, times(1)).registrar(any(), eq("sistema"));
    }

    @Test
    void empleadoAvanzaElPedidoSoloEnOrden() {
        pedido(50L, EstadoPedidoCodigo.PAGADO, cliente("DNI", "12345678"));
        var service = pedidoService();
        assertThatThrownBy(() -> service.cambiarEstado(50L, EstadoPedidoCodigo.PENDIENTE))
                .isInstanceOf(ReglaNegocioException.class);
        assertThat(service.cambiarEstado(50L, EstadoPedidoCodigo.ENTREGADO).estado().codigo()).isEqualTo("ENTREGADO");
    }

    // ---------- Pagos ----------

    @Test
    void pagoConTarjetaAprobadoCobraElTotalEnCentimosYMarcaElPedidoPagado() {
        Pedido pedido = pedido(50L, EstadoPedidoCodigo.PENDIENTE, cliente("DNI", "12345678"));
        AtomicReference<SolicitudCobro> solicitud = new AtomicReference<>();
        PasarelaPago pasarela = s -> {
            assertThat(pedido.estaEn(EstadoPedidoCodigo.PROCESANDO_PAGO)).isTrue();
            solicitud.set(s);
            return ResultadoCobro.aprobado("chr_test_1", "Venta exitosa", MetodoPagoCodigo.TARJETA);
        };

        var pago = pagoService(pasarela).pagarConCulqi(new PagoCulqiRequestDTO(50L, "tkn_test_1", "ana@example.test"), clienteActor);

        assertThat(solicitud.get().montoCentimos()).isEqualTo(3750L);
        assertThat(solicitud.get().moneda()).isEqualTo("PEN");
        assertThat(pago.estado().codigo()).isEqualTo("APROBADO");
        assertThat(pago.referenciaExterna()).isEqualTo("chr_test_1");
        assertThat(pago.monto()).isEqualByComparingTo("37.50");
        assertThat(pedido.estaEn(EstadoPedidoCodigo.PAGADO)).isTrue();
    }

    @Test
    void pagoConYapeQuedaRegistradoComoYape() {
        Pedido pedido = pedido(50L, EstadoPedidoCodigo.PENDIENTE, cliente("DNI", "12345678"));
        var pago = pagoService(new PasarelaSimulada())
                .pagarConCulqi(new PagoCulqiRequestDTO(50L, "tkn_yape_123", "ana@example.test"), clienteActor);
        assertThat(pago.estado().codigo()).isEqualTo("APROBADO");
        assertThat(pago.metodoPago().codigo()).isEqualTo("YAPE");
        assertThat(pedido.estaEn(EstadoPedidoCodigo.PAGADO)).isTrue();
    }

    @Test
    void pagoRechazadoDejaElPedidoPendienteParaReintentar() {
        Pedido pedido = pedido(50L, EstadoPedidoCodigo.PENDIENTE, cliente("DNI", "12345678"));
        var pago = pagoService(new PasarelaSimulada())
                .pagarConCulqi(new PagoCulqiRequestDTO(50L, "tkn_rechazo", "ana@example.test"), clienteActor);
        assertThat(pago.estado().codigo()).isEqualTo("RECHAZADO");
        assertThat(pago.mensaje()).isNotBlank();
        assertThat(pedido.estaEn(EstadoPedidoCodigo.PENDIENTE)).isTrue();
    }

    @Test
    void fallaInesperadaDeLaPasarelaNoTrabaElPedido() {
        Pedido pedido = pedido(50L, EstadoPedidoCodigo.PENDIENTE, cliente("DNI", "12345678"));
        PasarelaPago pasarela = s -> {
            throw new IllegalStateException("caída");
        };
        var pago = pagoService(pasarela).pagarConCulqi(new PagoCulqiRequestDTO(50L, "tkn", "ana@example.test"), clienteActor);
        assertThat(pago.estado().codigo()).isEqualTo("ERROR");
        assertThat(pedido.estaEn(EstadoPedidoCodigo.PENDIENTE)).isTrue();
    }

    @Test
    void noSePuedePagarDosVecesElMismoPedido() {
        pedido(50L, EstadoPedidoCodigo.PROCESANDO_PAGO, cliente("DNI", "12345678"));
        PasarelaPago pasarela = mock(PasarelaPago.class);
        assertThatThrownBy(() -> pagoService(pasarela)
                .pagarConCulqi(new PagoCulqiRequestDTO(50L, "tkn", "ana@example.test"), clienteActor))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("en proceso");
        verifyNoInteractions(pasarela);
    }

    // ---------- Comprobantes ----------

    @Test
    void boletaYFacturaUsanSuSerieYCorrelativo() {
        pedido(50L, EstadoPedidoCodigo.PAGADO, cliente("DNI", "12345678"));
        pedido(60L, EstadoPedidoCodigo.ENTREGADO, cliente("RUC", "20123456789"));
        when(tipos.bloquearPorCodigo("BOLETA")).thenReturn(Optional.of(catalogo(TipoComprobanteVenta::new, "BOLETA")));
        when(tipos.bloquearPorCodigo("FACTURA")).thenReturn(Optional.of(catalogo(TipoComprobanteVenta::new, "FACTURA")));
        ComprobanteVenta anterior = new ComprobanteVenta();
        anterior.setNumero("00000041");
        when(comprobantes.findTopBySerieOrderByNumeroDesc("B001")).thenReturn(Optional.of(anterior));
        when(comprobantes.findTopBySerieOrderByNumeroDesc("F001")).thenReturn(Optional.empty());
        when(comprobantes.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        var service = comprobanteService();

        var boleta = service.emitir(new ComprobanteVentaRequestDTO(50L, TipoComprobanteCodigo.BOLETA), empleadoActor);
        var factura = service.emitir(new ComprobanteVentaRequestDTO(60L, TipoComprobanteCodigo.FACTURA), empleadoActor);

        assertThat(boleta.serie() + "-" + boleta.numero()).isEqualTo("B001-00000042");
        assertThat(factura.serie() + "-" + factura.numero()).isEqualTo("F001-00000001");
        assertThat(boleta.estadoSunat()).isEqualTo("NO_ENVIADO");
        assertThat(boleta.total()).isEqualByComparingTo("37.50");
    }

    @Test
    void facturaExigeRucYSoloSeEmiteDePedidosPagados() {
        pedido(50L, EstadoPedidoCodigo.PAGADO, cliente("DNI", "12345678"));
        pedido(51L, EstadoPedidoCodigo.PENDIENTE, cliente("RUC", "20123456789"));
        var service = comprobanteService();

        assertThatThrownBy(() -> service.emitir(new ComprobanteVentaRequestDTO(50L, TipoComprobanteCodigo.FACTURA), empleadoActor))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("RUC");
        assertThatThrownBy(() -> service.emitir(new ComprobanteVentaRequestDTO(51L, TipoComprobanteCodigo.BOLETA), empleadoActor))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("pagado");
        verify(comprobantes, never()).saveAndFlush(any());
    }
}
