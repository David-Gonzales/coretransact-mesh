# CoreTransact-Mesh

Core transaccional bancario de alta concurrencia: Java 21 (WebFlux) + .NET 10,
RabbitMQ, gRPC, Redis, PostgreSQL y MongoDB.

> Proyecto en desarrollo. Documentación en [`/docs`](docs/).

## Documentación
- Arquitectura: `docs/architecture/CoreTransact-Mesh_Arquitectura_v2.md`
- Invariantes: `docs/Invariantes-Arquitectonicas.md`
- Decisiones (ADRs): `docs/adr/ADR-Indice.md`

## Estado
Ver el tablero de Jira (proyecto SCRUM).

## Convenciones
| Tema             | Regla                                                                               |
| ---------------- | ----------------------------------------------------------------------------------- |
| Rama principal   | `main` siempre estable (CI en verde desde D10)                                      |
| Ramas de trabajo | Una por día o tarea, corta: `d01-webflux-skeleton`, `adr-0003-idempotencia`         |
| Commits          | `tipo(servicio): mensaje` — por ejemplo `feat(ingesta): idempotencia con SET NX EX` |
| Tipos            | `feat`, `fix`, `test`, `docs`, `refactor`, `chore`, `adr`                           |
| Enlace a Jira    | Incluir la clave en el cuerpo o al final: `[SCRUM-29]`                              |
| Fusión           | Pull request incluso trabajando solo (aunque me lo apruebe a mí mismo)                     |