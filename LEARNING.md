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

### 2026-10-10 · D001 · SCRUM-25 · Proyecto WebFlux hexagonal y endpoint de salud
- **Tiempo real:** Sesión 1: 8h  (estimado: 3h) Paso 1, 3, 4: 10min - Paso 2. Aprendizaje: 6h - Laboratorio de Reactor 1h 50min | Sesión 2: 55 min (estimado: 1h 35min) Paso 6. Esqueleto: 10min - Paso 7. actuator/health y primer test con WebTestClient: 25min - Paso 8. Commit y pull request: 10min - Cierre: 10min | Total: 8h 55min (estimado: 4h 35min)
- **Qué aprendí:**
  - Aprendí acerca de la programación reactiva, donde los flujos reactivos se basan en el Patrón de diseño Publisher-Subscriber donde hay un publicador de eventos y uno o más suscriptores a este publicador. También aprendí que las operaciones aplicadas a los valores enviados se expresan de forma declarativa no imperativa, es decir, uno programa la lógica del cálculo a través de operadores en lugar de describir su flujo exacto. Un flujo reactivo no se ejecturará a menos que me suscriba, pueden haber distintos operadores que alteren los datos, pero si no me suscribo no pasará nada, una analogía a esto es que los operadores funcionan como una tubería de agua, es decir, los operadores le dan forma a los tubos por dónde pasará el agua pero si la llave está cerrada el agua nunca pasará, la llave que abre el agua sería la suscripción. Además aprendí que existen 2 grandes características de la programación reactiva, `Mono` y `Flux`. En `Mono` pasan cero o 1 elemento únicamente por la tubería, como por ejemplo cuando se hace una llamada HHTP a un método para obtener el ID de un usuario, mientras que en `Flux` pasan cero o muchos elementos o colecciones como listas, arreglos, etc.
  - También existen operaciones que "aplanan" colecciones o peticiones como `Map` y `FlatMap` donde el primero transforma cada elemento de forma síncrona (por ejemplo, cambiar un String a mayúsculas) mientras que `FlatMap` transforma cada elemento de forma asíncrona devolviendo `Flux` o `Mono` "aplanando" el resultado en un único flujo final. Es decir el `Map` modifica cada elemento uno a uno, mientras que `FlatMap` transforma de forma asíncrona (1-a-N)desordenadamente. Por eso, se usa `Map` cuando hay que transformar cosas pero mantener la estructura intacta y se usa `FlatMap` cuando lo que esté dentro sean más listas o grupos y quiera romper las estructuras para juntar todo en una sola lista gigante.
  - _Backpressure_ o Contrapresión es la capacidad que tiene la programación reactiva para proteger a los suscriptores del exceso de información que le llega. Este mecanismo de control de ritmo ofrece a los suscriptores la herramienta para solicitar "n" datos por cada lote que envía el publicador. Así se protege de la sobreinformación que puede perjudicar a la lectura por parte de los suscriptores.
  - Finalmente porqué no llamamos a `.block()` en código en producción en WebFlux. Esto destruye el propósito y el beneficio de usar programación reactiva, usar el bloqueo puede llegar incluso a congelar el servidor con pocos usuarios. WebFlux utiliza servidores como Netty el cual no crea cientos de hilos, por lo general crea la misma cantidad de hilos por núcleo de procesador, por ejemplo mi procesador tiene 6 núcleos de CPU, entonces Netty solo tendrá 6 hilos para atender a miles de usuarios. Estos hilos son llamados hilos de Event Loop y nunca deben detenerse, su única función es recibir la petición, delegarla asíncronamente y pasar a la siguiente petición. Si se coloca `.block()` dentro del código para esperar de manera sincrónica un `Mono` o `Flux` estoy obligando a uno de esos 6 hilos a quedarse esperando o congelado. Si 6 usuarios ejecutan al mismo tiempo un endpoint que contiene un `.block()` habré congelado el 100% de los hilos del servidor; a partir de ese momento un siguiente usuario que intente entrar a la aplicación se quedará esperando infinitamente dando la sensación de que el sistema está caído. Pero el uso de `.block()` está permitido únicamente en 2 escenarios fuera del flujo web de producción: En pruebas unitarias y en scripts de consola (o tareas batch para aplicaciones de terminal que se ejecutan procesando algo de manera secuencial y se cierran inmediatamente) INV-14
