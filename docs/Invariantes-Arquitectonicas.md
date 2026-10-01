
---

## Título: Invariantes Arquitectónicas — CoreTransact-Mesh
Version: 1
Estado: Vigente
Fecha: 2026-09-30
Tags: [arquitectura, invariantes, fitness-functions]

# Invariantes Arquitectónicas

Reglas que **no se rompen** sin un ADR aprobado que las reemplace. Cada invariante debe tener una forma **automática** de verificarse (fitness function); si solo existe en este documento, todavía no está protegida.

Relacionados: [[CoreTransact-Mesh_Arquitectura_v2]] · [[ADR-Indice]] · [[ADR-0000-plantilla]]

## Cómo usar este documento

1. Antes de implementar un cambio, revisa si toca alguna invariante.
2. Si la toca, escribe primero un ADR (estado `Propuesto`) explicando por qué cambia.
3. Si se aprueba, actualiza esta tabla (estado `Reemplazada por ADR-XXXX`), **no la borres**.
4. Cada invariante debe tener su verificación automatizada en CI (columna "Verificación").

## Estados posibles

`Vigente` · `Pendiente de automatizar` · `Reemplazada por ADR-XXXX`

## Catálogo

| ID     | Invariante                                                                                                                                                   | Motivo                                                                             | Verificación (fitness function)                                                                                        | Servicio                      | ADR                | Estado                   |
| ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ----------------------------- | ------------------ | ------------------------ |
| INV-01 | La **Ingesta nunca accede** a PostgreSQL ni a MongoDB                                                                                                        | Aislamiento y seguridad; el Ledger es dueño de los datos contables                 | ArchUnit: sin dependencias a drivers de BD salvo Redis; el contenedor Ingesta no recibe credenciales de Postgres/Mongo | Ingesta                       | ADR-0001           | Pendiente de automatizar |
| INV-02 | **Solo el Ledger modifica saldos**. PostgreSQL es de su propiedad exclusiva                                                                                  | Una sola fuente de verdad contable                                                 | Conciliación e Ingesta sin credenciales de Postgres; revisión de conexiones en compose                                 | Ledger                        | ADR-0001           | Pendiente de automatizar |
| INV-03 | Todo cambio de saldo ocurre en **una única transacción ACID** que bloquea las cuentas en **orden ascendente de `Id`**                                        | Evitar saldos corruptos y deadlocks                                                | Test de concurrencia (cuentas calientes, transferencias cruzadas A↔B) sin deadlocks ni saldos negativos                | Ledger                        | ADR-0006           | Pendiente de automatizar |
| INV-04 | La idempotencia tiene **dos barreras**: Redis (Ingesta) y PK del asiento (Ledger). Ninguna se elimina sin ADR                                                | RabbitMQ es _at-least-once_; el TTL de Redis expira                                | Test: mismo `transactionId` N veces (rápido y tras expirar TTL) produce exactamente 1 asiento                          | Ingesta, Ledger               | ADR-0003           | Pendiente de automatizar |
| INV-05 | El **Ledger es la fuente de verdad**: las validaciones de la Ingesta (gRPC o caché) son optimización; el Ledger **siempre revalida**                         | Con circuit breaker abierto la Ingesta puede aceptar transferencias no verificadas | Test: transferencia `UNVERIFIED` con cuenta inválida termina en `transfer.failed`                                      | Ledger                        | ADR-0005           | Pendiente de automatizar |
| INV-06 | **Dominio y aplicación no dependen de infraestructura** (hexagonal en Java, Clean en .NET)                                                                   | Poder cambiar frameworks sin tocar reglas de negocio                               | ArchUnit (Java) y NetArchTest (.NET)                                                                                   | Todos                         | ADR-0001           | Pendiente de automatizar |
| INV-07 | Los **contratos** (`.proto`, JSON Schema) viven en `/contracts`, están versionados y un cambio incompatible exige nueva versión + ADR                        | Evitar roturas silenciosas entre servicios                                         | Contract tests en CI; validación de esquemas en publicación y consumo                                                  | Todos                         | ADR-0004           | Pendiente de automatizar |
| INV-08 | Los servicios solo se comunican por **gRPC (validación)** y **RabbitMQ (eventos)**. Sin REST entre servicios y sin BD compartida                             | Desacoplamiento real                                                               | Revisión de dependencias/clientes HTTP entre servicios; reglas de arquitectura                                         | Todos                         | ADR-0001, ADR-0005 | Pendiente de automatizar |
| INV-09 | Todo mensaje y todo log llevan `transactionId` como **correlation id**                                                                                       | Trazabilidad extremo a extremo                                                     | Test que verifica la cabecera `correlationId` en cada evento; revisión de logs estructurados                           | Todos                         | ADR-0012           | Pendiente de automatizar |
| INV-10 | **Ningún mensaje se pierde**: colas durables, mensajes persistentes, publisher confirms, ack manual tras persistir; los fallos van a DLQ, nunca se descartan | Garantía de entrega en un core financiero                                          | Pruebas de caos: detener consumidores/broker y verificar cero pérdidas                                                 | Todos                         | ADR-0004, ADR-0009 | Pendiente de automatizar |
| INV-11 | Todo **consumidor es idempotente**                                                                                                                           | Reentrega at-least-once                                                            | Test: reentregar el mismo mensaje N veces no cambia el resultado                                                       | Ledger, Conciliación, Ingesta | ADR-0003           | Pendiente de automatizar |
| INV-12 | La **auditoría es append-only**: sin `update` ni `delete`                                                                                                    | Evidencia inmutable                                                                | Usuario de Mongo con permisos solo de `insert`/`find`; test que intenta modificar y falla                              | Conciliación                  | ADR-0008           | Pendiente de automatizar |
| INV-13 | **Partida doble**: por asiento `Σ Débitos == Σ Créditos`, y la **suma total de saldos se conserva**                                                          | Integridad contable                                                                | Test de propiedad; verificación post-carga con SQL; conciliación                                                       | Ledger, Conciliación          | ADR-0007           | Pendiente de automatizar |
| INV-14 | El **event loop de WebFlux nunca se bloquea**                                                                                                                | Rendimiento y estabilidad del servicio reactivo                                    | BlockHound activo en los tests de Ingesta                                                                              | Ingesta                       | ADR-0002           | Pendiente de automatizar |
| INV-15 | **Ningún cambio de tecnología o framework sin ADR** aceptado antes de fusionar                                                                               | Evitar erosión arquitectónica                                                      | Checklist del PR / Definition of Done; plantilla de PR exige enlazar ADR                                               | Todos                         | ADR-0000           | Vigente                  |
| INV-16 | Cada servicio es **construible, ejecutable y testeable de forma independiente** (Testcontainers)                                                             | Autonomía de los componentes                                                       | CI por servicio                                                                                                        | Todos                         | ADR-0001           | Pendiente de automatizar |
| INV-17 | Todo caso de uso tiene **al menos una prueba de integración automatizada** y aparece en el diagrama C4 / arc42 (Definition of Done)                          | Documentación y pruebas vivas                                                      | Checklist del PR; revisión al cierre de cada sprint                                                                    | Todos                         | —                  | Vigente                  |

## Definition of Done (aplica a cada tarea del backlog)

- [ ] Las pruebas pasan (unitarias e integración) y el build de CI está en verde.
- [ ] Ninguna invariante vigente fue violada; si cambió alguna, existe un ADR.
- [ ] Contratos actualizados y versionados si hubo cambios.
- [ ] Diagramas C4 / documentación actualizados cuando corresponda.
- [ ] Anotaciones del día en `LEARNING.md`.

## Registro de cambios

|Fecha|Cambio|ADR|
|---|---|---|
|2026-09-30|Versión inicial (INV-01 a INV-17)|—|
