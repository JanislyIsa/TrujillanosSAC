# Análisis de calidad con SonarQube

Esta configuración analiza el backend y genera cobertura con JaCoCo. El token se entrega al proceso por una variable de entorno y no se guarda en archivos del repositorio.

## Iniciar SonarQube local

1. Abre Docker Desktop y espera a que el motor esté iniciado.
2. Desde la raíz del backend, ejecuta `docker compose -f docs/calidad/docker-compose.sonarqube.yml up -d`.
3. Espera a que `http://localhost:9000` muestre el inicio de sesión. En la primera entrada se usa `admin` / `admin` y SonarQube solicita cambiar la contraseña.
4. En SonarQube abre **My Account → Security → Generate Token**. Guárdalo fuera del repositorio.

## Analizar el backend

En PowerShell, desde la raíz del backend, asigna el token en la terminal:

```powershell
$env:SONAR_TOKEN = "TU_TOKEN_LOCAL"
.\mvnw.cmd clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.5.0.6356:sonar `
  "-Dsonar.host.url=http://localhost:9000" `
  "-Dsonar.token=$env:SONAR_TOKEN"
```

JaCoCo genera `target/site/jacoco/jacoco.xml` durante `verify` y SonarQube importa ese informe.

## Análisis inicial y final

Para la evidencia inicial, registra el commit y métricas antes de corregir incidencias. Después de corregirlas y crear un nuevo commit, repite el análisis y completa la tabla de comparación del informe. Las capturas deben mostrar el proyecto y las métricas, nunca el token.

En **Quality Gates**, crea `LP2-Curso`, establece las condiciones Overall Code indicadas en la guía y asígnalo al proyecto backend y al frontend. Comprueba el estado final en SonarQube antes de marcarlo como Passed en el informe.
