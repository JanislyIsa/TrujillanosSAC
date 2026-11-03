# Frontend de inventario · El Trujillano

Aplicación Angular 22, con organización basada en [pharma-frontend](https://github.com/JanislyIsa/pharma-frontend) y diseño basado en los prototipos proporcionados. Los componentes, modelos, servicios HTTP y rutas se mantienen separados.

## Pantallas incluidas

- Productos e insumos: pestañas, búsqueda por nombre/código, filtro activo/inactivo y paginación. Modales de registro, detalle, edición y eliminación lógica.
- Registro de productos con precio, descripción, categoría, presentación, almacén, límites e imagen JPG/PNG de hasta 2 MB. Vista previa y validación del archivo.
- Control de stock: filtros por tipo, almacén y nivel; cantidades y detalle del registro.
- Movimientos: ingresos y salidas, vista previa del saldo resultante, filtros de historial y comprobante imprimible.
- Alertas: niveles bajos y sobrestock, historial de alertas resueltas y revisión manual de niveles.
- Notificaciones propias del jefe de compras, con marcado de lectura.
- Solicitudes de reposición: selección manual o llenado con insumos faltantes, cantidades editables, proveedor y envío al módulo de compras.
- Sesión con usuario/contraseña, menú según permisos y cierre de sesión.

Los datos mostrados provienen del backend. Los ejemplos con Tetin POP y otros productos pertenecen exclusivamente a la vista previa H2 descrita abajo.

## Estructura

```text
src/app/core                  API HTTP, modelos y sesión
src/app/layout                barra lateral y cabecera
src/app/shared                modales, iconos, paginador y formato
src/app/features/auth         login y acceso restringido
src/app/features/inventario
  components                  formularios y comprobantes
  pages                       catálogo, stock, movimientos, alertas y solicitudes
```

## Desarrollo

Requiere Node compatible con Angular 22 (se verificó con Node 24.21) y JDK 21 o superior para el backend.

1. Para una base nueva, seguir `database/oracle/README.md`. Para una existente, aplicar las migraciones pendientes V1, V2 y V3. Configurar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.
2. Iniciar el backend desde la raíz con `./mvnw.cmd spring-boot:run`.
3. En `frontend/`, ejecutar `npm ci` y `npm start`.
4. Abrir `http://localhost:4200`. El proxy dirige `/api`, `/img` y `/uploads` al backend en `localhost:9191`.

El frontend utiliza la sesión del servidor y CSRF; no guarda contraseñas ni tokens en localStorage/sessionStorage. Las rutas usan hash para poder servir la compilación como recursos estáticos de Spring sin reescrituras del servidor.

## Integración con Spring

Desde `frontend/`:

```powershell
npm run build:backend
```

Compila en producción y copia los bundles a `src/main/resources/static/inventario-app`. Después iniciar o empaquetar el backend. Abrir `/inventario-app/index.html`. Los accesos existentes `/admin/inventario`, `/control-stock`, `/movimientos`, `/alertas` y `/admin/productos` dirigen a las nuevas pantallas.

Ejecutar de nuevo `build:backend` y reiniciar Spring después de cambios en el código Angular. `npm run build` solo compila; `npm run check` verifica TypeScript.

## Vista previa aislada con H2

Desde la raíz del proyecto, tras ejecutar `npm run build:backend` en `frontend/`:

```powershell
./mvnw.cmd spring-boot:test-run '-Dspring-boot.run.main-class=com.example.TrujillanosSAC.preview.InventarioPreviewApplication'
```

Abrir `http://localhost:9192/inventario-app/index.html`.

Esta aplicación de prueba crea una base H2 en memoria y datos de ejemplo. No usa Oracle. Los datos se pierden al detenerla y sus clases no forman parte del JAR de producción.

| Usuario de prueba | Cargo | Contraseña de prueba |
|---|---|---|
| `demo` | Administrador, acceso a inventario y solicitudes | `DemoInventario1!` |
| `almacen-demo` | Almacenero | `DemoInventario1!` |
| `compras-demo` | Jefe de compras | `DemoInventario1!` |

## Decisiones de funcionamiento

- El código de producto o insumo se asigna al guardar; no se predice un ID desde el navegador.
- Al editar, el saldo actual es de consulta. Los cambios de stock se registran mediante ingresos y salidas.
- Un registro existente conserva almacén y presentación/unidad para proteger su historial.
- Eliminar inactiva el registro; conserva cantidades e historial. Puede reactivarse desde Editar. Los productos inactivos no pueden venderse.
- Las solicitudes automáticas muestran las cantidades sugeridas antes de enviar, permitiendo revisarlas.
- Se impide el doble envío mientras se guarda; los errores conservan el formulario y sus valores.
- La carga inicial, los resultados vacíos, la falta de permisos y los errores del servidor tienen estados visibles.
- El diseño adapta el menú y los formularios a pantallas pequeñas. Las tablas conservan desplazamiento horizontal para mantener legibles los datos.

Contrato REST: `docs/inventario/openapi.json`. Las pruebas de backend se ejecutan con `./mvnw.cmd test`.
