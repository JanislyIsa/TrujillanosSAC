# Trabajo compartido con Git y GitHub

El backend y el frontend son dos repositorios separados. Este repositorio contiene Spring Boot, API REST, scripts Oracle y documentación; `TrujillanosSAC-Frontend` contiene Angular y se conecta al backend mediante sus API REST.

La carpeta `frontend/` que llegó a existir dentro del backend era una copia accidental y se retiró. No se integra ni se compila Angular desde este repositorio.

No existe un historial anterior: `main` comienza con documentación y configuración Git; `inventario-suarez-quezada` incorpora la versión actual completa. Incluye también el código heredado de compras, ventas y seguridad necesario para ejecutarla. No se presenta esa importación como si fuera un diff exclusivo de inventario frente a una base anterior inexistente.

## Ramas

- `main`: versión compartida y comprobada.
- `inventario-suarez-quezada`: cambios de inventario y sus integraciones con seguridad, compras y ventas.
- `compras-apellido`: desarrollo de compras por otro integrante.
- `ventas-apellido`: desarrollo de ventas por otro integrante.

Cada rama contiene una versión completa del proyecto. Una rama no es una carpeta ni restringe los archivos que puede editar una persona. Los paquetes Java organizan el código; las ramas permiten desarrollar y revisar cambios.

La rama de inventario debe ser la primera entrega a integrar. Mientras no se integre, hay que cambiar a `inventario-suarez-quezada` para ejecutar el sistema; `main` todavía no incluye código de aplicación.

## Integración

1. Preparar y verificar inventario en su rama.
2. Abrir un pull request hacia `main` y revisar los cambios.
3. Integrarlo cuando esté probado. El historial mantiene la autoría y la rama identifica la entrega del módulo.
4. Quien haga compras actualiza `main` y crea su rama desde esa versión.
5. Subir commits a la rama de compras y abrir otro pull request.

Si compras necesita inventario antes de su integración, puede partir temporalmente de la rama de inventario. Debe coordinar después la actualización con `main`; no son dos bases independientes.

## Desde otra laptop

Tras aceptar la invitación como colaborador y tener Git instalado:

```powershell
git clone URL_DEL_REPOSITORIO
cd CARPETA_DEL_REPOSITORIO
git switch main
git pull --ff-only origin main
git switch -c compras-apellido
```

Después de implementar y verificar cambios, seleccionar los archivos que correspondan:

```powershell
git add RUTAS_DE_LOS_ARCHIVOS
git commit -m "Implementar solicitudes de compras"
git push -u origin compras-apellido
```

Abrir el pull request en GitHub para revisar e integrar a `main`. Los commits deben usar el nombre y correo de quien hizo el trabajo. No se comparte una cuenta ni una contraseña de GitHub.

## Configuración y base de datos

Antes de publicar, excluir credenciales locales, IDE, `target`, `node_modules`, cachés, temporales y archivos subidos durante pruebas. Versionar código, `pom.xml`, `package.json`, el archivo de bloqueo de npm y los scripts SQL.

GitHub comparte los archivos SQL, pero no comparte automáticamente la instancia Oracle ni sus datos. Cada integrante puede preparar su propia base con los mismos scripts y configurar sus credenciales localmente. Los cambios de esquema posteriores deben entregarse como nuevas migraciones coordinadas.

El acceso del compañero requiere su usuario de GitHub y una invitación al repositorio con permiso de escritura.

## Publicar por primera vez

Crear en GitHub un repositorio nuevo, por ejemplo `TrujillanosSAC`, preferentemente privado para el equipo. Dejarlo vacío: sin README, licencia ni `.gitignore`, porque ya existen localmente.

Desde la raíz de este proyecto, reemplazar `URL_DEL_REPOSITORIO` por la URL real:

```powershell
git remote add origin URL_DEL_REPOSITORIO
git push -u origin main
git push -u origin inventario-suarez-quezada
```

Crear un pull request de `inventario-suarez-quezada` hacia `main` para revisar la primera entrega. Una vez integrado, los compañeros pueden clonar `main` y crear sus ramas. La invitación como colaborador se hace con la cuenta GitHub del compañero; debe aceptarla antes de subir su rama.

Las credenciales de esta laptop se conservaron en `application-local.properties`, ignorado por Git. El archivo `application-local.properties.example` sirve de plantilla para cada integrante.
