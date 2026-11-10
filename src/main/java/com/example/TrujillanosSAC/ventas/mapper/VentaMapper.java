package com.example.TrujillanosSAC.ventas.mapper;

import com.example.TrujillanosSAC.ventas.dto.response.*;
import com.example.TrujillanosSAC.ventas.model.*;

public final class VentaMapper {

    private VentaMapper() {
    }

    public static CatalogoResponseDTO catalogo(Catalogo c) {
        return new CatalogoResponseDTO(c.getId(), c.getCodigo(), c.getNombre());
    }

    public static DetallePedidoResponseDTO detalle(DetallePedido d) {
        return new DetallePedidoResponseDTO(d.getIdDetalle(), d.getProducto().getIdProducto(),
                d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }

    public static PedidoResponseDTO pedido(Pedido p) {
        return new PedidoResponseDTO(p.getIdPedido(), p.getFechaRegistro(), p.getCliente().getIdPersona(),
                catalogo(p.getEstado()), p.getTotal(),
                p.getDetalles().stream().map(VentaMapper::detalle).toList());
    }

    public static PagoResponseDTO pago(Pago p) {
        return new PagoResponseDTO(p.getIdPago(), p.getPedido().getIdPedido(), catalogo(p.getMetodoPago()),
                catalogo(p.getEstado()), p.getFechaPago(), p.getMonto(), p.getMoneda(),
                p.getReferenciaExterna(), p.getMensaje());
    }

    public static ComprobanteVentaResponseDTO comprobante(ComprobanteVenta c) {
        return new ComprobanteVentaResponseDTO(c.getIdComprobante(), c.getPedido().getIdPedido(),
                catalogo(c.getTipo()), catalogo(c.getEstado()), c.getSerie(), c.getNumero(),
                c.getFechaEmision(), c.getTotal(), c.getEstadoSunat().name());
    }
}
