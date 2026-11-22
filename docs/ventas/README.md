# Módulo de ventas

API REST en `/api/v1/ventas/**`, pensada para el frontend Angular `trujillano-frontend`
(`http://localhost:4200`). Usa **JWT**: no hay sesión, cookies ni CSRF.

## Autenticación (JWT)

1. `POST /api/v1/auth/login` (público) con `{ "username": "cliente", "password": "..." }`. Responde:
   ```json
   { "token": "eyJ...", "tipo": "Bearer", "expiraEnSegundos": 3600, "username": "cliente",
     "nombreCompleto": "Cliente Demostracion", "roles": ["CLIENTE"], "permisos": ["PAGO_REALIZAR", "PEDIDO_CREAR", ...] }
   ```
2. En cada petición a `/api/v1/ventas/**` enviar el header `Authorization: Bearer <token>`
   (en Angular, con un `HttpInterceptor`).
3. Cuando el token vence (60 minutos por defecto) la API responde 401: volver a hacer login.
   "Cerrar sesión" es borrar el token en el frontend (el servidor no guarda sesiones).

Errores de seguridad, en el mismo formato que los demás (`timestamp, status, error, message, path`):
- **401**: sin token, token vencido o inválido, o usuario/contraseña incorrectos en el login.
- **403**: el rol no tiene permiso (por ejemplo, un cliente llamando a un endpoint de empleado).

`permisos` (ver `auth/PermisosPorRol`) solo sirve para mostrar u ocultar opciones en el frontend;
quien decide el acceso es el backend con los `@PreAuthorize` de cada endpoint.

En Swagger: hacer login, copiar el `token` y pegarlo en **Authorize → jwt** (sin la palabra Bearer).

| Variable de entorno | Para qué |
|---|---|
| `JWT_SECRET` | Secreto Base64 de mínimo 32 bytes (`openssl rand -base64 32`). Obligatorio en `prod` |
| `JWT_EXPIRACION_MINUTOS` | Duración del token (60) |
| `API_CORS_ORIGENES` | Orígenes permitidos del frontend, separados por coma (`http://localhost:4200,http://localhost:4300`) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Conexión a Oracle (ej. `jdbc:oracle:thin:@IP:1521/orcl`). Obligatorias; nunca en Git |
| `SERVER_PORT` | Puerto del backend (8080) |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | Cuenta Gmail del negocio y su contraseña de aplicación. Sin ellas no se envían correos |
| `MAIL_REMITENTE`, `MAIL_NOMBRE_REMITENTE` | Remitente visible (por defecto `MAIL_USERNAME` y "El Trujillano") |
| `FRONTEND_URL` | URL de la tienda para los enlaces de los correos (`http://localhost:4300`) |

## Recuperar y cambiar la contraseña (clientes)

Solo para clientes: son los que tienen correo (`CLIENTES.CORREO`). Requiere `MAIL_USERNAME` y `MAIL_PASSWORD`,
y la tabla `PASSWORD_RESET_TOKEN` (en una base ya creada, ejecutar una vez `database/oracle/06_password_reset.sql`).

| Endpoint | Acceso | Cuerpo | Qué hace |
|---|---|---|---|
| `POST /api/v1/auth/olvide-clave` | público | `{ "correo" }` | Envía un enlace `{FRONTEND_URL}/restablecer-clave?token=...`. Responde **siempre** 200 con el mismo mensaje, exista o no el correo |
| `POST /api/v1/auth/restablecer-clave` | público | `{ "token", "nuevaClave" }` | Cambia la clave si el enlace existe, no se usó y no venció. Si no: 409 "El enlace no es válido o ya venció" |
| `POST /api/v1/auth/cambiar-clave` | con token JWT | `{ "claveActual", "nuevaClave" }` | Cambio desde "Mi cuenta"; 409 si la clave actual no coincide |

- El enlace vence a los 30 minutos (`app.recuperacion-clave.minutos-validez`) y sirve una sola vez.
- En la base solo se guarda el hash SHA-256 del token; pedir un enlace nuevo o cambiar la clave anula los anteriores.
- Como máximo un correo por minuto por cuenta (`app.recuperacion-clave.espera-segundos`).
- La nueva clave: de 8 a 72 caracteres (BCrypt). El correo se envía en segundo plano, después de guardar el enlace.
- Los tokens JWT ya emitidos siguen valiendo hasta que vencen (la API no guarda sesiones).

## Flujo de una venta

1. **Pedido** — `POST /pedidos` `{ "detalles": [{ "productoId": 1, "cantidad": 2 }] }`
   - Solo lo crea un cliente, siempre a su nombre (el sistema no tiene caja ni venta en tienda).
   - Se reserva el stock con un movimiento de SALIDA en inventario. El pedido queda `PENDIENTE`.
   - Si no se paga en 30 minutos (`ventas.pedido.expiracion.minutos`) se cancela solo y el stock vuelve.
