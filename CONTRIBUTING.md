# Contribuir

## Ramas
- `main`: siempre verde y protegida (cambios solo por Pull Request).
- `feature/<tema-corto>`, `fix/<tema-corto>`, `docs/<tema-corto>`.

## Commits (Conventional Commits)
`<tipo>(<alcance>): <descripción en imperativo>`

Tipos: `feat`, `fix`, `docs`, `test`, `refactor`, `build`, `ci`, `chore`.
Ejemplo: `feat(core): agregar lectura de configuración por ambiente`.

## Nombres
- Código, clases y métodos en **inglés**; documentación, `@DisplayName` y mensajes de reporte en **español**.
- Clases de test terminan en `Test`; métodos describen el comportamiento (`unknownEnvironmentFails`).
- Un test sigue AAA (Arrange-Act-Assert) y verifica una sola cosa lógica.

## Antes de abrir un PR
1. `.\mvnw.cmd clean verify` en verde.
2. Código formateado (Spotless).
3. Tests nuevos para código nuevo.
4. Decisiones de arquitectura importantes: un ADR corto.
