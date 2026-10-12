---
id: ADR-0002
titulo: "Stack políglota: Java 21 + WebFlux (Ingesta), .NET 10 (Ledger y Conciliación)"
estado: Aceptado
fecha: 2026-10-05
decisores:
  - David
reemplaza: ""
reemplazado_por: ""
refinado por: ADR-0013, ADR-0014
servicios:
  - ingesta
  - ledger
  - conciliacion
  - infra
invariantes:
  - INV-06
  - INV-07
  - INV-09
  - INV-14
  - INV-16
tags:
  - stack-poliglota
---

# ADR-0002: Stack Políglota

## 1. Contexto

CoreTransact-Mesh podría construirse con un solo lenguaje (ya sea .NET o Java) y esa sería la opción más simple, económica y rápida de mantener para un sistema nuevo. Sin embargo, este proyecto tiene un objetivo formativo y de portafolio: aprender Java 21 con Spring WebFlux (programación reactiva), practicar .NET 10 en servicios transaccionales y demostrar criterio de arquitectura en un entorno donde conviven varias plataformas, algo habitual en sistemas bancarios ya existentes que hay que mantener y evolucionar.

El punto de partida es un dominio sólido de .NET/C#; WebFlux, RabbitMQ y gRPC son tecnologías que aún se están aprendiendo.

Restricciones: una sola persona, un equipo de escritorio (12 hilos, 16 GB de RAM) y 2 a 3 horas diarias. Hay que decidir qué plataforma implementa cada servicio antes de crear los proyectos.

## 2. Fuerzas (criterios de decisión)

- **Valor formativo y de portafolio:** practicar ambas plataformas y demostrar criterio de arquitectura.
- **Ajuste técnico por servicio:** la ingesta es una puerta de entrada limitada por E/S y con alta concurrencia; el Ledger y la Conciliación concentran lógica de dominio contable y acceso a datos.
- **Curva de aprendizaje y riesgo de calendario:** más plataformas nuevas implican más tiempo hasta tener algo funcionando.
- **Costo de mantener dos plataformas (en tiempo):** CI, observabilidad y reglas de arquitectura duplicadas.
- **Memoria disponible:** dos runtimes (JVM y .NET) más cuatro contenedores de infraestructura en 16 GB.

## 3. Opciones consideradas

| Opción                                                              | Pros                                                                                                                                                                                                              | Contras                                                                                                                                                                                                           |
| ------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| A. Todo en .NET                                                     | Una sola plataforma y un solo CI; aprovecha el dominio actual (menor riesgo de calendario); Kestrel con `async/await` maneja alta concurrencia sin bloquear; EF Core, Dapper y MediatR cubren bien el Ledger      | No ejercita Java ni la programación reactiva con WebFlux, que es parte del objetivo formativo; menor exposición a un entorno políglota                                                                            |
| B. Todo en Java                                                     | Ecosistema Spring maduro; WebFlux para la ingesta; los hilos virtuales de Java 21 permiten código bloqueante simple (JDBC) en el Ledger sin agotar hilos de plataforma; una sola plataforma                       | Curva de aprendizaje alta en todos los servicios a la vez (riesgo de calendario); no aprovecha el dominio actual de .NET ni practica .NET 10                                                                      |
| C. Java 21 + WebFlux para Ingesta y .NET para Ledger y Conciliación | Cada servicio usa una plataforma que encaja con su carga; cumple el objetivo formativo; obliga a contratos explícitos (`.proto`, JSON Schema) y a pruebas de arquitectura por plataforma; refleja entornos reales | Dos toolchains y dos pipelines de CI; dos runtimes en 16 GB; reglas de arquitectura duplicadas (ArchUnit y NetArchTest); riesgo de divergencias de serialización entre plataformas; trazabilidad entre dos stacks |

> Ninguna de las tres opciones es técnicamente inviable para el rendimiento objetivo (≈ 200 TPS). La diferencia relevante es formativa y de costo de mantenimiento, no de capacidad.

## 4. Decisión

Se adopta la **opción C**: Java 21 con Spring Boot 3 y WebFlux para la **Ingesta** (herramienta de build: Gradle), y .NET 10 para el **Ledger** y la **Conciliación**.

Se elige principalmente por valor formativo y de portafolio, y porque cada plataforma encaja con su servicio: WebFlux (Reactor Netty) para la entrada de alta concurrencia limitada por E/S, y .NET con Clean Architecture y CQRS (MediatR, EF Core y Dapper) para el dominio contable. La integridad ACID la garantiza **PostgreSQL con transacciones explícitas** (ver ADR-0006), no el framework de acceso a datos.

Se reconoce que el rendimiento objetivo también sería alcanzable con una sola plataforma; la decisión asume conscientemente el costo de operar dos.

## 5. Consecuencias

**Positivas**

- Aprendizaje práctico de WebFlux y consolidación de .NET 10 en servicios reales.
- Los contratos (`.proto` y JSON Schema en `/contracts`) pasan a ser la única fuente de verdad entre plataformas, con código generado en ambos lados.
- Las invariantes se verifican con ArchUnit y NetArchTest: demuestra que son reglas de diseño y no de un lenguaje.
- Cada servicio usa herramientas idiomáticas de su ecosistema.

**Negativas / deuda aceptada**

