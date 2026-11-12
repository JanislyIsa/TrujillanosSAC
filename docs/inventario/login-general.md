# Acceso general y conexión Oracle

El frontend Angular está en el repositorio independiente `TrujillanosSAC-Frontend`. Su login específico se sustituyó por una redirección a `/login`, el formulario general del sistema. La falta de sesión, una sesión caducada y el cierre de sesión conducen al mismo formulario. El backend dirige a cada empleado al primer módulo autorizado por su cargo.

Acceso normal: http://localhost:9191/login. Oracle: `localhost:1522/FREEPDB1`, esquema `ELTRUJILLANO_APP`. La página del puerto 9192 correspondía a la vista previa H2.

El servidor de desarrollo Angular utiliza `localhost:4200`; su proxy reenvía las llamadas de API al backend en `localhost:8080` (perfil dev). Las rutas Angular se sirven desde el frontend y el backend responde solo a las API.

Validación del 3 de noviembre de 2026: TypeScript y compilación integrada correctos; 21 pruebas del backend aprobadas, incluida la sesión compartida entre el login general y la API de inventario. Hibernate validó el esquema Oracle. La prueba HTTP en `localhost:9191` comprobó el formulario general con CSRF, login de administrador, sesión compartida, consulta de cuatro productos, bundles actualizados y logout con invalidación de sesión. JAR actualizado generado correctamente.

Reiniciar el backend desde IntelliJ para cargar cambios del servidor. Para compilar Angular, ejecutar `npm run build` en el repositorio independiente del frontend. Reiniciar `npm start` después de cambiar el proxy.
