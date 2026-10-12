---
Id: ADR-0000
Titulo: Plantilla de ADR (copiar y renombrar a ADR-XXXX-titulo-corto.md)
Estado: Propuesto
Fecha: AAAA-MM-DD
Decisores: [David]
Tags: [adr]


# ADR-XXXX: Título corto de la decisión

> [!tip] Cómo usar esta plantilla
>
> - Una decisión por ADR. Escríbelo **antes** de implementar (estado `Propuesto`) y pásalo a `Aceptado` al decidir.
> - Un ADR aceptado **no se edita** para cambiar la decisión: se crea uno nuevo que lo reemplaza y se actualiza el estado del anterior.
> - Máximo una página. Si necesitas más, enlaza a notas de apoyo.

## 1. Contexto

¿Qué problema o necesidad existe? ¿Qué restricciones hay (hardware, tiempo, stack, aprendizaje)? ¿Qué pasa si no decidimos nada?

## 2. Fuerzas (criterios de decisión)

Lo que pesa en la decisión, por ejemplo:

- Consistencia / disponibilidad / rendimiento
- Complejidad y curva de aprendizaje
- Operabilidad y testabilidad
- Valor formativo para el proyecto

## 3. Opciones consideradas

|Opción|Pros|Contras|
|---|---|---|
|A. …|…|…|
|B. …|…|…|
|C. …|…|…|

## 4. Decisión

Elegimos **la opción X** porque …

## 5. Consecuencias

**Positivas**

- …

**Negativas / deuda aceptada**

- …

**Riesgos y mitigaciones**

- …

## 6. Invariantes afectadas

Enlazar a [[Invariantes-Arquitectonicas]]: ¿se crea, modifica o reemplaza alguna `INV-XX`?

## 7. Cómo se verifica (fitness function)

¿Qué prueba o regla automática (ArchUnit, NetArchTest, test de integración, contract test, carga) garantiza que esta decisión se respeta?

## 8. Plan de implementación

- [ ] Tarea de Jira / sprint donde se implementa
- [ ] Cambios en contratos (`/contracts`)
- [ ] Cambios en diagramas C4 / arc42

## 9. Notas de aprendizaje (opcional)

Qué aprendí al investigar o probar esta decisión; enlaces útiles.

## 10. Referencias

- Documentación oficial, artículos, ADRs relacionados ([[ADR-Indice]]).