- Hay que mantener cada regla de arquitectura en dos herramientas (ArchUnit y NetArchTest).
- El CI es más pesado: JDK 21 y SDK de .NET, con jobs separados.
- Mayor consumo de memoria: JVM y runtime de .NET junto a cuatro contenedores con límites (≈ 1,8 GB en total) en 16 GB; habrá que fijar límites de memoria (por ejemplo, `-Xmx`) y medir.
- La observabilidad cruza dos plataformas: el `transactionId` debe propagarse por gRPC y por las cabeceras de RabbitMQ (INV-09, ver ADR-0012).
- Curva de aprendizaje inicial mayor, con riesgo de calendario.

**Riesgos y mitigaciones**

- _Divergencia de serialización entre Java y .NET_ (importes `BigDecimal` frente a `decimal`, fechas con zona horaria). **Mitigación:** los importes nunca se tratan como `double`; en los contratos se representan como cadena decimal o en unidades menores (decisión final en ADR-0013); las fechas van en ISO-8601 UTC. El código se genera desde `/contracts` en ambos builds (plugin de Protobuf para Gradle y `Grpc.Tools` en el `.csproj`), los eventos JSON se validan contra su JSON Schema y se agregan pruebas de ida y vuelta Java↔.NET con valores límite. Nota: la compilación solo detecta algunos cambios; para incompatibilidades de contrato más sutiles conviene un chequeo específico de compatibilidad (por ejemplo, `buf breaking` para los `.proto`).
- _Bloqueo del event loop de Netty por el cliente gRPC_ si el Ledger se degrada. **Mitigación:** usar stubs asíncronos o reactivos, o aislar la llamada en un scheduler dedicado (`boundedElastic`); _deadline_ corto en cada llamada; circuit breaker y fallback según lo definido en ADR-0005; BlockHound en los tests de la Ingesta (INV-14).

## 6. Invariantes afectadas

Enlaza a [[Invariantes-Arquitectonicas]]:

- **INV-14** (el event loop de WebFlux nunca se bloquea): este ADR la hace relevante al elegir WebFlux.
- **INV-07** (contratos versionados en `/contracts`): con dos plataformas, el contrato es la única garantía de compatibilidad.
- **INV-06** (dominio sin dependencias de infraestructura): se verifica dos veces, con ArchUnit y con NetArchTest.
- **INV-09** (`transactionId` como correlation id): debe propagarse entre las dos plataformas.
- **INV-16** (cada servicio construible y testeable de forma independiente): implica dos toolchains.
- INV-18 (los importes monetarios usan tipos enteros en unidad mínima y nunca `float` ni `double`; en los contratos se representan como enteros en unidad mínima)

## 7. Cómo se verifica (fitness function)

| Qué                                           | Cómo                                                                                  | Invariante                    |
| --------------------------------------------- | ------------------------------------------------------------------------------------- | ----------------------------- |
| El event loop no se bloquea                   | BlockHound activo en los tests de la Ingesta                                          | INV-14                        |
| Capas sin dependencias prohibidas             | ArchUnit (Java) y NetArchTest (.NET) en CI                                            | INV-06                        |
| Contratos compatibles entre plataformas       | Código generado desde `/contracts` en ambos builds; contract tests contra JSON Schema | INV-07                        |
| Importes y fechas sin pérdida de precisión    | Prueba de ida y vuelta Java → RabbitMQ → .NET con valores límite y fechas UTC         | INV-07, INV-18 (si se adopta) |
| `transactionId` presente en cada evento y log | Test que verifica la cabecera `correlationId` en los eventos publicados               | INV-09                        |
| Cada servicio se construye por separado       | Jobs de CI independientes (JDK 21 y SDK de .NET), obligatorios para fusionar          | INV-16                        |

## 8. Plan de implementación

- [ ]  **D01:** proyecto Spring Boot 3 + WebFlux (Java 21) con Gradle.
- [ ]  **D07:** activar BlockHound en los tests de la Ingesta.
- [ ]  **D10:** reglas ArchUnit y CI de Java.
- [ ]  **D12:** contrato JSON Schema de `transfer.requested`.
- [ ]  **D16:** `.proto` y generación de código en Java y .NET.
- [ ]  **D18:** cliente gRPC sin bloquear el event loop (revisar con BlockHound).
- [ ]  **D21:** solución .NET con NetArchTest y CI extendido a .NET.
- [ ]  **D59:** CI final con todas las verificaciones.
- [ ]  Decidir y registrar la representación de importes (ADR-0004) y, si procede, INV-18.

## 9. Notas de aprendizaje

- Tres modelos de concurrencia que conviene distinguir: `async/await` en .NET, flujos reactivos (Reactor) en WebFlux e hilos virtuales de Java 21. Resuelven problemas parecidos con costos distintos.
- Un stack políglota no se justifica solo por rendimiento: el costo operativo es real y por eso queda registrado en este ADR.
- La integridad transaccional viene de la base de datos, no del framework o del ORM.
- Las ventajas del desacoplamiento (que la Ingesta siga recibiendo si cae el Ledger, escalar un servicio por separado) vienen del estilo de eventos (ADR-0001), no de usar dos lenguajes.

## 10. Referencias

- [[CoreTransact-Mesh_Arquitectura_v2]] (secciones 3 y 5)
- [[ADR-0001-estilo-arquitectonico]]
- ADR-0004 (contratos y topología) y ADR-0005 (gRPC con fallback), por crear
- [[Invariantes-Arquitectonicas]]
- [[ADR-Indice]]
- Documentación oficial: Spring WebFlux y Project Reactor, JEP 444 (hilos virtuales), gRPC (deadlines), Protocol Buffers
