---
id: ADR-0001
titulo: Estilo arquitectónico - microservicios, EDA y CQRS; hexagonal en Java y Clean en .NET
estado: Aceptado
fecha: 2026-10-05
decisores:
  - David
reemplaza: ""
reemplazado_por: ""
servicios:
  - ingesta
  - ledger
  - conciliacion
  - infra
invariantes:
  - INV-01
  - INV-02
  - INV-06
  - INV-08
  - INV-16
tags:
  - adr
  - estilo-arquitectonico
---

# ADR-0001: Estilo arquitectónico

## 1. Contexto

CoreTransact-Mesh debe absorber ráfagas de transferencias (meta: 1.000 en 5 s), garantizar saldos íntegros bajo concurrencia y poder auditar y conciliar sin bloquear la ruta de escritura. Se ejecuta en un solo equipo de escritorio (12 hilos, 16 GB) y, además de funcionar, es un proyecto de portafolio cuyo objetivo es demostrar criterio de arquitectura y aprender Java WebFlux, .NET 10, RabbitMQ y gRPC. El tiempo disponible es de 2 a 3 horas diarias.

Si no se decide un estilo desde el inicio, el sistema tiende a crecer como un bloque donde la ingesta, la contabilidad y la auditoría se acoplan entre sí, y las pruebas de concurrencia y resiliencia dejan de tener sentido.

## 2. Fuerzas (criterios de decisión)

- **Rendimiento ante ráfagas:** la ingesta no puede depender de la velocidad del Ledger.
- **Integridad contable:** una única fuente de verdad para los saldos.
- **Resiliencia:** la caída de un componente no debe detener a los demás.
- **Valor formativo:** el estilo debe obligarme a practicar mensajería, RPC y persistencia políglota.
- **Complejidad operativa y tiempo:** debe ser viable para una sola persona en 2 a 3 horas diarias.
- **Evitar la erosión:** las fronteras deben poder verificarse automáticamente.

## 3. Opciones consideradas

| Opción | Pros | Contras |
|---|---|---|
| A. Monolito modular (un solo despliegue, módulos internos) | Mucho más simple de operar y depurar; transacciones locales | No ejercita mensajería ni RPC; la ingesta y el Ledger compartirían recursos; menor valor formativo y de portafolio |
| B. Microservicios síncronos (REST entre servicios) | Fácil de entender | Acoplamiento temporal; si cae el Ledger se cae la ingesta; no absorbe ráfagas |
| C. **Microservicios orientados a eventos (EDA) con CQRS** y un único tramo síncrono (gRPC) | Absorbe ráfagas con colas; resiliencia; separación de lectura y escritura; responde a los objetivos formativos | Mayor complejidad operativa; consistencia eventual; exige disciplina con contratos e idempotencia |

## 4. Decisión

Se adopta la **opción C**: tres microservicios (Ingesta, Ledger, Conciliación) que se comunican por **eventos en RabbitMQ**, con **gRPC** como única llamada síncrona (validación de cuentas, ver ADR-0005) y **CQRS** para separar comandos de consultas.

Dentro de cada servicio se aplica una arquitectura que protege el dominio de la infraestructura:

- **Ingesta (Java):** arquitectura **hexagonal** (puertos y adaptadores).
- **Ledger y Conciliación (.NET):** **Clean Architecture** con MediatR.

Cada base de datos pertenece a un único servicio.

## 5. Consecuencias

**Positivas**

- La ingesta responde `202` rápido y el Ledger procesa a su ritmo.
- La caída del Ledger o de Mongo no pierde mensajes.
- Los límites entre servicios son verificables con pruebas de arquitectura.

**Negativas / deuda aceptada**

- Consistencia eventual: el estado final de una transferencia no es inmediato (se mitiga con CU-14, ver ADR-0010).
- Más piezas que operar en local (4 contenedores de infraestructura y 3 servicios).
- Necesidad de versionar contratos y de idempotencia en todos los consumidores.

**Riesgos y mitigaciones**

- *Sobrediseño para el tamaño del problema:* se acepta de forma consciente por el objetivo formativo y de portafolio; se documenta aquí para no presentarlo como la opción mínima.
- *Erosión de las fronteras:* invariantes + ArchUnit/NetArchTest en CI.

## 6. Invariantes afectadas

INV-01, INV-02, INV-06, INV-08 e INV-16 en [[Invariantes-Arquitectonicas]].

## 7. Cómo se verifica (fitness function)

- ArchUnit (Java) y NetArchTest (.NET): el dominio no depende de infraestructura (INV-06).
- Ingesta sin dependencias a drivers de PostgreSQL ni MongoDB (INV-01).
- Revisión de `docker-compose` y de credenciales: cada servicio solo tiene acceso a su propio almacén (INV-02).
- CI por servicio (INV-16).

## 8. Plan de implementación

- [x] D00B: redactar y aceptar este ADR.
- [ ] D10 y D21: reglas ArchUnit y NetArchTest iniciales.
- [ ] D10: C4 nivel 2 v1.

## 9. Notas de aprendizaje

- La diferencia entre *desacoplar por tiempo* (colas) y *desacoplar por contrato* (esquemas versionados).
- Por qué CQRS no obliga a tener dos bases de datos: aquí la separación es de modelos y de servicios.

## 10. Referencias

- [[CoreTransact-Mesh_Arquitectura_v2]] (secciones 2, 3 y 8)
- [[ADR-Indice]]
