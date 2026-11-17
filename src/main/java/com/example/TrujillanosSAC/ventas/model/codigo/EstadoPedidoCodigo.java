package com.example.TrujillanosSAC.ventas.model.codigo;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoPedidoCodigo implements CodigoCatalogo {
    PENDIENTE("Pendiente"),
    PROCESANDO_PAGO("Procesando pago"),
    PAGADO("Pagado"),
    LISTO_PARA_RECOJO("Listo para recojo"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String nombre;

    EstadoPedidoCodigo(String nombre) { this.nombre = nombre; }

    @Override public String nombre() { return nombre; }

    /**
     * Estados que cuentan como venta en los reportes: el pedido ya se cobró.
     * PENDIENTE, PROCESANDO_PAGO y CANCELADO no son ventas (equivalen a lo "ANULADO" de la clase).
     */
    public static Set<EstadoPedidoCodigo> vendidos() {
        return EnumSet.of(PAGADO, LISTO_PARA_RECOJO, ENTREGADO);
    }

    /** Transiciones que un empleado puede hacer a mano. Pagar y cancelar tienen su propio flujo. */
    public boolean puedePasarManualmenteA(EstadoPedidoCodigo destino) {
        return (this == PAGADO && (destino == LISTO_PARA_RECOJO || destino == ENTREGADO))
                || (this == LISTO_PARA_RECOJO && destino == ENTREGADO);
    }
}