- **Explícalo en 3 frases:** La programación reactiva es importante en sistemas donde se necesite una comunicación asíncrona, con alta concurrencia y que no sea bloqueante. Se basa en características como `Mono` o `Flux` para manejar los datos y utilizan operadores para transformar la salida.
- **Decisión tomada / ADR:**
- **Lo que me bloqueó o me sorprendió:** Me bloqueó un poco aprender el concepto de flujos reactivos y cómo es que se comportan con respecto a los flujos tradicionales
- **Primer paso de mañana:**

### 2026-10-05 · D00B · SCRUM-24 · Docker compose corregido y verificación de infraestructura · Construcción, redacción de ADR-0002 y cierre
- **Tiempo real:** Construcción: 30min  (estimado: 1h 45min) | Redacción ADR-0002: 2h (estimado: 55min) | Total: 2h 30min
- **Qué construí:**
  - Construí el docker-compose.yml para 4 contenedores de infra: redis, rabbitmq, postgres y mongodb. Levanté los contenedores y comprobé que cada motor responde.
- **Qué aprendí:**
  - Aprendí los comandos básicos para levantar y apagar los contenedores. Un detalle importante es que hay que entender bien la diferencia entre `down` y `down -v`:
    ```powershell
    docker compose -f infra/docker-compose.yml down        # apaga y conserva los datos (volúmenes)
    docker compose -f infra/docker-compose.yml down -v     # apaga y BORRA los datos
    ```
  - Aprendí a escribir mejor un ADR pasándole a un modelo de IA (Claude) para que lo revise y me genere feedback, esto me llevó mucho más tiempo del previsto
- **Explícalo en 3 frases:** Un healthcheck es muy importante al momento de levantar contenedores ya que nos permite comprobar que hay una conexión entre la petición y la respuesta. Además terminé eligiendo un stack políglota (Java 21 + WebFlux para Ingesta y .NET para Ledger y Conciliación) a un stack de solo Java o solo .NET como valor formativo y de portafolio y además porque cada plataforma logra encajar con el servicio asignado (WebFlux para la entrada de alta concurrencia limitada por E/S y .NET con arquitectura limpia y CQRS para el dominio contable). Aprendí que para reemplazar un ADR aceptado como el ADR-0002 debo crear un nuevo ADR y no editar el ADR aceptado por eso es importante poner mucha atención cuando se va a aceptar un ADR y si llega el momento de necesitar cambiar algo o reemplazar un ADR aceptado se debería crear uno con el estado de "Reemplaza a ADR-XX".
- **Decisión tomada / ADR:** ADR-0002 Aceptada.
- **Lo que me bloqueó o me sorprendió:** Me sorprendió la manera estructurada de organizar un proyecto grande; son muchos pasos, sin embargo, me puedo acostumbrar fácilmente a esta metodología ágil.
- **Primer paso de mañana:** fusionar el PR de D00B, cerrar SCRUM-24 y empezar D01 (WebFlux hexagonal); verificar JDK 21.


### 2026-10-04 · D00B · SCRUM-24 · Docker compose corregido y verificación de infraestructura · Aprendizaje
- **Tiempo real:** 4h  (estimado: 30 min)
- **Qué aprendí:**
  - Entendí cómo se estructura un ADR por dentro, me di cuenta que habían términos nuevos para mí y me puse a investigarlos y los anoté para recordarlos. Después de leer este ADR-0001 lo terminé aceptando sin modificaciones.