2. **Pago** (cliente) — `POST /pagos/culqi` `{ "pedidoId", "token", "email" }`
   - El `token` lo genera Culqi Checkout en el navegador, con tarjeta o con Yape (ver abajo).
   - El monto nunca viene del frontend: se cobra el total del pedido. El pago guarda si fue `TARJETA` o `YAPE`.
   - La respuesta trae `estado.codigo`: `APROBADO`, `RECHAZADO` o `ERROR` (revisarlo siempre).
3. **Comprobante** — `POST /comprobantes` `{ "pedidoId", "tipo": "BOLETA" | "FACTURA" }`
   - Solo de pedidos pagados. Factura exige cliente con RUC de 11 dígitos. Series B001 / F001.
   - Al aprobarse el pago, el backend emite solo el comprobante: **factura** si el cliente tiene RUC, si no **boleta**
     (`B001-00000001`). Si falla, el pago queda registrado igual y se puede emitir después con `POST /comprobantes`.
   - Ticket: `GET /pedidos/{id}/comprobante-pago` (cliente dueño o empleado; 409 si no está pagado) devuelve negocio,
     serie-número, cliente, ítems, total, método de pago, N.º de operación de Culqi y la leyenda
     "no válida ante SUNAT" mientras no exista facturación electrónica. Datos del negocio: `VENTAS_NEGOCIO_RAZON_SOCIAL`,
     `VENTAS_NEGOCIO_RUC`, `VENTAS_NEGOCIO_DIRECCION`.
4. **Entrega** (empleado) — `PATCH /pedidos/{id}/estado` `{ "estado": "LISTO_PARA_RECOJO" | "ENTREGADO" }`

Otros: `GET /pedidos` (empleado: todos; cliente: los suyos), `GET /pedidos/{id}`,
`POST /pedidos/{id}/cancelar` (solo empleado y solo pendientes), `GET /pagos?pedidoId=`, `GET /comprobantes/{id}`.

Catálogos (`/estados-pedido`, `/estados-pago`, `/metodos-pago`, `/tipos-comprobante`, `/estados-comprobante`):
`GET` para cualquier usuario con sesión y `PUT /{id}` `{ "nombre" }` (renombrar) para empleados.
Los registros los crea el sistema y el `codigo` no cambia nunca: es lo que usa el programa.

El empleado solo administra: ve todos los pedidos y pagos, avanza la entrega y puede cancelar pendientes.
El cliente no cancela: si no paga, el pedido vence solo a los 30 minutos y el stock vuelve.

## Consultas y reportes

**Búsqueda** — `GET /pedidos/buscar` (empleado y cliente). Todos los filtros son opcionales y combinables:

| Parámetro | Tipo | Por defecto | Ejemplo | Si falta |
|---|---|---|---|---|
| `clienteId` | Long | — | `3` | No filtra por cliente. Un cliente siempre ve solo los suyos (se ignora este valor) |
| `estado` | código de `EstadoPedidoCodigo` | — | `PAGADO` | No filtra por estado |
| `desde` / `hasta` | fecha ISO | — | `2026-11-01T00:00:00` | No limita el rango |
| `ordenarPor` | `fecha`, `total` o `id` | `fecha` | `total` | Ordena por fecha |
| `direccion` | `asc` o `desc` | `desc` | `asc` | Descendente |

Respuestas: sin coincidencias **200 con `[]`** (nunca 404); `ordenarPor` no permitido o `desde` posterior a `hasta`
**409**; fecha que no es ISO o estado inexistente **400**.

**Reportes** (solo empleado), con rango de fechas opcional `desde` / `hasta`:
- `GET /reportes/ventas-por-categoria` → `[{ categoriaId, categoriaNombre, unidadesVendidas, montoTotal }]`
- `GET /reportes/productos-mas-vendidos?top=10` → `[{ productoId, productoNombre, unidadesVendidas, montoTotal }]`

Solo cuentan como venta los pedidos `PAGADO`, `LISTO_PARA_RECOJO` y `ENTREGADO` (`EstadoPedidoCodigo.vendidos()`):
un pedido `PENDIENTE` o `CANCELADO` no se cobró y no debe sumar. Los reportes están bajo `/api/v1/ventas/reportes`
(no en `/api/v1/reportes`) para usar la misma seguridad JWT y el mismo CORS del módulo.

**Trazabilidad**: `PedidoServiceImpl` y `ReporteServiceImpl` registran, al inicio y al final de cada consulta,
los parámetros, el número de filas y `duracionMs`. Solo identificadores: nunca nombres, documentos ni lo que compró
una persona. En `dev` el nivel es `debug`; en `prod`, `info`.

## Estructura del paquete `ventas`

```
ventas/
├── config/            seguridad JWT + CORS, seeders, tarea de expiración
├── controller/        PedidoController, PagoController, ComprobanteVentaController, ReporteController, catálogos
├── dto/request/       PedidoRequestDTO, DetallePedidoRequestDTO, ...
├── dto/response/      PedidoResponseDTO, DetallePedidoResponseDTO, ...
├── dto/reporte/       VentaPorCategoriaDTO, ProductoMasVendidoDTO (record)
├── exception/         VentasExceptionHandler (lo propio de ventas; lo demás va al GlobalExceptionHandler)
├── mapper/            VentaMapper (entidad -> DTO)
├── model/             Pedido (cabecera), DetallePedido (detalle), Pago, ComprobanteVenta, catálogos
├── repository/        PedidoRepository (búsqueda + reportes), ...
├── security/          ActorVenta (quién hace la operación)
└── service/
    ├── generic/       CatalogoService (contrato común de los catálogos, como CrudService)
    ├── service/       interfaces: PedidoService, PagoService, ReporteService, EstadoPedidoService, ...
    ├── impl/          implementaciones: PedidoServiceImpl, ReporteServiceImpl, CatalogoServiceImpl, ...
    ├── pasarela/      Culqi
    └── facturacion/   SUNAT (pendiente)
```

