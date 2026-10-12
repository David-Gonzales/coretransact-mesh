---
id: ADR-0014
titulo: Versión de Spring Boot, JDK y herramienta de build de la Ingesta
estado: Aceptado
fecha: 2026-10-10
decisores:
  - David
reemplaza: ""
reemplazado_por: ""
refina: ADR-0002
servicios:
  - ingesta
invariantes:
  - INV-14
  - INV-15
  - INV-16
tags:
  - adr
  - versiones
  - build
---

# ADR-0014: Versión de Spring Boot, JDK y herramienta de build de la Ingesta

> [!note] Relación con el ADR-0002
> Este ADR **no reemplaza** al ADR-0002: la decisión central (stack políglota, opción C) sigue en pie. Solo **refina un detalle**: el ADR-0002 mencionaba "Spring Boot 3", una versión que ya no tiene soporte de código abierto. El ADR-0002 no se edita.

## 1. Contexto

El ADR-0002 fijó Java 21 con Spring WebFlux para la Ingesta e indicó Spring Boot 3. Al preparar el proyecto (octubre de 2026) se comprobó que:

- Spring Boot 3.5, la última serie 3.x, recibió su último release de código abierto el 25 de junio de 2026 y su soporte OSS terminó el 30 de junio de 2026.
- Spring Boot 4.0 termina su soporte OSS el 31 de diciembre de 2026.
- Spring Boot 4.1 es la serie estable vigente, con soporte OSS hasta el 31 de julio de 2027. Requiere Java 17 como mínimo, es compatible con Java 21 y 25, y pide Gradle 8.14 o superior (o 9.x).

El equipo de desarrollo tiene instalado OpenJDK 25 (LTS) y debe decidir también qué JDK usar, dado que el ADR-0002 indicaba Java 21.

## 2. Fuerzas (criterios de decisión)

- **Soporte y parches de seguridad** durante todo el desarrollo y después.
- **Compatibilidad de herramientas** del proyecto: BlockHound (INV-14), ArchUnit, Testcontainers, plugin de Protobuf.
- **Riesgo de aprendizaje:** el proyecto ya introduce varias tecnologías nuevas a la vez.
- **Material de aprendizaje disponible** (muchos tutoriales son de Spring Boot 3).
- **Reproducibilidad:** que cualquiera construya el proyecto con la misma versión de JDK y de Gradle.****

## 3. Opciones consideradas

### 3.1 Versión de Spring Boot

| Opción | Pros | Contras |
|---|---|---|
| A. Spring Boot 3.5.x | Más tutoriales; mayor familiaridad | Sin soporte OSS: empezar un proyecto nuevo ahí genera deuda desde el día uno |
| B. Spring Boot 4.0.x | Ya es la generación actual | Su soporte termina el 31 de diciembre de 2026, antes de acabar el proyecto |
| C. **Spring Boot 4.1.x** | Serie estable vigente; soporte hasta julio de 2027; compatible con Java 21 y 25 | Boot 4 reorganizó módulos y paquetes: parte del material de aprendizaje no coincide y hay que consultar la documentación 4.1 |

### 3.2 JDK

| Opción | Pros | Contras |
|---|---|---|
| A. **Java 21 (LTS)** | Coherente con el ADR-0002; amplia compatibilidad de herramientas; hilos virtuales disponibles | No es la última LTS |
| B. Java 25 (LTS) | Última LTS; incluye JEP 491 (los hilos virtuales ya no se fijan en `synchronized`); ya instalado | Compatibilidad de BlockHound y ArchUnit **sin verificar**; para un servicio WebFlux el beneficio de JEP 491 es pequeño (aplica a hilos virtuales, no al event loop de Netty) |

### 3.3 Herramienta de build

| Opción | Pros | Contras |
|---|---|---|
| A. **Gradle (con wrapper)** | Elegida por el autor; el plugin de Protobuf para Gradle encaja con la generación de código desde `/contracts`; el wrapper fija la versión | Algo más de curva de aprendizaje que Maven |
| B. Maven | Muy extendido; configuración declarativa | Menos cercano a la preferencia del autor |

## 4. Decisión