- **Explícalo en 3 frases:** Un ADR es un Registro de una desición arquitectónica, este registro guarda muchos parámetros importantes que deben cumplirse una vez aprobado el ADR, si un ADR que ya ha sido aprobado requiere cambios no se debe editar, se debe crear un nuevo ADR que lo reemplace. Esto con la finalidad de evitar erosionar la arquitectura del sistema. Un ADR se diferencia de una Invariante en que el primero solo es un registro que puede llegar a cambiar con el tiempo mientras que una Invariante es una regla que debe cumplirse.
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
| S0 | D00A-D00B | 7,6 | 5 | +52 % | Aprender conceptos nuevos (ADR, C4, AMQP, CI) tomó mucho más de lo estimado; la construcción fue más rápida |
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
| 2026-10-01 | ¿Cómo automatizo INV-01 con ArchUnit? | Cerrada | Se aborda en D10 (reglas ArchUnit) |

---

## Glosario personal

Términos que fui aprendiendo, con mis palabras.

| Término | Mi definición | Día en que lo vi |
|---|---|---|
| Idempotencia | Es la propiedad de una operación que permite aplicarla varias veces sin cambiar el resultado obtenido después de la primera operación, por ejemplo, cuando consulto mi saldo, esta operación tiene que ser idempotente, ya que sin importar cuántas veces se ejecute esta operación el resultado no debería cambiar | 2026-10-04 |
| Diagrama C4 |Es un conjunto de diagramas de arquitectura de software que sirve para visualizar y documentar cómo está estructurado el sistema y cómo interactúan sus partes. Tiene 4 niveles: Contexto, Contenedores, Componentes y Código | 2026-10-04 |
| RPC/gRPC |Es un framework de Google de código abierto que sirve para llamadas a procedimientos remotos (RPC) y que permite una comunicación eficiente entre servicios distribuidos usando HTTP/2 y Protocol Buffers en código binario. A diferencia de una API Rest donde se usa el formato JSON y HTTP/1 gRCP utiliza un formato binario junto con un .proto que define el contrato de estas llamadas | 2026-10-04 |
| Message Broker | Es un intermediario de mensajería (middleware) que toma mensajes de productores, los enruta mediante colas y los entrega a consumidores resuleve el problema de Ingesta y Ledger, si uno de estos servicios cae, el que envía un mensaje no lo hace ya que el middleware se ha encargado de colocar el mensaje en una cola, ahí tendrá que esperar a que el consumidor responda cuando levante o en todo caso devolver una respuesta negativa, pero sin afectar al otro servicio que está bien | 2026-10-04 |
| AMQP |Protocolo de cola de mensajería avanzada. Este es el protocolo que utiliza RabbitMQ quien conecta los tres servicios: Ingesta, Ledger y Conciliación | 2026-10-04 |
| Integración Continua (CI) | Es lo que hace cumplir mis invariantes sin depender de mi memoria. En cada Push que hago a una rama remota un servidor lo compila y ejecuta automáticamente las pruebas y verificaciones, y me avisa si algo falla. Por ejemplo, en mi proyecto GitHub Actions compilaría Java con JDK 21 y .NET con su SDK y ejecutaría tests, ArchUnit y NetArchTest. Si algo falla, el PR no se fusiona | 2026-10-04 |
| ArchUnit | Verifica reglas como por ejemplo, que Ingesta nunca acceda a PostgreSQL ni a MongoDB. Lo que hace esta prueba unitaria es hacer cumplor que el servicio de Ingesta no tenga dependencias a drivers de estos contenedores  | 2026-10-04 |
| Backpressure | asd | 2026-10-10 |
| Publisher/Subscriber | asd | 2026-10-10 |
| Event Loop |  | 2026-10-10 |


---

## Banco de historias para entrevistas

Cuando algo salga especialmente bien (o mal), anótalo en formato STAR. Servirá para el CV y para entrevistas de arquitectura.

### Seleccionar Framework para Ingesta
- **Situación:** Elegir un Framework WebFlux o Spring MVC con hilos virtuales
- **Tarea:** ¿Por qué WebFlux y no Spring MVC con hilos virtuales?
- **Acción:** WebFlux ofrece flujos con backpressure y composición asíncrona, esto incrementa la complejidad pero elegí WebFlux por razones formativas.
- **Resultado:**
- **Qué aprendí / qué haría distinto:**


Formato:
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
