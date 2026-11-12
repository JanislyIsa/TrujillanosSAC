-- Ejecutar una sola vez en la base Oracle antes de desplegar el portal de proveedor.
-- Conserva comprobantes existentes; el número y el PDF son opcionales para registros previos.
ALTER TABLE COMPROBANTE_COMPRA ADD (
    NUMERO_FACTURA VARCHAR2(50 CHAR),
    ARCHIVO_FACTURA BLOB
);
ALTER TABLE COTIZACION ADD (DESCRIPCION VARCHAR2(500 CHAR));
