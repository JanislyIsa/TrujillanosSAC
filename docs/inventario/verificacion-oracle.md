# Verificación de la instalación Oracle y los diagramas

Fecha: 3 de noviembre de 2026.

## Comprobaciones realizadas

- `mvnw.cmd -o test`: **20 pruebas aprobadas**, cero fallos y cero errores. Incluye los flujos de inventario, permisos, CSRF, salidas concurrentes y la persistencia del comprobante después de limpiar el contexto JPA. Se comprobó que consultar de nuevo no crea otro comprobante y que una salida rechazada no lo inserta.
- `mvnw.cmd package -DskipTests`: **BUILD SUCCESS** después de las pruebas. JAR ejecutable en `target/TrujillanosSAC-0.0.1-SNAPSHOT.jar`.
- Revisión de las entidades: **38 tablas**, incluyendo `USUARIO_ROL`; todas las tablas y columnas mapeadas por JPA aparecen en `01_tablas.sql`.
- Instalación SQL ejecutada con SQL*Plus en **Oracle Free**, en un contenedor temporal separado de `oracle-local`, sin montar volúmenes del usuario ni publicar puertos. Se crearon usuario, tablas, índices, catálogos y datos demo como propietario.
- Se comprobó desde un usuario nuevo que los permisos finales `CREATE SESSION`, `CREATE TABLE` y `CREATE SEQUENCE`, más la cuota, permiten crear columnas de identidad y generar el ID 1000.
- Datos iniciales: ocho comprobantes, cuatro notificaciones para compras y dos líneas en la solicitud de ejemplo. Restricciones habilitadas y validadas; sin objetos inválidos ni saldos de movimientos inconsistentes.
- Oracle rechazó stock negativo, cantidades cero, almacén inexistente y comprobante duplicado. Las operaciones de prueba se revirtieron.
- V3 se ejecutó en otro esquema temporal que simulaba movimientos existentes. Generó `CM-001`, `CM-999`, `CM-1000` y `CM-10000`, y admitió responsable/motivo históricos nulos.

La primera ejecución detectó que Oracle requería `CREATE SEQUENCE` para la identidad. Se agregó el permiso al script de usuario y se verificó de nuevo con un usuario nuevo.

## Alcance de la evidencia

Las pruebas automáticas del backend usan H2 en modo Oracle. La ejecución SQL y la migración se verificaron directamente en Oracle Free. No se inició el backend contra ese contenedor, ni se migró ni modificó la base existente del usuario. La guía proporciona la conexión para su instalación; otras versiones/servicios de Oracle requieren ajustar el puerto, la PDB y, si corresponde, los tablespaces.

La correspondencia de RF25–RF56 y las diferencias respecto a los mensajes literales del PDF están en `conformidad-diagramas.md`.
