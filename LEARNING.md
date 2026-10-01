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



| INV-02 | **Solo el Ledger modifica saldos**. PostgreSQL es de su propiedad exclusiva                                           |
| ------ | --------------------------------------------------------------------------------------------------------------------- |
| INV-03 | Todo cambio de saldo ocurre en **una única transacción ACID** que bloquea las cuentas en **orden ascendente de `Id`** |
| INV-04 | La idempotencia tiene **dos barreras**: Redis (Ingesta) y PK del asiento (Ledger). Ninguna se elimina sin ADR         |
|        |                                                                                                                       |

### 2026-10-01 · D00A · SCRUM-23 · Repo monorepo y gobernanza base
- **Tiempo real:** 2,5 h  (estimado: 2,5 h)
- **Qué construí:**
  - Estructura del monorepo, plantilla de PR, convención de commits y primer push.
- **Qué aprendí (con mis palabras):**
  - Un ADR aceptado no se edita; se reemplaza por otro, así queda la historia de por qué cambió.
- **Explícalo en 3 frases:** Un monorepo guarda todos los servicios juntos para versionar contratos y documentación a la par. Un ADR registra una decisión y su motivo para no discutirla dos veces. Las invariantes son las reglas que no se rompen sin un ADR.
- **Decisión tomada / ADR:** ninguna todavía (ADR-0001 y 0002 en D00B).
- **Lo que me bloqueó o me sorprendió:** 
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
| Idempotencia | | |
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
