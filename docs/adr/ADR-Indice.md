
---

Título: Índice de ADRs — CoreTransact-Mesh
Fecha: 2026-09-30
Tags: [adr, indice]

# Índice de ADRs

Plan inicial de decisiones de arquitectura. Cada fila se convierte en un archivo `ADR-XXXX-titulo.md` creado a partir de [[ADR-0000-plantilla]]. Las decisiones marcadas como **pre-decididas** ya se tomaron durante la revisión del documento de arquitectura; falta solo redactarlas formalmente.

| ADR      | Decisión                                                                               | Estado       | Sprint            | Invariantes            |
| -------- | -------------------------------------------------------------------------------------- | ------------ | ----------------- | ---------------------- |
| ADR-0001 | Estilo arquitectónico: microservicios, EDA y CQRS; hexagonal en Java y Clean en .NET   | Aceptado     | S0                | INV-01, 02, 06, 08, 16 |
| ADR-0002 | Stack políglota: Java 21 + WebFlux (Ingesta), .NET 10 (Ledger y Conciliación)          | Aceptado     | S0                | INV-14                 |
| ADR-0003 | Idempotencia en dos barreras (`SET NX EX` en Redis + PK del asiento en Ledger)         | Pre-decidida | S1                | INV-04, 11             |
| ADR-0004 | Topología RabbitMQ y contratos de eventos (publisher confirms, ack manual, versionado) | Pendiente    | S2                | INV-07, 10             |
| ADR-0005 | gRPC Ingesta→Ledger con deadline, circuit breaker y fallback (fail-open controlado)    | Pre-decidida | S2                | INV-05, 08             |
| ADR-0006 | Locking ordenado (`ORDER BY Id FOR UPDATE`) y nivel de aislamiento                     | Pre-decidida | S3                | INV-03                 |
| ADR-0007 | Modelo de partida doble (`LineasAsiento`)                                              | Pre-decidida | S3                | INV-13                 |
| ADR-0008 | Auditoría append-only en MongoDB (permisos e índice único)                             | Pendiente    | S4                | INV-12                 |
| ADR-0009 | Reintentos y DLQ (DLX, cola de espera con TTL, política de descarte)                   | Pendiente    | S5                | INV-10                 |
| ADR-0010 | Modelo de lectura de estado de transferencias en Redis                                 | Pre-decidida | S5                | INV-01                 |
| ADR-0011 | Transactional Outbox en el Ledger (opcional)                                           | Opcional     | S5                | INV-10                 |
| ADR-0012 | Observabilidad: correlation id y OpenTelemetry                                         | Pendiente    | S5                | INV-09                 |
| ADR-0013 | Representación de importes y fechas                                                    | Aceptado     | S2 (Antes de D12) | INV-07, 18, 19         |
| ADR-0014 | Versión de Spring Boot, JDK y Gradle (Refina ADR-0002)                                 | Aceptado     | S1 (D01)          | INV-14, 15, 16         |

## Reglas

- Un ADR **Aceptado** no se reescribe: se crea otro que lo reemplaza y se actualizan ambos estados.
- Todo ADR indica qué **invariantes** toca y cómo se **verifica** automáticamente.
- Si surge una decisión no prevista (por ejemplo, cambiar un framework), se crea el siguiente número libre **antes** de implementarla.
