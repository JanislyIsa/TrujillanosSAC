-- Para bases YA creadas: agrega el correo del cliente (registro desde la tienda).
-- En una instalacion nueva no hace falta: 01_tablas.sql ya lo incluye.
-- Ejecutar UNA sola vez con el usuario dueño del esquema (eltrujillano_app).
ALTER TABLE CLIENTES ADD (CORREO VARCHAR2(100 CHAR));
-- Un correo no puede repetirse (sin distinguir mayusculas); los clientes sin correo (NULL) no chocan entre si.
CREATE UNIQUE INDEX UX_CLIENTES_CORREO ON CLIENTES (LOWER(CORREO));
