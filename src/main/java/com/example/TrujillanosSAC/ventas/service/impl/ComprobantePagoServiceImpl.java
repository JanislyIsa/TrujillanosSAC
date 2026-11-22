package com.example.TrujillanosSAC.ventas.service.impl;

import com.example.TrujillanosSAC.exception.RecursosNoEncontradosException;
import com.example.TrujillanosSAC.exception.ReglaNegocioException;
import com.example.TrujillanosSAC.usuarios.model.Cliente;
import com.example.TrujillanosSAC.usuarios.model.Persona;
import com.example.TrujillanosSAC.ventas.dto.response.ComprobantePagoDTO;
import com.example.TrujillanosSAC.ventas.model.ComprobanteVenta;
import com.example.TrujillanosSAC.ventas.model.DetallePedido;
import com.example.TrujillanosSAC.ventas.model.Pago;
import com.example.TrujillanosSAC.ventas.model.Pedido;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPagoCodigo;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoPedidoCodigo;
import com.example.TrujillanosSAC.ventas.model.codigo.EstadoSunat;
import com.example.TrujillanosSAC.ventas.model.codigo.TipoComprobanteCodigo;
import com.example.TrujillanosSAC.ventas.repository.ComprobanteVentaRepository;
import com.example.TrujillanosSAC.ventas.repository.PagoRepository;
import com.example.TrujillanosSAC.ventas.repository.PedidoRepository;
import com.example.TrujillanosSAC.ventas.security.ActorVenta;
import com.example.TrujillanosSAC.ventas.service.service.ComprobantePagoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service("ventasComprobantePagoService")
@Transactional(readOnly = true)
public class ComprobantePagoServiceImpl implements ComprobantePagoService {

    static final String MONEDA = "PEN";
    static final String LEYENDA_INTERNA =
            "Representación interna del comprobante. No válida ante SUNAT mientras no se envíe electrónicamente.";

    private final PedidoRepository pedidos;
    private final PagoRepository pagos;
    private final ComprobanteVentaRepository comprobantes;
    private final AccesoVentas acceso;
    private final ComprobantePagoDTO.Negocio negocio;

    public ComprobantePagoServiceImpl(PedidoRepository pedidos, PagoRepository pagos,
                                      ComprobanteVentaRepository comprobantes, AccesoVentas acceso,
                                      @Value("${ventas.negocio.razon-social:El Trujillano S.A.C.}") String razonSocial,
                                      @Value("${ventas.negocio.ruc:}") String ruc,
                                      @Value("${ventas.negocio.direccion:}") String direccion) {
        this.pedidos = pedidos;
        this.pagos = pagos;
        this.comprobantes = comprobantes;
        this.acceso = acceso;
        this.negocio = new ComprobantePagoDTO.Negocio(razonSocial, vacioANull(ruc), vacioANull(direccion));
    }

    @Override
    public ComprobantePagoDTO obtener(Long pedidoId, ActorVenta actor) {
        Pedido pedido = buscar(pedidoId);
        acceso.verificarPedido(pedido, actor);
        return armar(pedido);
    }

    @Override
    public ComprobantePagoDTO armar(Long pedidoId) {
        return armar(buscar(pedidoId));
    }

    private Pedido buscar(Long pedidoId) {
        return pedidos.buscarCompleto(pedidoId)
                .orElseThrow(() -> new RecursosNoEncontradosException("Pedido no encontrado"));
    }

    private ComprobantePagoDTO armar(Pedido pedido) {
        if (!(pedido.estaEn(EstadoPedidoCodigo.PAGADO) || pedido.estaEn(EstadoPedidoCodigo.LISTO_PARA_RECOJO)
                || pedido.estaEn(EstadoPedidoCodigo.ENTREGADO))) {
            throw new ReglaNegocioException("El comprobante de pago está disponible cuando el pedido está pagado");
        }
        Long id = pedido.getIdPedido();
        Optional<ComprobanteVenta> comprobante = comprobantes.findByPedido_IdPedido(id);
        // listarPorPedido viene ordenado del más reciente al más antiguo: el primero aprobado es el que cobró.
        Optional<Pago> pago = pagos.listarPorPedido(id).stream()
                .filter(p -> EstadoPagoCodigo.APROBADO.name().equals(p.getEstado().getCodigo()))
                .findFirst();

        List<ComprobantePagoDTO.Item> items = pedido.getDetalles().stream().map(ComprobantePagoServiceImpl::item).toList();
        int unidades = pedido.getDetalles().stream().mapToInt(DetallePedido::getCantidad).sum();
        EstadoSunat estadoSunat = comprobante.map(ComprobanteVenta::getEstadoSunat).orElse(EstadoSunat.NO_ENVIADO);

        return new ComprobantePagoDTO(
                negocio,
                comprobante.map(c -> c.getTipo().getCodigo()).orElse(null),
                comprobante.map(c -> nombreTipo(c.getTipo().getCodigo())).orElse(null),
                comprobante.map(ComprobanteVenta::getSerie).orElse(null),
                comprobante.map(ComprobanteVenta::getNumero).orElse(null),
                comprobante.map(ComprobanteVenta::getFechaEmision).orElse(null),
                id,
                pedido.getFechaRegistro(),
                pedido.getEstado().getNombre(),
                cliente(pedido.getCliente()),
                items,
                unidades,
                pedido.getTotal(),
                pago.map(Pago::getMoneda).orElse(MONEDA),
                pago.map(p -> p.getMetodoPago().getNombre()).orElse(null),
                pago.map(Pago::getReferenciaExterna).orElse(null),
                pago.map(Pago::getFechaPago).orElse(null),
                estadoSunat.name(),
                estadoSunat == EstadoSunat.ACEPTADO ? null : LEYENDA_INTERNA);
    }

    private static ComprobantePagoDTO.Item item(DetallePedido d) {
        var producto = d.getProducto();
        String presentacion = producto.getPresentacion() == null ? null : producto.getPresentacion().getNombre();
        return new ComprobantePagoDTO.Item(producto.getIdProducto(), producto.getNombre(), presentacion,
                d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }

    private static ComprobantePagoDTO.ClienteComprobante cliente(Cliente cliente) {
        Persona persona = cliente.getPersona();
        String nombre = (persona.getNombre() + " " + (persona.getApellido() == null ? "" : persona.getApellido())).trim();
        String tipoDocumento = persona.getDocumento() == null ? null : persona.getDocumento().getAbreviatura();
        return new ComprobantePagoDTO.ClienteComprobante(nombre, tipoDocumento, persona.getNumDoc(), cliente.getCorreo());
    }

    private static String nombreTipo(String codigo) {
        for (TipoComprobanteCodigo t : TipoComprobanteCodigo.values()) {
            if (t.name().equals(codigo)) {
                return t.nombre();
            }
        }
        return codigo;
    }

    private static String vacioANull(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
