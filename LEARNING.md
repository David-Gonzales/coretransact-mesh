# LEARNING.md — Diario de aprendizaje de CoreTransact-Mesh

Bitácora personal del proyecto. Se completa en los **15 minutos de cierre** de cada día. No es documentación formal: es la memoria de lo que aprendí, lo que me costó y lo que decidí.

## Cómo usar este archivo

1. Al cerrar el día, copia la plantilla de entrada (abajo) al inicio de la sección **Registro diario** y complétala.
2. Escribe con tus palabras. Si no puedes explicar algo en 3 frases, todavía no lo entiendes: anótalo en **Preguntas abiertas**.
3. Cada viernes completa el **Cierre de semana**.
4. Cuando una decisión pese lo suficiente, no la dejes aquí: conviértela en un ADR y enlázalo.

---

## Plantilla de entrada diaria

```markdown
### YYYY-MM-DD · DXX · SCRUM-XX · título corto
- **Tiempo real:** X h  (estimado: 2,5 h)
- **Qué construí:**
  - …
- **Qué aprendí (con mis palabras):**
  - …
- **Explícalo en 3 frases** (como si se lo contaras a un compañero junior):
  …
- **Decisión tomada / ADR:** … (o "ninguna")
- **Lo que me bloqueó o me sorprendió:** …
- **Pregunta abierta:** … (pasarla a la sección de preguntas)
- **Primer paso de mañana:** …
```

---

## Registro diario

<!-- Las entradas más recientes van arriba. -->

### 2026-10-05 · D00B · SCRUM-24 · Docker compose corregido y verificación de infraestructura · Construcción, redacción de ADR-0002 y cierre
- **Tiempo real:** Construcción: 30min  (estimado: 1h 45min) | Redacción ADR-0002: 2h (estimado: 55min) | Total: 2h 30min
- **Qué construí:**
  - Construí el docker-compose.yml para 4 contenedores de infra: redis, rabbitmq, postgres y mongodb. Levanté los contenedores y comprobé que cada motor responde.
- **Qué aprendí:**
  - Aprendí los comandos básicos para levantar y apagar los contenedores. Un detalle importante es que hay que entender bien la diferencia entre `down` y `down -v`:
    ```powershell
    - docker compose -f infra/docker-compose.yml down        # apaga y conserva los datos (volúmenes)
    - docker compose -f infra/docker-compose.yml down -v     # apaga y BORRA los datos
    ```
  - Aprendí a escribir mejor un ADR pasándole a un modelo de IA (Claude) para que lo revise y me genere feedback, esto me llevó mucho más tiempo del previsto
- **Explícalo en 3 frases:** Un monorepo guarda todos los servicios juntos para versionar los contratos y documentación. Un ADR registra una decisión arquitectónica y su motivo para no discutirla dos veces. Las invariantes son las reglas que no se rompen sin un ADR.
- **Decisión tomada / ADR:** ADR-0002 Aceptada.
- **Lo que me bloqueó o me sorprendió:** Me sorprendió la manera estructurada de organizar un proyecto grande; son muchos pasos, sin embargo, me puedo acostumbrar fácilmente a esta metodología ágil.


### 2026-10-04 · D00B · SCRUM-24 · Docker compose corregido y verificación de infraestructura · Aprendizaje
- **Tiempo real:** 4h  (estimado: 30 min)
- **Qué aprendí:**
  - Entendí cómo se estructura un ADR por dentro, me di cuenta que habían términos nuevos para mí y me puse a investigarlos y los anoté para recordarlos. Después de leer este ADR-0001 lo terminé aceptando sin modificaciones.
- **Explícalo en 3 frases:** Aprendí sobre el registro que se lleva de las decisiones arquitectónicas tomadas, leí a detalle cada una de las invariantes o reglas que se establecen y las acepté. Existieron conceptos nuevos para mí como diagrama C4, procedimientos remotos, message broker, AMPQ, Integración Continua, cómo ArchUnit puede automatizar que un servicio no se conecte a bases de datos a las que no debería tener acceso, entre otros conceptos.
- **Decisión tomada / ADR:** ADR-0001 Aceptada.
- **Lo que me bloqueó o me sorprendió:** Me sorprendió que cuando más iba leyendo sobre un texto o tema en específico habían términos que jamás había escuchado y eso me obligaba a ir a buscarlos y unir los conceptos y volver al texto para entenderlo y que tenga significado para mí. Esta tarea de media hora me llevó mucho más tiempo del que había estimado.
- **Primer paso de mañana:** poner docker compose en el repo, levantar infra, comprobar cada motor funcionando y redactar un ADR-0002

