# Backend de inventario

Implementa el módulo de inventario dentro del proyecto TrujillanosSAC. La matriz original usaba RF18–RF49; los PDF de análisis y secuencia aportados posteriormente usan RF25–RF56. La correspondencia y diferencias están documentadas en [conformidad-diagramas.md](conformidad-diagramas.md). Conserva las tablas y relaciones compartidas con compras y ventas.

## Estructura

```text
com.example.TrujillanosSAC.inventario
  config          reloj de Lima, seguridad REST, permisos, CORS y monitor
  controller      adaptadores de las pantallas existentes
    api           endpoints REST para el futuro frontend
  dto             peticiones, respuestas y formularios existentes
  exception       errores HTTP de inventario
  model           productos, insumos, almacenes, movimientos, alertas y solicitudes
  repository      acceso JPA y consultas con bloqueo
  service         contratos de negocio
    impl          implementaciones transaccionales y reglas de stock
```

Usuarios, empleados y proveedores siguen siendo entidades compartidas. Los módulos existentes importan las entidades del paquete `inventario`; no existen copias ni entidades duplicadas.

## Preparación de Oracle

Para crear **usuario, permisos, tablas y datos desde cero**, seguir [la instalación completa](../../database/oracle/README.md). Los pasos siguientes son para un esquema existente.

1. Respaldar el esquema existente y revisar que stock y cantidades previas sean válidos.
2. Ejecutar **una sola vez**, en orden y solo las pendientes, `src/main/resources/db/migration/oracle/V1__inventario.sql`, `V2__inventario_frontend.sql` y `V3__comprobante_movimiento.sql` en ese esquema. Son scripts manuales; el proyecto no incorpora Flyway ni ejecuta migraciones al arrancar. Mantienen los registros históricos y agregan las columnas y las tablas de notificaciones y comprobantes.
3. Configurar las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`. No hay contraseña ni servidor remoto incrustados en la configuración. El valor por defecto de la URL apunta a Oracle local.
4. Verificar que existan almacenes con un empleado responsable, categorías, presentaciones, unidades y el estado de solicitud **Pendiente**. Registrar empleados con rol `EMPLEADO` y cargos `ALMACENERO`, `JEFE_COMPRAS` / `JEFE_DE_COMPRAS` (también nombres con espacios) o `ADMIN` / `ADMINISTRADOR`.
5. Con JDK 21 o superior, ejecutar `./mvnw.cmd spring-boot:run` en Windows. Puerto: `9191`.

`spring.jpa.hibernate.ddl-auto=validate` comprueba el esquema sin alterarlo. Los seeders heredados quedan deshabilitados por defecto; `APP_SEED_HABILITADO=true` los activa si se necesita preparar una base de desarrollo. Revisar sus datos antes de activarlos. Esta entrega no se conectó ni migró una base Oracle remota.

## Contrato y acceso

Importar `docs/inventario/openapi.json` en Postman o un visor OpenAPI. Base: `/api/v1/inventario`.

| Operación | Endpoints | Acceso |
|---|---|---|
| Productos RF25–RF29 | `GET/POST /productos`, `GET/PUT/DELETE /productos/{id}` | Almacenero, administrador |
| Insumos RF30–RF34 | `GET/POST /insumos`, `GET/PUT/DELETE /insumos/{id}` | Almacenero, administrador |
| Stock RF35–RF39 | `GET /stock` | Inventario o compras |
| Movimientos y comprobantes RF40–RF48 | `GET/POST /movimientos`, `GET /movimientos/{id}`, `GET /movimientos/{id}/comprobante` | Almacenero, administrador |
| Alertas RF49–RF51 | `GET /alertas`, `POST /alertas/monitorear` | Lectura: inventario o compras; monitoreo manual: inventario |
| Notificaciones RF52 | `GET /notificaciones`, `PATCH /notificaciones/{id}/leida` | Compras, solo mensajes propios |
| Solicitudes RF53 y RF56 | `GET/POST /solicitudes-compra`, `GET /solicitudes-compra/{id}` | Jefe de compras, administrador |
| Faltantes y llenado automático RF54–RF55 | `GET /solicitudes-compra/faltantes`, `POST /solicitudes-compra/automatica` | Jefe de compras, administrador |
| Opciones para formularios | `GET /catalogos` | Inventario o compras |
| Token de escritura | `GET /csrf` | Público |

El administrador puede operar ambos ámbitos. Los permisos combinan el rol `EMPLEADO` y su cargo en base de datos. Un cliente o proveedor no puede acceder a la API de inventario. La API acepta la sesión de login existente y HTTP Basic; usar HTTPS fuera de desarrollo.

Las escrituras requieren CSRF. Consultar `GET /csrf`, conservar cookies y enviar el valor `token` en el encabezado `headerName` devuelto (`X-XSRF-TOKEN`). En el frontend usar `credentials: 'include'`. Las sesiones utilizan el login existente en `/login`; si este rota o elimina el token, volver a consultar `/csrf`.

CORS admite `http://localhost:5173` y `http://localhost:3000`. Configurar `INVENTARIO_CORS_ORIGENES` como lista de orígenes separados por comas para otras direcciones.

Las listas principales devuelven `{contenido, pagina, tamanio, totalElementos, totalPaginas}`. `pagina` empieza en 0; `tamanio` admite 1–100 y vale 20 por defecto. Productos e insumos admiten `buscar`, `idAlmacen`, `estado`. Stock agrega `tipoItem`. El historial admite `tipoItem`, `idItem`, `tipoMovimiento`, `desde`, `hasta`, `idAlmacen`; las fechas son inclusivas. El filtrado y paginado actuales se hacen sobre las consultas de gestión; para catálogos grandes se deberá llevar esa paginación a SQL.

