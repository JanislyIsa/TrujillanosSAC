# Trujillanos SAC

Repositorio del equipo para inventario, compras y ventas.

## Ramas de trabajo

- `main`: punto de integración del equipo.
- `inventario-suarez-quezada`: primera entrega del proyecto actual y del módulo de inventario.
- `compras-apellido`: rama que debe crear el responsable de compras.

Este repositorio comienza sin un historial anterior. El primer commit de `main` contiene la documentación inicial; el código actual se incorpora en `inventario-suarez-quezada`. Esa primera entrega incluye también los módulos heredados necesarios para ejecutar el sistema. No representa una comparación contra una versión anterior a inventario.

Para comenzar compras, integrar primero la entrega inicial mediante un pull request hacia `main`. Después cada integrante crea su rama desde el `main` actualizado. Hasta integrar esa entrega, `main` solo contiene la documentación inicial.

## Colaboración

El propietario debe crear el repositorio en GitHub e invitar a los compañeros como colaboradores. Cada persona usa su propia cuenta y clona el repositorio en su laptop. Los cambios se proponen mediante pull requests, sin compartir credenciales.

La rama de implementación incluye el backend Spring Boot, el frontend Angular, los scripts Oracle y las instrucciones de ejecución. Cada integrante configura su propia base y sus credenciales localmente.

## Ejecutar la entrega de inventario

Requisitos: JDK 21 o superior, Node compatible con Angular 22 (se verificó con Node 24.21), y Oracle con una PDB abierta.

1. Preparar usuario, tablas y datos con [la guía Oracle](database/oracle/README.md). Cada integrante usa su propia base o una instancia compartida acordada por el equipo.
2. Copiar `application-local.properties.example` a `application-local.properties` en la raíz y completar las credenciales. Ese archivo personal no se sube a Git.
3. Iniciar Spring desde la raíz con `./mvnw.cmd spring-boot:run` en Windows. El perfil local escucha en `http://localhost:8080`.
4. En el repositorio independiente `TrujillanosSAC-Frontend`, ejecutar `npm ci` y `npm start`. Abrir `http://localhost:4200`; el proxy de Angular conecta con el backend.

Validación: `./mvnw.cmd test` en la raíz del backend; `npm run check` y `npm run build` en el repositorio `TrujillanosSAC-Frontend`.

El backend expone las API REST y el frontend Angular se ejecuta y versiona por separado. Los cambios de esquema se entregan como nuevas migraciones. No volver a ejecutar la instalación inicial sobre una base con datos.