Equivalencias con el ejemplo de clase (PharmaBackend): `Venta` = `Pedido`, `DetalleVenta` = `DetallePedido`,
estado inicial `REGISTRADA` = `PENDIENTE`, `ANULADA` = `CANCELADO` (`POST /pedidos/{id}/cancelar`, 409 si ya no
está pendiente). Diferencia del negocio: en clase los reportes excluyen `ANULADA`; aquí, como el cobro es en línea,
solo cuentan los pedidos ya pagados.

## Estados del pedido

```
PENDIENTE ──pago──▶ PROCESANDO_PAGO ──aprobado──▶ PAGADO ──▶ LISTO_PARA_RECOJO ──▶ ENTREGADO
    │                      └──rechazado/error──▶ PENDIENTE
    └──vence / cancela un empleado──▶ CANCELADO (devuelve stock)
```

## Configuración (`application.yaml`)

| Variable de entorno | Para qué |
|---|---|
| `CULQI_LLAVE_SECRETA` | Llave `sk_test_...` o `sk_live_...`. Nunca en el código ni en Git |
| `VENTAS_PEDIDO_EXPIRACION_MINUTOS` | Minutos para pagar antes de que el pedido se cancele (30) |


## Culqi Checkout en Angular (solo tarjeta y Yape)

Mientras no exista el frontend, `docs/ventas/prueba-culqi.html` abre el checkout real con la llave `pk_test_...`
y muestra el token de Culqi para pegarlo en Swagger (`POST /pagos/culqi`).
Abrirla desde IntelliJ (clic derecho → Open In → Browser) para que se sirva por `http://localhost`.

Datos de prueba (solo con llaves `pk_test`/`sk_test`, no mueven dinero real;
fuente: https://docs.culqi.com/es/documentacion/pagos-online/tarjetas-de-prueba/):
- Yape: celular `900 000 001` y cualquier código de 6 dígitos. Nunca usar un número o código real en pruebas.
- Tarjeta aprobada: Visa `4111 1111 1111 1111`, vence `09/30`, CVV `123`.
- Tarjeta rechazada (fondos insuficientes): Visa `4000 0400 0000 0008`, vence `03/30`, CVV `295`.

Documentación: https://docs.culqi.com/es/documentacion/checkout/checkout-custom

1. En `index.html`, antes de cerrar `body`: `<script src="https://js.culqi.com/checkout-js"></script>`
2. Al pagar un pedido ya creado (usar su `total` exacto: Yape genera el token para ese monto):

```js
const config = {
  settings: {
    title: 'Trujillanos SAC',
    currency: 'PEN',                         // obligatorio para Yape
    amount: Math.round(pedido.total * 100),  // en céntimos: 51.50 -> 5150
  },
  options: {
    lang: 'auto',
    installments: false,
    modal: true,
    paymentMethods: {
      tarjeta: true, yape: true,
      billetera: false, bancaMovil: false, agente: false, cuotealo: false,
    },
    paymentMethodsSort: ['tarjeta', 'yape'],
  },
};
const Culqi = new CulqiCheckout('pk_test_xxxxxxxx', config);  // llave PÚBLICA
Culqi.culqi = () => {
  if (Culqi.token) {
    Culqi.close();
    // POST /api/v1/ventas/pagos/culqi  { pedidoId, token: Culqi.token.id, email }
  } else if (Culqi.error) {
    console.error(Culqi.error);
  }
};
Culqi.open();
```

La llave pública (`pk_...`) va en el frontend; la secreta (`sk_...`) solo en el backend (`CULQI_LLAVE_SECRETA`).
Verificar en el sandbox que Yape aparezca: la documentación indica que sin `order` podría mostrarse solo tarjeta.
Si pasa, el backend tendría que crear una orden de Culqi antes de abrir el checkout.

## Pendiente

- Webhooks de Culqi (necesarios para Yape/PagoEfectivo y para confirmar pagos que quedaron en `ERROR`).
- Autenticación 3DS de Culqi: hoy esos cargos se tratan como no aprobados.
- Devolución de dinero al cancelar un pedido ya pagado.
- SUNAT: implementar `FacturacionElectronica` (hoy `SinFacturacionElectronica` deja `NO_ENVIADO`).

## Base de datos

Las tablas de ventas están en `database/oracle/01_tablas.sql` (sección VENTAS) y sus códigos en
`02_catalogos.sql`. Si agregan triggers, ninguno debe modificar el stock: eso ya lo hace la aplicación.
