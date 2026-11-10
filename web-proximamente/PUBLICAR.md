# Página "Próximamente" de Productos El Trujillano

Página temporal para tener una URL pública (por ejemplo, la que pide Culqi en el registro del comercio)
mientras la tienda Angular está en desarrollo. Son archivos estáticos: `index.html` y la carpeta `img/`.

## Antes de publicar
Abre `index.html` y completa lo marcado con `COMPLETAR`: el correo, la ciudad y las redes del negocio que el
dueño autorice mostrar. Si el dueño lo autoriza, agrega también el RUC en el pie de página.

## Publicarla gratis en Netlify
1. Crea una cuenta gratis en https://www.netlify.com (puedes entrar con GitHub o con correo).
   Hazlo antes de subir la página, para que el sitio quede guardado en tu cuenta.
2. En el panel: **Add new site → Deploy manually**.
3. Arrastra la carpeta `web-proximamente` completa a la zona indicada.
4. Netlify te da una dirección como `https://nombre-aleatorio-123.netlify.app`.
5. Para un nombre más claro: **Site configuration → Change site name** y escribe por ejemplo
   `el-trujillano` → queda `https://el-trujillano.netlify.app` (si ese nombre está libre).
6. Esa dirección es la que pones en Culqi, en "URL del comercio".

Para actualizar la página: **Deploys** y arrastra otra vez la carpeta.

Cuando el frontend Angular esté listo, se puede publicar en este mismo sitio y la URL de Culqi no cambia.
Cuando compren un dominio propio, se conecta en **Domain management** y se actualiza la web en el CulqiPanel.