- **Spring Boot 4.1.x** (la última estable de la serie al crear el proyecto).
- **Java 21** fijado mediante *toolchain* de Gradle (`languageVersion = JavaLanguageVersion.of(21)`), de modo que el sistema pueda tener otros JDK instalados sin afectar al proyecto.
- **Gradle con wrapper** (`gradlew`), con una versión compatible con Spring Boot 4.1.
- El proyecto se genera con **Spring Initializr**.

La migración a **Java 25 LTS** se considera una mejora futura y opcional: si se hace, se registrará en un ADR nuevo que reemplace la parte de JDK de este ADR.

## 5. Consecuencias

**Positivas**

- Soporte OSS durante todo el proyecto y después.
- Mantiene la coherencia con el ADR-0002 y con las herramientas de verificación planificadas.
- El toolchain y el wrapper hacen el build reproducible.
- Una futura migración a Java 25 servirá para demostrar el valor de las pruebas de arquitectura automatizadas.

**Negativas / deuda aceptada**

- No se usa la última LTS de Java desde el inicio.
- Cambios de paquetes y módulos en Spring Boot 4: hay que consultar la documentación de la versión 4.1 y desconfiar de ejemplos de Boot 3.
- Dos JDK instalados en el equipo de desarrollo (25 y 21).

**Riesgos y mitigaciones**

- *Una dependencia del proyecto aún no soporta Spring Boot 4.1.* Mitigación: verificar compatibilidad al incorporar cada dependencia nueva; si hay problemas, documentarlo.
- *BlockHound se carga como agente dinámico.* En JDK 21 solo produce una advertencia (JEP 451), pero una versión futura lo prohibirá por defecto. Mitigación: comprobarlo en D07 y, si fuera necesario, pasar la configuración de JVM correspondiente.

## 6. Invariantes afectadas

Enlaza a [[Invariantes-Arquitectonicas]]:

- **INV-15** (ningún cambio de tecnología sin ADR): este ADR es su aplicación.
- **INV-14** (el event loop no se bloquea): condiciona la compatibilidad de BlockHound.
- **INV-16** (cada servicio construible y testeable de forma independiente): el toolchain y el wrapper la refuerzan.

## 7. Cómo se verifica (fitness function)

- El `build.gradle` declara el toolchain de Java 21: la compilación falla si no hay un JDK 21 disponible.
- El wrapper de Gradle fija la versión de Gradle en el repositorio.
- El CI (D10) usa JDK 21 en su job de Java.
- Revisión del calendario de soporte de Spring Boot al cerrar cada sprint.

## 8. Plan de implementación

- [ ] **D01:** instalar JDK 21 y generar el proyecto en Spring Initializr con Boot 4.1.x, Java 21 y Gradle.
- [ ] Actualizar [[CoreTransact-Mesh_Arquitectura_v2]] (sección 3, componente 1: "Spring Boot 3.x" → "Spring Boot 4.1.x").
- [ ] **D07:** comprobar BlockHound con la configuración elegida.
- [ ] *(Opcional, S5-S6)* historia "Migrar a JDK 25 LTS", con su propio ADR.

## 9. Notas de aprendizaje

- Los hilos virtuales llegaron en Java 21; JEP 491 (JDK 24) eliminó el *pinning* por `synchronized`, y solo quedan casos como frames nativos o inicialización de clases.
- **Hilos virtuales y WebFlux son modelos distintos.** En WebFlux el código corre en el event loop de Netty (hilos de plataforma): bloquearlo frena a todas las peticiones con cualquier JDK. Por eso INV-14 y BlockHound siguen vigentes.
- Alternativa que conviene saber explicar: Spring MVC con hilos virtuales (código imperativo y simple) frente a WebFlux (backpressure y composición asíncrona, con más complejidad). Se eligió WebFlux por valor formativo.
- JDK Flight Recorder (`jdk.VirtualThreadPinned`) sirve para detectar pinning en aplicaciones con hilos virtuales; no sustituye a BlockHound en un servicio WebFlux.

## 10. Referencias

- [[ADR-0002-stack-poliglota]] (refinado por este ADR)
- [[CoreTransact-Mesh_Arquitectura_v2]]
- [[Invariantes-Arquitectonicas]] · [[ADR-Indice]]
- Calendario de soporte de Spring Boot; anuncio de Spring Boot 3.5.16; requisitos del sistema de Spring Boot 4.1
- JEP 444 (hilos virtuales), JEP 451 (carga dinámica de agentes), JEP 491 (sincronización sin pinning)
