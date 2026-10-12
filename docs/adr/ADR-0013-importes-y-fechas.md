---
id: ADR-0013
titulo: Representación de importes y fechas
estado: Propuesto
fecha: 2026-10-10
decisores:
  - David
reemplaza: ""
reemplazado_por: ""
servicios:
  - ingesta
  - ledger
  - conciliacion
invariantes:
  - INV-07
  - INV-18
  - INV-19
tags:
  - adr
  - contratos
  - datos
---

# ADR-0013: Representación de importes y fechas

## 1. Contexto

Los importes y las fechas viajan entre Java (Ingesta), .NET (Ledger y Conciliación), RabbitMQ (JSON), gRPC, PostgreSQL y MongoDB. Cada plataforma los trata a su manera (`BigDecimal` frente a `decimal`, `double` al leer números JSON, fechas con o sin zona horaria). Sin una regla común, un importe puede perder precisión o una fecha puede cambiar de día al cruzar de una plataforma a otra, en un sistema donde el saldo debe ser exacto.

El alcance es de **transferencias entre cuentas** (sumas y restas), en **soles (PEN) y dólares (USD)**.

## 2. Fuerzas (criterios de decisión)

- **Exactitud:** ningún error de punto flotante ni redondeo.
- **Neutralidad entre lenguajes:** el mismo valor en Java, .NET, JSON y SQL.
- **Simplicidad:** la menor cantidad posible de conversiones.
- **Compatibilidad con JSON:** muchos parseadores leen los números JSON como `double`.
- **Alcance real del proyecto:** solo transferencias, sin intereses ni tipos de cambio.

## 3. Opciones consideradas

### 3.1 Importes

| Opción | Pros | Contras |
|---|---|---|
| A. `double` / `float` | Trivial | Error de punto flotante: **inaceptable** en un sistema financiero |
| B. Decimal exacto (`BigDecimal`/`decimal`, `NUMERIC`), como cadena en JSON | Admite fracciones y varias escalas | Dos tipos distintos entre plataformas; riesgo de que alguien lo parsee como número; conversiones |
| C. **Entero en la unidad mínima** (céntimos) | Exacto; mismo tipo (`long`/`BIGINT`) en todo el sistema; sin parseo ambiguo; aritmética simple | No admite fracciones; cada moneda tiene su exponente; hay que convertir al mostrar |

### 3.2 Fechas y horas

| Opción | Pros | Contras |
|---|---|---|
| A. Hora local sin zona | Aparentemente cómoda | Ambigua; cambia de día según la zona; frágil entre plataformas |
| B. **Instante en UTC (ISO-8601 con `Z`)**, más una fecha de negocio aparte cuando haga falta | Inequívoco; estándar en las tres plataformas; conversión solo al mostrar | Hay que distinguir instante de fecha de negocio |

## 4. Decisión

**Importes: opción C.**

- Todo importe es un **entero en la unidad mínima de la moneda**, con el nombre `amountMinor` (sufijo `Minor` para no mezclar unidades) y la moneda en `currency` (ISO 4217).
- Tipo en cada capa: `long` (Java y .NET), `BIGINT` (PostgreSQL), `Int64` (MongoDB) y `integer` en JSON Schema.
- Monedas admitidas en el alcance: **PEN y USD** (exponente 2). Añadir otra exige revisar el exponente según ISO 4217.
- La conversión a decimal (dividir entre 10^exponente) ocurre **solo en la capa de presentación**, nunca en el dominio.
- Queda prohibido `float`/`double` para dinero.

**Fechas: opción B.**

- Todo **instante** se almacena y transporta en **UTC**, en ISO-8601 con `Z` (por ejemplo `2026-10-05T19:25:00Z`).
- Tipos: `Instant` (Java), `DateTimeOffset` en UTC (.NET), `timestamptz` (PostgreSQL) y `Date` (MongoDB).
- La conversión a la zona del usuario ocurre **solo al mostrar**, en el cliente.
- La **fecha contable** (día de cierre) es un dato aparte (`LocalDate`) y se calcula con una **zona de negocio de referencia explícita: `America/Lima`**. La Conciliación la usa para el cierre diario.

## 5. Consecuencias

**Positivas**