Los enums de entrada son `PRODUCTO`, `INSUMO`, `INGRESO`, `SALIDA`, `BAJO`, `NORMAL`, `SOBRESTOCK`. El DTO heredado del historial conserva las etiquetas `Producto`, `Insumo`, `Ingreso`, `Salida`. `ItemResponse.idUnidad` representa la presentación del producto o la unidad de medida del insumo según `tipoItem`.

## Reglas de negocio

- Stock y mínimos no negativos; límite mayor o igual al mínimo. Precio de producto positivo, con hasta dos decimales. Nombres de hasta 100 caracteres.
- Stock bajo: `stock < stockMinimo`. Sobrestock: `stock > stockLimite`. Los valores iguales a cada umbral son normales.
- Un alta con `stock > 0` guarda un ingreso inicial en la misma transacción. Al editar, omitir `stock` o enviar su valor actual. Otro valor devuelve `409`; los cambios de existencias requieren movimientos.
- Cada movimiento afecta un producto o insumo. Fecha, hora (America/Lima), responsable y almacén real los determina el servidor. No se admiten cantidades no positivas, stock negativo ni desbordamiento de enteros.
- Se bloquea y refresca el item antes de calcular el saldo. Movimiento, detalle, actualización de stock, alertas y notificaciones se confirman o revierten juntos. Las ventas y cancelaciones existentes utilizan el mismo servicio.
- Se persisten saldo anterior, saldo nuevo, nombre y unidad en el detalle. El comprobante se guarda en `COMPROBANTE_MOVIMIENTO` en la misma transacción, con código único, fecha de emisión, responsable y motivo. Su consulta no genera otro registro. No hay endpoints para modificar o borrar movimientos.
- Los movimientos anteriores a esta versión no tenían saldos guardados. Sus saldos se devuelven como `null`; no se reconstruyen suponiendo un saldo inicial cero.
- Un registro existente conserva su almacén y su presentación/unidad. Registrar otro item para otra ubicación o presentación. Eliminar deja el item inactivo y conserva stock, referencias e historial. Puede reactivarse desde Editar. Los productos inactivos no se venden.
- Las alertas se guardan una vez por episodio y se resuelven cuando el stock vuelve al rango normal o cambia de estado. Cambiar el saldo dentro de un mismo episodio actualiza sus cantidades sin regenerar fecha ni notificación.
- Cada jefe de compras recibe una notificación persistida por alerta. No se envían correos ni mensajes externos. Si aún no hay jefe registrado, el monitor reintenta crear su notificación cuando exista. Los administradores pueden ver alertas y sus propias notificaciones; no reciben automáticamente las destinadas al jefe.
- El monitor revisa niveles cada 60 segundos, con primera ejecución al minuto; las escrituras actualizan las alertas inmediatamente. Propiedades: `inventario.monitor.habilitado`, `inventario.monitor.intervalo-ms`, `inventario.monitor.inicio-ms`.
- La solicitud automática detecta insumos por debajo del mínimo y propone `stockLimite - stock`. La solicitud manual admite cantidades positivas con hasta dos decimales, máximo 100 líneas y sin repetir insumos.
- Crear una solicitud la envía inmediatamente al flujo de compras con estado `Pendiente`, proveedor y empleado autenticado. Usa las tablas existentes, por lo que aparece en compras y en el flujo del proveedor sin copiar datos. No crea órdenes ni cotizaciones automáticamente. Las solicitudes no reservan stock ni impiden generar otra solicitud posterior.
- Los `POST` no tienen clave de idempotencia: un reintento puede crear otro movimiento o solicitud. El frontend debe bloquear el doble envío y comprobar el resultado ante una respuesta de red incierta.

## Ejemplos de cuerpos JSON

Alta de producto (reemplazar IDs por los de `/catalogos`):

```json
{
  "nombre": "Caramelo surtido", "descripcion": "Bolsa de caramelos",
  "precio": 12.50, "stock": 40, "stockMinimo": 10, "stockLimite": 100,
  "idCategoria": 1, "idPresentacion": 1, "idAlmacen": 1
}
```

Movimiento:

```json
{"tipoMovimiento":"SALIDA","tipoItem":"PRODUCTO","idItem":1,"cantidad":5,"motivo":"Despacho de almacen"}
```

Solicitud automática:

```json
{"idProveedor":1}
```

Solicitud manual:

```json
{"idProveedor":1,"detalles":[{"idInsumo":1,"cantidad":15.50}]}
```

Errores: `400` datos inválidos, `401` sin autenticación, `403` sin permisos o CSRF, `404` recurso inexistente, `409` stock insuficiente, conflicto concurrente. Los errores de validación REST incluyen `errores` por campo; los de negocio incluyen el motivo en `mensaje`. No se devuelve la excepción de la base de datos.

## Verificación local

Ejecutar `./mvnw.cmd test` con JDK 21 o superior. Las pruebas usan H2 en modo Oracle, sin seeders, monitor ni servicios externos. Cubren CRUD, validación, búsqueda, saldos y comprobantes, alertas y notificaciones, solicitudes atómicas, permisos y CSRF, integración de ventas y dos salidas concurrentes. H2 verifica las reglas y transacciones; no sustituye aplicar y comprobar la migración en una instancia Oracle.

## Frontend Angular

El módulo ya incluye frontend en `frontend/`. Consultar `frontend/README.md` para iniciar desarrollo, compilar e integrarlo con Spring. La migración V2 agrega el estado activo/inactivo requerido por sus formularios. La sesión REST y la carga de imágenes están descritas en el contrato actualizado.
