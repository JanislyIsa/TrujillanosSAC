package com.example.TrujillanosSAC.ventas.model.codigo;

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

    /** Transiciones que un empleado puede hacer a mano. Pagar y cancelar tienen su propio flujo. */
    public boolean puedePasarManualmenteA(EstadoPedidoCodigo destino) {
        return (this == PAGADO && (destino == LISTO_PARA_RECOJO || destino == ENTREGADO))
                || (this == LISTO_PARA_RECOJO && destino == ENTREGADO);
    }
}
