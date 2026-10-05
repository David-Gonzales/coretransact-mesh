---
title: D00A — Proyecto Jira, repo monorepo y gobernanza base
jira: SCRUM-23
sprint: S0 - Fundamentos
estimacion: 2h 30m
epica: EP01 - Setup y gobernanza arquitectonica
tags: [checklist, sprint-0, gobernanza]
---

# D00A — Repo monorepo y gobernanza base

**Criterio de hecho (Jira):** repo creado, tablero con épicas, plantillas en `/docs`.
**Meta del día:** que cualquier decisión o cambio futuro tenga un lugar claro donde vivir *antes* de escribir código.

Tablero de Jira ya importado, así que esa parte está hecha. Hoy es el repositorio.

## Reparto del tiempo (2 h 30 min)

| Bloque | Tiempo | Qué |
|---|---|---|
| Aprendizaje | 30 min | Monorepo y ADRs (lecturas abajo) |
| Construcción | 1 h 45 min | Pasos 1 a 7 |
| Cierre | 15 min | Commit, Jira y `LEARNING.md` |

---

## Bloque 1 — Aprendizaje (30 min)

Objetivo: poder explicar con tus palabras (3 frases) por qué un monorepo y por qué un ADR no se reescribe.

- [x] Leer el apartado de contexto y decisión de la plantilla [[ADR-0000-plantilla]] (5 min).
- [x] Buscar y leer una introducción a **MADR** (Markdown Any Decision Records) y a **monorepo vs polirepo** (15 min).
- [ ] Recorrer [[Invariantes-Arquitectonicas]] y marcar las 3 que más te cuesta imaginar cómo automatizar (10 min). Anótalas en `LEARNING.md`.

---

## Bloque 2 — Construcción (1 h 45 min)

### Paso 1. Crear el repositorio y la estructura (20 min)

Desde PowerShell (o Git Bash) en la carpeta donde guardes tus proyectos:

```powershell
mkdir coretransact-mesh
cd coretransact-mesh
git init -b main

# Carpetas
mkdir .github, contracts\events, contracts\grpc, infra, loadtests
mkdir services\ingestion-java, services\ledger-dotnet, services\reconciliation-dotnet
mkdir docs\adr, docs\c4, docs\arc42, docs\architecture

# Para que Git conserve las carpetas vacías
"" | Out-File contracts\events\.gitkeep
"" | Out-File contracts\grpc\.gitkeep
"" | Out-File loadtests\.gitkeep
"" | Out-File services\ingestion-java\.gitkeep
"" | Out-File services\ledger-dotnet\.gitkeep
"" | Out-File services\reconciliation-dotnet\.gitkeep
"" | Out-File docs\c4\.gitkeep
"" | Out-File docs\arc42\.gitkeep
```

Estructura resultante:

```
coretransact-mesh/
├── .github/
│   └── pull_request_template.md
├── contracts/
│   ├── events/            # JSON Schema de eventos
│   └── grpc/              # archivos .proto
├── services/
│   ├── ingestion-java/
│   ├── ledger-dotnet/
│   └── reconciliation-dotnet/
├── infra/                 # docker-compose.yml (D00B)
├── loadtests/             # scripts k6
├── docs/                  # <- bóveda de Obsidian
│   ├── architecture/
│   ├── adr/
│   ├── c4/
│   ├── arc42/
│   └── Invariantes-Arquitectonicas.md
├── LEARNING.md
├── README.md
├── .gitignore
└── .editorconfig
```

- [x] Estructura creada y `git status` la reconoce.

### Paso 2. Copiar los documentos a `/docs` (10 min)

| Archivo | Destino |
|---|---|
| `CoreTransact-Mesh_Arquitectura_v2.md` | `docs/architecture/` |
| `Invariantes-Arquitectonicas.md` | `docs/` |
| `ADR-0000-plantilla.md` | `docs/adr/` |
| `ADR-Indice.md` | `docs/adr/` |
| `CoreTransact-Mesh_Documentacion_Inicial.docx` | `docs/architecture/` |

> [!warning] No renombres estos archivos
> Los enlaces de Obsidian (`[[ADR-Indice]]`, `[[Invariantes-Arquitectonicas]]`…) se resuelven por **nombre de archivo**. Si cambias un nombre, el enlace se rompe. Los ADRs nuevos llevarán nombres como `ADR-0001-estilo-arquitectonico.md`.

- [x] Archivos copiados con los nombres originales.
- [x] Abrir **`/docs` como bóveda de Obsidian** (*Abrir carpeta como bóveda*) y comprobar que los enlaces `[[...]]` funcionan y que los diagramas Mermaid se renderizan.

### Paso 3. Archivos base del repo (15 min)

**`.gitignore`** (mínimo para Java, .NET y editores; lo ampliarás al crear cada servicio):

