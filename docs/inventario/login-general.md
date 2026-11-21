# Acceso general y conexión Oracle

El formulario general de Spring está en `/login`. Spring dirige al empleado al primer módulo autorizado por su cargo. Si el destino es Inventario, la ruta administrativa redirige a la URL del panel de empleados configurada en `FRONTEND_EMPLEADOS_URL`.

El panel Angular se inicia desde el repositorio independiente `TrujillanosSAC-Frontend` con `npm start`. Abre [127.0.0.1:4200](http://127.0.0.1:4200/) y consulta [la guía de Oracle](../../database/oracle/README.md) para preparar la base.

El proxy de Angular reenvía las llamadas de API al backend en el puerto 8080 con el perfil `dev`. Las rutas de Angular se sirven desde el frontend.

La validación del 3 de noviembre de 2026 comprobó el formulario de Spring con CSRF, el login de administrador, la sesión compartida con la API de Inventario, la consulta de productos y el cierre de sesión.

Reiniciar el backend desde IntelliJ para cargar cambios del servidor. Para compilar Angular, ejecutar `npm run build` en el repositorio independiente del frontend. Reiniciar `npm start` después de cambiar el proxy.
