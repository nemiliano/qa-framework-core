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
| `junit-extensions` | `QaExtension`: logging de ciclo de vida e inyección de `QaConfig` |

(Se irán sumando `ui-selenium`, `ui-playwright`, `data`, `api`, `db`, `files`, `reporting`.)

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
