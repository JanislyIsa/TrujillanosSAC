# Instalación de Oracle para El Trujillano

Esta carpeta instala desde cero el esquema que utiliza **todo el backend**: seguridad, inventario y sus dependencias de compras y ventas. Requiere una instancia Oracle ya instalada, una PDB abierta y un usuario administrador para el primer paso. No ejecuta `CREATE DATABASE` ni instala el servidor Oracle.

Los scripts contienen el resultado de V1, V2 y V3. **En una instalación nueva no ejecutes esas migraciones después de `01_tablas.sql`.** Si ya tienes tablas y datos, usa el apartado de actualización.

## 1. Crear el usuario y sus permisos

En DBeaver o SQL Developer, crea una conexión administrativa con estos valores de ejemplo:

| Campo | Valor |
|---|---|
| Servidor | `localhost` |
| Puerto | `1522` si usas el contenedor `oracle-local` de esta máquina; `1521` si tu instalación lo expone así |
| Tipo de conexión | Nombre de servicio, no SID |
| Servicio | `FREEPDB1` en Oracle Free; `XEPDB1` en XE 21c |
| Usuario | `SYSTEM`, con tu contraseña de instalación |

Comprueba el destino:

```sql
SELECT SYS_CONTEXT('USERENV','CON_NAME') AS PDB FROM DUAL;
```

Debe indicar la PDB elegida, por ejemplo `FREEPDB1`. Si indica `CDB$ROOT`, cambia el servicio de la conexión. No necesitas `_ORACLE_SCRIPT` ni un usuario `C##`.

Abre `00_usuario.sql`, **reemplaza `CAMBIAR_ClaveOracle_2026!` por tu propia contraseña**, y ejecútalo como script. Usa el bloque completo, incluida la barra `/` del bloque PL/SQL. El script crea `ELTRUJILLANO` con `CREATE SESSION`, `CREATE TABLE`, `CREATE SEQUENCE` (generadores de las columnas de identidad) y cuota de 200 MB en `USERS`. Si el servidor utiliza otros tablespaces, ajusta `USERS` y `TEMP` antes de ejecutar.

