# Acceso general y conexión Oracle

El frontend Angular está en `C:/Users/jatni/Downloads/TrujillanosSAC-Frontend`. Su login específico se sustituyó por una redirección a `/login`, el formulario general del sistema. La falta de sesión, una sesión caducada y el cierre de sesión conducen al mismo formulario. El backend dirige a cada empleado al primer módulo autorizado por su cargo.

Acceso normal: http://localhost:9191/login. Oracle: `localhost:1522/FREEPDB1`, esquema `ELTRUJILLANO_APP`. La página del puerto 9192 correspondía a la vista previa H2.

El servidor de desarrollo Angular utiliza `localhost:4200`; su proxy utiliza `localhost:9191` e incluye las rutas del login general, sus estilos/scripts y las páginas de Spring. Las llamadas de API siguen usando rutas relativas al servidor actual. Si se abre manualmente una dirección con `127.0.0.1`, el navegador conserva ese nombre; los accesos de uso habitual se hacen ahora con `localhost`.

Validación del 3 de noviembre de 2026: TypeScript y compilación integrada correctos; 21 pruebas del backend aprobadas, incluida la sesión compartida entre el login general y la API de inventario. Hibernate validó el esquema Oracle. La prueba HTTP en `localhost:9191` comprobó el formulario general con CSRF, login de administrador, sesión compartida, consulta de cuatro productos, bundles actualizados y logout con invalidación de sesión. JAR actualizado generado correctamente.

Reiniciar el backend desde IntelliJ para cargar los cambios en la redirección por cargo. Para recompilar el frontend, ejecutar `npm run build:backend` desde su carpeta y reiniciar Spring. Si se usa Angular por separado, reiniciar también `npm start` después de cambiar el proxy.
