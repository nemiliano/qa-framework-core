# qa-framework-core

Librería base de automatización de pruebas (Java, JUnit, Selenium, Playwright). No contiene tests de negocio.

## Requisitos
- JDK 25 LTS (el bytecode se genera para Java 17)
- Git. Maven no hace falta instalarlo: se usa el wrapper (`mvnw`).

## Estructura
| Módulo | Contenido |
|---|---|
| `bom` | Fija las versiones de todas las librerías (Bill Of Materials) |
| `core` | Configuración por ambiente (`QaConfig`), logging (Log4j2) y excepciones |
| `reporting` | Evidencias (disco + Allure) y nombres de evidencia |
| `data` | Datos de prueba desde CSV/JSON a `record`, con "Caso de prueba" (`@CsvTestData`, `@JsonTestData`) |
| `api` | Cliente HTTP (REST Assured) con request/response en Allure |
| `db` | JDBC con pool (HikariCP), consultas parametrizadas y transacciones |
| `files` | Archivos temporales, CSV, JSON y espera de descargas |
| `ui-selenium` | WebDriver (Selenium Manager), `BasePage`/`BaseComponent` con waits explícitos |
| `ui-playwright` | Sesión Playwright (Browser/Context/Page por test), tracing |
| `junit-extensions` | `QaExtension`, `SeleniumExtension`, `PlaywrightExtension` (evidencias ante fallo) |

## Comandos
```powershell
.\mvnw.cmd clean verify          # compila, testea y chequea formato
.\mvnw.cmd clean install         # además instala en tu ~/.m2 para usarlo desde otro proyecto
# Formatear el código (Spotless). Se excluye el bom, que no usa el plugin:
.\mvnw.cmd com.diffplug.spotless:spotless-maven-plugin:3.10.3:apply -pl "!bom"
```

## Configuración
Prioridad: variable de ambiente > propiedad de sistema > `config/<ambiente>.properties` > valor por defecto.

| Clave | Variable de ambiente | Propiedad de sistema |
|---|---|---|
| `base.url` | `BASE_URL` | `-Dbase.url=...` |
| `headless` | `HEADLESS` | `-Dheadless=true` |
| `qa.env` (ambiente) | `QA_ENV` | `-Dqa.env=qa` |

Secretos (usuarios, claves, connection strings): **nunca** en el repo; usar variables de ambiente o `.env` (ignorado por git).

## Cómo usar la extensión en un test
```java
@ExtendWith(QaExtension.class)
class MiTest {
  @Test
  void ejemplo(QaConfig config) {
    // config.baseUrl(), config.headless(), ...
  }
}
```

## Tests con navegador
Llevan `@Tag("browser")` y se excluyen por defecto. Una sola vez, instalar los navegadores de Playwright:
```powershell
.\mvnw.cmd install -DskipTests
.\mvnw.cmd -pl ui-playwright exec:java "-Dexec.mainClass=com.microsoft.playwright.CLI" "-Dexec.args=install chromium"
```
Y para correrlos: `.\mvnw.cmd clean verify -DexcludedGroups=none`.

## Usar el framework desde otro proyecto
**Local:** `.\mvnw.cmd clean install` instala todo en tu `~/.m2`. En el otro proyecto se importa el BOM y se declaran solo los módulos necesarios (sin versión).

**GitHub Packages:** al crear un tag `vX.Y.Z`, GitHub Actions (`publish.yml`) fija esa versión en los poms, corre los tests, publica los 11 módulos y crea el Release. Para publicar una versión nueva:
```powershell
git tag -a v0.2.0 -m "Descripción" ; git push origin v0.2.0
```
Para consumirlo desde otro proyecto, GitHub Packages exige autenticarse incluso en repos públicos:
1. Creá un token (classic) con permiso `read:packages` y agregalo en `~/.m2/settings.xml`:
```xml
<servers>
  <server>
    <id>github</id>
    <username>TU_USUARIO_DE_GITHUB</username>
    <password>${env.GITHUB_TOKEN}</password>   <!-- el token va en una variable de ambiente, no escrito acá -->
  </server>
</servers>
```
2. En el `pom.xml` del proyecto consumidor:
```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/nemiliano/qa-framework-core</url>
  </repository>
</repositories>
```
y se importa el BOM `com.nemiliano.qa:qa-framework-bom:0.1.0` en `dependencyManagement`, como hace `qa-tests-demo`.

## CI (GitHub Actions)
- `ci.yml`: en cada push y Pull Request a `main` corre `verify` (tests unitarios y formato) y, en otro job, los tests con navegador.
- `publish.yml`: al crear un tag `v*` publica en GitHub Packages.
- La rama `main` está protegida: los cambios entran por Pull Request con los checks en verde.

**Versionado semántico:** `MAYOR.MENOR.PARCHE`. Parche = corrección sin cambios de API; menor = funcionalidad nueva compatible; mayor = cambio incompatible. Los tags son `v0.1.0`, `v0.2.0`, etc.