El propietario ya puede leer y modificar sus propias tablas. No se otorga `DBA`, acceso a otras bases, privilegios `ANY` ni cuota ilimitada. Oracle documenta los requisitos de [privilegios y cuota para crear usuarios](https://docs.oracle.com/en/database/oracle/oracle-database/19/dbseg/creating-user-accounts.html).

El script no reemplaza usuarios existentes ni cambia sus contraseñas. Si `ELTRUJILLANO` ya existe, comprueba primero si es el esquema que quieres actualizar; no lo borres.

## 2. Crear las tablas dentro del usuario

**Crea una segunda conexión** al mismo servidor, puerto y servicio, ahora como `ELTRUJILLANO` y con la contraseña del paso anterior. No basta con cambiar `CURRENT_SCHEMA` desde `SYSTEM`.

Comprueba:

```sql
SELECT USER FROM DUAL;
SELECT COUNT(*) FROM USER_TABLES;
```

Para la instalación nueva debe ser `ELTRUJILLANO` y cero tablas. Si es una base de desarrollo que quieres recrear, ejecuta antes `limpiar_esquema.sql` con ese mismo usuario: borra todas sus tablas y datos.

Ejecuta en orden:

1. `01_tablas.sql`: 41 tablas, claves primarias, claves foráneas, restricciones e índices.
2. `02_catalogos.sql`: documentos, roles, cargos, categorías, presentaciones, unidades, estados y métodos de pago.
3. `03_datos_demo.sql`: datos ficticios de desarrollo; es opcional.
4. `04_verificar.sql`: diagnóstico de esquema, accesos, existencias, comprobantes y solicitudes.

En DBeaver usa **Ejecutar script SQL (Alt+X)**, configura **detenerse al primer error** y desactiva el autocommit durante los datos iniciales. En SQL Developer usa **F5**. `03_datos_demo.sql` es una transacción: ante un fallo se revierte íntegramente. Los scripts se ejecutan una sola vez. El DDL de Oracle confirma sus cambios automáticamente; si aparece un error, corrígelo antes de continuar y revisa qué objetos llegaron a crearse.

Con SQL*Plus/SQLcl o SQL Developer puedes ejecutar el instalador de desarrollo, que detiene la ejecución al primer error:

```sql
-- Ya conectado como ELTRUJILLANO:
@C:/Users/jatni/Downloads/TrujillanosSAC-master/database/oracle/instalar_esquema.sql
```

`instalar_esquema.sql` incluye los datos demo. Para una base real, ejecuta solamente `01`, `02` y `04`, y registra personas, empleados, usuarios, proveedores y almacenes con tus datos. La aplicación exige un empleado responsable por almacén. Las contraseñas web deben almacenarse con BCrypt, como utiliza `SecurityConfig`; una contraseña en texto plano no permite iniciar sesión. El ejemplo de `03` muestra las relaciones necesarias.

Las identidades empiezan en 1000; los IDs explícitos 1–999 quedan reservados a los datos iniciales. Así el siguiente alta de la aplicación no colisiona con un ID insertado por estos scripts.

## 3. Cuentas y datos de desarrollo

Solo al ejecutar `03_datos_demo.sql` se crean estas cuentas de la **aplicación web**:

| Usuario | Acceso |
|---|---|
| `admin` | Administración, inventario y compras |
| `almacenero` | Catálogo, stock, movimientos y alertas |
| `compras` | Stock, alertas, notificaciones propias y solicitudes |
| `ventas` | Módulos heredados de ventas |
| `proveedor` | Solicitudes del proveedor de ejemplo |
| `cliente` | Catálogo de ventas |

Contraseña web de desarrollo: **`Inventario2026!`**. Los hashes fueron generados con BCrypt de Spring Security. Esta contraseña no es la contraseña de Oracle y estas cuentas no deben cargarse en producción.

Se insertan cuatro productos, cuatro insumos, un almacén, dos proveedores, ocho ingresos iniciales con saldos y comprobantes, cuatro alertas con notificaciones para `compras`, y una solicitud pendiente con dos líneas. Hay productos/insumos normales, bajos y con sobrestock. No se inventan cotizaciones, compras pagadas ni ventas; sus tablas quedan listas para las operaciones del sistema.

## 4. Conectar y ejecutar la aplicación

Abre PowerShell en la raíz y configura las variables en esa misma terminal:

```powershell
cd "C:\Users\jatni\Downloads\TrujillanosSAC-master"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
$env:DB_URL = "jdbc:oracle:thin:@//localhost:1522/FREEPDB1"
$env:DB_USERNAME = "eltrujillano"
$claveOracle = Read-Host "Contraseña Oracle de ELTRUJILLANO" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $claveOracle).Password
try {
    .\mvnw.cmd spring-boot:run
} finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
}
```

Ajusta el puerto/servicio a tu instalación. La contraseña se solicita oculta y no queda escrita en el historial de PowerShell ni en el repositorio. `APP_SEED_HABILITADO` debe quedar sin definir o en `false`; los datos ya se cargaron mediante SQL.

Cuando aparezca `Started TrujillanosSacApplication`, abre:

**http://localhost:9191/inventario-app/index.html**

La interfaz Angular ya está integrada en los recursos estáticos. No necesitas levantar Angular por separado. Si cambias su código, ejecuta `npm run build:backend` desde `frontend` y vuelve a iniciar el backend. `Ctrl+C` detiene la aplicación; los datos de Oracle permanecen.

No uses `spring-boot:test-run` para esta conexión: ese comando inicia la vista previa H2 del proyecto, que utiliza datos temporales y el puerto 9192.

## 5. Actualizar una base existente

No ejecutes `00`, `01`, `02` ni `03` sobre una base que ya tiene datos. Respáldala y aplica **solo las migraciones pendientes**, por orden:

1. `src/main/resources/db/migration/oracle/V1__inventario.sql`.
2. `src/main/resources/db/migration/oracle/V2__inventario_frontend.sql`.
3. `src/main/resources/db/migration/oracle/V3__comprobante_movimiento.sql`.

V3 crea la entidad persistente `COMPROBANTE_MOVIMIENTO`, una por movimiento, y copia código, fecha, responsable y motivo de los movimientos previos. Mantiene los códigos `CM-001`, `CM-1000`, etc., sin truncar IDs mayores de 999. Los saldos históricos que no se guardaron siguen siendo desconocidos; la migración no los inventa.

No hay Flyway: estas migraciones son manuales. Spring conserva `ddl-auto=validate`, por lo que comprueba el esquema sin crearlo, borrarlo ni modificarlo.

## 6. Diagnóstico

| Mensaje | Qué revisar |
|---|---|
| `ORA-12541` / conexión rechazada | Oracle encendido, listener, servidor y puerto correctos |
| `ORA-12514` | Nombre de servicio/PDB correcto y abierto |
| `ORA-01017` | Usuario y contraseña de Oracle, no los de la web |
| `ORA-65096` | Se intentó crear el usuario en `CDB$ROOT`; conectar a la PDB |
| `ORA-01950` | Cuota del propietario en el tablespace usado |
| `ORA-00955` / objeto existente | Se está repitiendo una instalación; revisar antes de continuar |
| `Schema-validation: missing table/column` | Instalación incompleta o migración pendiente |
| Login web rechazado | Ejecutar el demo o crear un usuario con hash BCrypt y sus relaciones |

Correspondencia y decisiones respecto a los PDF: `docs/inventario/conformidad-diagramas.md`.