### 2026-10-01 · D00A · SCRUM-23 · Repo monorepo y gobernanza base
- **Tiempo real:** 1h 6m  (estimado: 2,5 h)
- **Qué construí:**
  - Estructura del monorepo, plantilla de Pull Request (PR), convención de commits y primer push.
- **Qué aprendí:**
  - Un ADR aceptado no se edita; se reemplaza por otro, así queda la historia de por qué cambió.
- **Explícalo en 3 frases:** Un monorepo guarda todos los servicios juntos para versionar los contratos y documentación. Un ADR registra una decisión arquitectónica y su motivo para no discutirla dos veces. Las invariantes son las reglas que no se rompen sin un ADR.
- **Decisión tomada / ADR:** ninguna todavía.
- **Lo que me bloqueó o me sorprendió:** Me sorprendió la manera estructurada de organizar un proyecto grande; son muchos pasos, sin embargo, me puedo acostumbrar fácilmente a esta metodología ágil.
- **Pregunta abierta:** ¿Cómo automatizo INV-01 con ArchUnit?
- **Primer paso de mañana:** docker-compose corregido y `docker compose ps` con 4 servicios healthy.

---

## Registro semanal

| Semana | Días | Horas reales | Horas estimadas | Desviación | Notas |
|---|---|---|---|---|---|
| S0 | D00A-D00B | | 5 | | |
| S1 · sem 1 | D01-D05 | | 12,5 | | |
| S1 · sem 2 | D06-D10 | | 12,5 | | |
| S2 · sem 1 | D11-D15 | | 12,5 | | |
| S2 · sem 2 | D16-D20 | | 12,5 | | |
| S3 · sem 1 | D21-D25 | | 12,5 | | |
| S3 · sem 2 | D26-D30 | | 12,5 | | |
| S4 · sem 1 | D31-D35 | | 12,5 | | |
| S4 · sem 2 | D36-D40 | | 12,5 | | |
| S5 · sem 1 | D41-D45 | | 12,5 | | |
| S5 · sem 2 | D46-D50 | | 12,5 | | |
| S6 · sem 1 | D51-D55 | | 12,5 | | |
| S6 · sem 2 | D56-D60 | | 12,5 | | |

> Regla práctica: si dos semanas seguidas la desviación supera el 30 %, ajusta el plan (mover tareas, recortar opcionales como ADR-0011) en lugar de acumular atraso en silencio.

---

## Plantilla de cierre de semana

```markdown
### Cierre de semana · Sprint X · semana N
- **Lo que salió bien:**
- **Lo que me costó:**
- **Lo que cambiaría del plan:**
- **Invariantes que ya tienen verificación automática:** INV-…
- **ADRs redactados / pendientes:**
- **Ajuste al backlog (qué muevo o recorto):**
- **Una cosa que ahora entiendo y hace una semana no:**
```

---

## Preguntas abiertas

Cosas que no entendí del todo. Al resolver una, tacha la pregunta y escribe la respuesta.

| Fecha | Pregunta | Estado | Respuesta / recurso |
|---|---|---|---|
| | | Abierta | |

---

## Glosario personal

Términos que fui aprendiendo, con mis palabras.

| Término | Mi definición | Día en que lo vi |
|---|---|---|
| Idempotencia | Propiedad de una operación que permite aplicarla varias veces sin cambiar el resultado obtenido después de la primera operación | 2026-10-04 |
| Backpressure | | |
| Publisher confirm | | |
| Deadline (gRPC) | | |
| Circuit breaker | | |
| Partida doble | | |

---

## Banco de historias para entrevistas

Cuando algo salga especialmente bien (o mal), anótalo en formato STAR. Servirá para el CV y para entrevistas de arquitectura.

```markdown
### Título de la historia
- **Situación:** ¿qué problema había?
- **Tarea:** ¿qué tenía que resolver?
- **Acción:** ¿qué decidí y por qué? (enlazar ADR)
- **Resultado:** ¿qué medí? (TPS, latencia p95, saldos íntegros, etc.)
- **Qué aprendí / qué haría distinto:**
```

Ideas de historias que probablemente aparezcan:

- El deadlock de las transferencias cruzadas y el locking ordenado.
- Por qué la idempotencia necesita dos barreras.
- Qué pasó al apagar el Ledger en pleno tráfico.
- Un cambio de decisión y cómo quedó registrado en un ADR.

---

## Recursos útiles

| Tema | Recurso | Para qué lo usé |
|---|---|---|
| | | |
