# Correspondencia con los diagramas de inventario

Referencias revisadas: **Diagrama de Clase de Analisis-Inventario.drawio.pdf** (28 páginas) y **Diagrama de Secuencia-INVENTARIO.drawio.pdf** (70 páginas), aportados el 3 de noviembre de 2026. Se leyó también el XML draw.io embebido en los PDF para recuperar los elementos repartidos entre páginas. Son especificaciones de análisis; no contienen un DDL físico ni un diccionario completo de columnas. Los tipos, longitudes y claves de la instalación se basan en las entidades JPA existentes.

La numeración del PDF es **RF25–RF56**. Sustituye, para esta comparación, la numeración RF18–RF49 de la documentación anterior. Las interfaces y controladores del diagrama se implementan con componentes Angular y controladores REST; no son tablas adicionales.

## Clases y relaciones

| Clase de análisis | Implementación / tabla |
|---|---|
| Producto → Categoría, PresentaciónProducto, Almacén | `Producto`, `PRODUCTO` con tres FK obligatorias |
| Insumo → UnidadMedida, Almacén | `Insumo`, `INSUMO` con dos FK obligatorias |
| Almacén → Empleado responsable | `Almacen`, `ALMACEN.ID_PERSONA` → `EMPLEADOS` |
| Movimiento → DetalleMovProducto → Producto | `MOVIMIENTO`, `DETALLE_MOV_PRODUCTO`, `PRODUCTO` |
| Movimiento → DetalleMovInsumo → Insumo | `MOVIMIENTO`, `DETALLE_MOV_INSUMO`, `INSUMO` |
| Movimiento ↔ ComprobanteMovimiento (1:1) | Nueva entidad y repositorio `ComprobanteMovimiento`; `COMPROBANTE_MOVIMIENTO.ID_MOVIMIENTO` es PK y FK |
| Alerta → artículo, almacén, empleado | `ALERTA`, artículo identificado por `TIPO_ITEM` + `ID_ITEM`; FK a almacén y empleado |
| Alerta notifica a Jefe de Compras | `NOTIFICACION_INVENTARIO`, FK a alerta y empleado destinatario; unicidad por alerta/destinatario |
| SolicitudCompra → Proveedor, Estado, Empleado | `SOLICITUD_COMPRA` con tres FK obligatorias |
| SolicitudCompra → DetalleSolicitud → Insumo | `DETALLE_SOLICITUD`, FK a solicitud e insumo; cantidad positiva y unicidad por solicitud/insumo |

La relación polimórfica alerta/artículo se valida en el servicio; Oracle no puede poner una sola FK que apunte alternativamente a `PRODUCTO` o `INSUMO`. La aplicación guarda cabecera y detalles de solicitudes en una transacción y exige al menos una línea. La API actual registra un artículo por operación de movimiento, aunque el modelo relacional permite varios detalles por cabecera; no hay una pantalla de movimientos por lote.

## Flujos del PDF

| RF del PDF | Flujo | Código / evidencia |
|---|---|---|
| 25–29 | Registrar, modificar, eliminar, buscar y consultar producto | Pantalla de catálogo, `ProductoInventarioController`, `CatalogoInventarioServiceImpl` |
| 30–34 | Registrar, modificar, eliminar, buscar y consultar insumo | Pantalla de catálogo, `InsumoInventarioController`, `CatalogoInventarioServiceImpl` |
| 35–36 | Verificar stock y mostrar cantidades | Pantalla de stock, `StockInventarioController`, `CatalogoInventarioServiceImpl.listar` |
| 37–39 | Límites, stock bajo, normal y sobrestock | `EstadoStock.calcular`: `< mínimo`, `> límite`; igual a un umbral es normal |
| 40–46 | Ingreso/salida, fecha/hora, actualizar y recalcular existencias | `MovimientoInventarioServiceImpl.registrar`, bloqueo del artículo y transacción única; salida insuficiente revierte |
| 47–48 | Generar y registrar comprobante | `ComprobanteMovimientoRepository`, persistencia al registrar el movimiento; consulta posterior lee el mismo comprobante |
| 49–52 | Monitorear niveles, alertar y notificar a compras | `InventarioMonitor`, `AlertaInventarioServiceImpl`, pantalla de alertas y notificaciones propias |
| 53–56 | Identificar faltantes, llenar y enviar solicitud | `SolicitudInventarioServiceImpl`, pantalla de solicitudes; solicitud pendiente visible en compras con las tablas compartidas |

## Cambios realizados a partir de esta revisión

- El comprobante antes era una respuesta calculada. Ahora se guarda en la misma transacción que el movimiento, con código único, fecha de emisión, responsable y motivo. Una consulta repetida no crea otro registro. La migración V3 conserva los códigos de movimientos existentes.
- Se agrega una instalación Oracle completa, sin depender de tablas preexistentes. Incluye las entidades compartidas que Hibernate también valida al arrancar, además de las de inventario.
- Los datos demo de Oracle incluyen ingresos iniciales y comprobantes, alertas de niveles bajos/sobrestock, notificaciones y una solicitud enviada a compras.

## Diferencias de diseño que se conservan explícitamente

1. **Eliminar:** el PDF muestra `delete`; la aplicación inactiva productos/insumos con `ACTIVO=0`. Así puede conservar movimientos, comprobantes y referencias de compras/ventas. La eliminación física del artículo con historial no se ofrece.
2. **Edición de stock, almacén y unidad/presentación:** el PDF dibuja estos campos dentro del formulario de modificación. La aplicación conserva stock, almacén y unidad/presentación de un registro existente; las cantidades cambian por ingresos/salidas. Cambiar almacén o unidad requiere otro registro. Se mantiene para proteger el historial; no es una reproducción literal de ese formulario.
3. **Emisión del comprobante:** el PDF dibuja una inserción al consultar `GET /movimientos/{id}/comprobante`. La aplicación lo guarda durante el registro del movimiento y deja `GET` como consulta, para asegurar un solo comprobante por movimiento y evitar escrituras al recargar.
4. **Rutas y verbos:** las rutas dibujadas `/productos`, `/insumos`, `/inventario/control-stock`, etc. representan pantallas; la interfaz Angular usa rutas hash y consume `/api/v1/inventario`. Consultas usan `GET`, modificaciones `PUT` y bajas `DELETE` (lógico), aunque algunos mensajes del PDF se dibujan como `POST`.
5. **Notificar/enviar:** la notificación y la entrega al módulo compras son registros persistidos en la misma base. No son correos ni llamadas a un proveedor externo. Las alertas se deduplican por episodio y se resuelven al normalizar el stock.

Por estas diferencias no se afirma una equivalencia literal de cada mensaje del diagrama. Los flujos de negocio cubiertos y los límites de la implementación quedan identificados aquí.