- Un único tipo entero para dinero en todo el sistema; desaparece el riesgo de `BigDecimal` frente a `decimal`.
- Sin ambigüedad horaria entre plataformas.
- Pruebas de ida y vuelta sencillas y deterministas.

**Negativas / deuda aceptada**

- No hay fracciones de unidad mínima: si más adelante hicieran falta intereses, comisiones o tipo de cambio, habría que ampliar la escala o introducir decimales (nuevo ADR).
- Cada moneda tiene su exponente: se limita el alcance a PEN y USD.
- Los cambios ya documentados deben actualizarse: en el contrato de eventos `amount` pasa a `amountMinor` (entero); en PostgreSQL, `Saldo` y `Monto` pasan de `NUMERIC(18,4)` a `BIGINT`; los JSON Schema se escriben con `integer`.

**Riesgos y mitigaciones**

- *Confundir unidades (soles frente a céntimos).* Mitigación: sufijo `Minor` en los nombres, pruebas con valores conocidos y tipos propios en el dominio (por ejemplo, un objeto de valor `Money`).
- *Clientes JavaScript con enteros mayores de 2^53.* Mitigación: fuera del alcance (equivale a billones de soles); documentarlo si hubiera un cliente web.
- *Cierre contable en el día equivocado por usar UTC.* Mitigación: la fecha contable se calcula con `America/Lima` y se prueba con transferencias cercanas a la medianoche.

## 6. Invariantes afectadas

- **INV-07** (contratos versionados): el contrato fija `amountMinor` como entero y los instantes en UTC.
- **INV-18 (nueva):** *Los importes monetarios se representan como enteros en la unidad mínima (`amountMinor`); nunca `float`, `double` ni decimales en contratos o dominio.*
- **INV-19 (nueva):** *Todo instante se almacena y transporta en UTC (ISO-8601 con `Z`); la conversión a zona horaria ocurre solo en la capa de presentación.*

## 7. Cómo se verifica (fitness function)

| Qué | Cómo | Invariante |
|---|---|---|
| El contrato usa enteros | JSON Schema con `amountMinor: integer` (mínimo 1); validación al publicar y al consumir | INV-07, INV-18 |
| Ningún `float`/`double` para dinero en el dominio | Regla de arquitectura o prueba que falle si el dominio declara esos tipos para importes | INV-18 |
| Ida y vuelta sin pérdida | Prueba Java → RabbitMQ → .NET → PostgreSQL con valores límite (por ejemplo 1, 150050 y un valor muy grande) | INV-18 |
| Instantes en UTC | Prueba de ida y vuelta de un instante y revisión de los tipos usados en cada capa | INV-19 |
| Cierre contable correcto | Prueba con una transferencia a las 23:30 hora de Lima, que en UTC cae al día siguiente | INV-19 |

## 8. Plan de implementación

- [ ] Antes de **D12:** aceptar este ADR (el JSON Schema de `transfer.requested` ya debe usar `amountMinor`).
- [ ] **D12:** contratos con `amountMinor` entero y fechas UTC.
- [ ] **D23:** esquema de PostgreSQL con `BIGINT` y `timestamptz`.
- [ ] **D36-D37:** conciliación con enteros y fecha contable en `America/Lima`.
- [ ] Actualizar [[CoreTransact-Mesh_Arquitectura_v2]] (contratos y esquemas) y el catálogo de invariantes.

## 9. Notas de aprendizaje

- Instante frente a fecha de negocio: "cuándo ocurrió" es un instante (UTC); "a qué día contable pertenece" depende de una zona de referencia.
- Convertir fechas a una zona horaria es barato; lo importante es hacerlo en el cliente y no mezclar tipos con y sin zona.
- Guardar dinero como entero en la unidad mínima es habitual en APIs de pagos; muchos core bancarios usan decimales de punto fijo en la base de datos. Ambas son válidas; lo incorrecto es `float`/`double`.

## 10. Referencias

- [[CoreTransact-Mesh_Arquitectura_v2]] (sección 5)
- [[ADR-0002-stack-poliglota]] (riesgo de divergencia de serialización)
- [[Invariantes-Arquitectonicas]] · [[ADR-Indice]]
- ISO 4217 (monedas y exponentes), ISO 8601 (fechas)
