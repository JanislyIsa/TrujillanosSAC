package com.example.TrujillanosSAC.ventas.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Todo lo que necesita el ticket (comprobante de pago) de un pedido pagado, ya armado por el backend:
 * el frontend y el correo no toman datos de la sesión.
 *
 * @param tipoComprobante BOLETA o FACTURA; null si el pedido todavía no tiene comprobante emitido
 * @param serie           B001 / F001 (null si no hay comprobante)
 * @param numero          correlativo de 8 dígitos, por ejemplo 00000012 (null si no hay comprobante)
 * @param leyenda         aviso legal; mientras no exista facturación electrónica: no válido ante SUNAT
 */
public record ComprobantePagoDTO(
        Negocio negocio,
        String tipoComprobante,
        String nombreComprobante,
        String serie,
        String numero,
        LocalDateTime fechaEmision,
        Long pedidoId,
        LocalDateTime fechaPedido,
        String estadoPedido,
        ClienteComprobante cliente,
        List<Item> items,
        int unidades,
        BigDecimal total,
        String moneda,
        String metodoPago,
        String referenciaOperacion,
        LocalDateTime fechaPago,
        String estadoSunat,
        String leyenda) {

    public record Negocio(String razonSocial, String ruc, String direccion) {
    }

    public record ClienteComprobante(String nombreCompleto, String tipoDocumento, String numeroDocumento,
                                     String correo) {
    }

    public record Item(Long productoId, String descripcion, String presentacion, Integer cantidad,
                       BigDecimal precioUnitario, BigDecimal subtotal) {
    }
}
