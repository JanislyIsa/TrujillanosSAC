# Estado de calidad con SonarQube

## Alcance

Configuración preparada en la rama `optimizacion-yactayo` para analizar el backend y el frontend de Trujillanos SAC. Las mediciones siguientes son locales; todavía no hay análisis, Quality Gate, incidencias ni Security Hotspots generados por SonarQube.

## Backend

- JaCoCo 0.8.14 genera `target/site/jacoco/jacoco.xml` durante `verify`.
- Se excluyen DTO, entidades/modelos, enums y configuración del cálculo de cobertura.
- `clean verify`: 92 pruebas aprobadas, 0 fallidas; cobertura local de líneas: 54.7 %.
- La verificación se ejecutó con JDK 24. El proyecto declara Java 21. En la máquina local, JDK 27 produce un error de inicialización de Lombok durante compilación.

## Frontend

- Vitest y `@vitest/coverage-v8` producen cobertura HTML y LCOV en `coverage/inventario/lcov.info`.
- `npm run test:coverage`: 40 pruebas aprobadas; cobertura local de líneas: 21.01 %.
- `npm run check` y `npm run build`: aprobados.
- `tsconfig.app.json` excluye los archivos de prueba para que no formen parte de la compilación de la aplicación.

## Pendiente para completar la guía

Docker Desktop debe estar iniciado para levantar SonarQube con `docs/calidad/docker-compose.sonarqube.yml`. Después se deben analizar ambos repositorios, crear y asignar el gate `LP2-Curso`, guardar las capturas, comparar análisis inicial/final, corregir al menos diez hallazgos reales y revisar cada hotspot. No se debe inventar ni marcar como resuelto ningún hallazgo antes de que SonarQube lo reporte.

Los comandos para iniciar el servidor y analizar cada proyecto están en `docs/calidad/README.md` (backend) y `TrujillanosSAC-Frontend/docs-sonarqube.md` (frontend). El token se mantiene en una variable de entorno y no se guarda en Git.