```gitignore
# Java / Gradle / Maven
target/
build/
.gradle/
*.class

# .NET
bin/
obj/
*.user

# IDEs
.idea/
.vs/
.vscode/*
!.vscode/extensions.json

# Entorno local y secretos
.env
*.log

# Obsidian (configuración local de la bóveda)
docs/.obsidian/workspace*.json
```

**`.editorconfig`**:

```ini
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true
indent_style = space
indent_size = 4

[*.{yml,yaml,json,md}]
indent_size = 2
```

**`README.md`** (esqueleto; se completa en S6):

```markdown
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
```

- [x] `.gitignore`, `.editorconfig` y `README.md` creados.

### Paso 4. Plantilla de Pull Request (15 min)

Aunque trabajes solo, úsala: crea el hábito y deja evidencia de que cada cambio respeta las reglas.

**`.github/pull_request_template.md`**:

```markdown
## Qué cambia
<!-- Resumen en 1-3 líneas -->

## Jira
SCRUM-XX

## Tipo de cambio
- [ ] Funcionalidad
- [ ] Corrección
- [ ] Refactor
- [ ] Documentación / ADR
- [ ] Infraestructura / CI

## Arquitectura
- [ ] No viola ninguna invariante de `docs/Invariantes-Arquitectonicas.md`
- [ ] Si toca una invariante o introduce una tecnología nueva: ADR enlazado -> ADR-XXXX
- [ ] Contratos (`/contracts`) actualizados y versionados si cambiaron

## Definition of Done
- [ ] Pruebas unitarias y de integración en verde
- [ ] CI en verde
- [ ] C4 / documentación actualizados si corresponde
- [ ] Anotaciones del día en `LEARNING.md`
```

- [x] Plantilla creada.

### Paso 5. Convención de ramas y commits (10 min)

Define esto ahora; luego no se discute:

| Tema             | Regla                                                                               |
| ---------------- | ----------------------------------------------------------------------------------- |
| Rama principal   | `main` siempre estable (CI en verde desde D10)                                      |
| Ramas de trabajo | Una por día o tarea, corta: `d01-webflux-skeleton`, `adr-0003-idempotencia`         |
| Commits          | `tipo(servicio): mensaje` — por ejemplo `feat(ingesta): idempotencia con SET NX EX` |
| Tipos            | `feat`, `fix`, `test`, `docs`, `refactor`, `chore`, `adr`                           |
| Enlace a Jira    | Incluir la clave en el cuerpo o al final: `[SCRUM-29]`                              |
| Fusión           | Pull request incluso trabajando solo (aunque te lo apruebes tú)                     |

- [x] Convención escrita en la sección "Convenciones" del `README.md` o en `docs/` (a tu elección).

### Paso 6. Crear `LEARNING.md` en la raíz (5 min)

Copia la plantilla `LEARNING.md` que te entregué junto a este checklist y deja listas la sección de registro semanal y la de preguntas abiertas.

- [x] `LEARNING.md` en la raíz del repo.

### Paso 7. Primer commit y repositorio remoto (15 min)

```powershell
git add .
git commit -m "chore: estructura inicial del monorepo y gobernanza base [SCRUM-23]"
```

Crea un repositorio vacío en GitHub (privado al inicio si lo prefieres; lo harás público en S6 con el README para el CV) y enlázalo:

```powershell
git remote add origin <URL-del-repositorio>
git push -u origin main
```

- [x] Primer commit hecho y subido.

---

## Bloque 3 — Cierre (15 min)

- [x] Mover **SCRUM-23** a *Hecho* en Jira.
- [x] Registrar el tiempo real en la historia (*Log work*): te servirá para recalibrar el plan en la semana 1.
- [x] Completar tu entrada de hoy en `LEARNING.md`.
- [x] Anotar en `LEARNING.md` el **primer paso de mañana**: D00B (docker-compose corregido, `docker compose ps` con 4 servicios *healthy*, y redacción de ADR-0001 y ADR-0002).

## Criterios de aceptación del día

- [x] El repositorio tiene la estructura acordada y está en GitHub.
- [x] `/docs` abre como bóveda de Obsidian con los enlaces funcionando.
- [x] Existen: plantilla de PR, `.gitignore`, `.editorconfig`, `README.md`, `LEARNING.md`.
- [x] La convención de ramas y commits está escrita.
- [x] SCRUM-23 está cerrada con el tiempo real registrado.

## Si te quedas corto de tiempo

Prioridad si no alcanzas todo en una sesión: **1) estructura y copia de documentos, 2) plantilla de PR, 3) primer commit**. El `.editorconfig` y el README pueden esperar a D00B sin costo.

Siguiente: **D00B** — `docker-compose` corregido y ADR-0001 / ADR-0002 (ver [[ADR-Indice]]).
