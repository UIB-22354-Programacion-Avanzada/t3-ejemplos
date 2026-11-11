# t3-ejemplos — Código del Tema 3: Programación Concurrente

Todos los listados de código del
[Tema 3](https://uib-22354-programacion-avanzada.github.io/website/es/contenido/tema3.html) de
*Programación Avanzada (22354)*, Grado en Ingeniería Telemática, UIB-EPS, reunidos en un proyecto
que compila y se ejecuta.

Es un proyecto **Maven** para **Java 25** con pruebas en **JUnit 5**, preparado para **GitHub
Codespaces**. Aquí **no hay nada que resolver**: el código está completo y la gracia es ejecutarlo,
modificarlo y ver qué cambia. En este tema eso es más cierto que nunca, porque **casi ningún
programa da dos veces la misma salida**: ejecútalos varias veces.

## Cómo ejecutarlo

**En el navegador, con Codespaces.** **Code** → **Codespaces** → **Create codespace on main**. Se
abre VS Code con el JDK 25 y Maven ya puestos.

**En tu ordenador.** Clona el repositorio y abre el proyecto por su `pom.xml`: en IntelliJ,
**File → Open** y selecciona el `pom.xml` (elige *Open as Project*); en VS Code, abre la carpeta y
deja que la extensión de Java la importe. Necesitas un **JDK 25**.

```bash
mvn -q compile                                   # compilar
mvn test                                         # las pruebas
mvn -q compile exec:java -Dexec.mainClass=es.uib.prgava.tema3.fundamentos.DemoCarrera
```

Todos los programas con `main` también se lanzan con el botón de ejecutar del IDE, que es más
cómodo.

### La opción `--enable-preview`

Tres clases —`RondaEstructurada`, `DemoRondaEstructurada` y `DemoValorConAmbito`— usan
`StructuredTaskScope`, que en Java 25 es todavía una API **en vista previa** (sección 3.3.4 del
tema). El proyecto ya está configurado para ello, en dos sitios:

- en el `pom.xml`, para el compilador y para las pruebas;
- en `.mvn/jvm.config`, para que `mvn exec:java` pueda ejecutar las clases compiladas así.

IntelliJ y VS Code leen la opción del `pom.xml` al importar el proyecto. Si al ejecutar una clase
desde el IDE ves un error que habla de *preview features*, comprueba que el nivel de lenguaje del
proyecto sea **25 (Preview)** en IntelliJ, o vuelve a importar el proyecto en VS Code. Y recuerda
que lo compilado con esta opción en Java 25 solo se ejecuta en Java 25.

## Qué hay aquí

```text
t3-ejemplos/
├── pom.xml
├── .mvn/jvm.config               ← --enable-preview para exec:java
└── src/
    ├── main/java/es/uib/prgava/
    │   ├── tema1/monitor/        ← diecisiete clases del Tema 1, copiadas (ver más abajo)
    │   ├── tema2/monitor/        ← dos clases del Tema 2, copiadas
    │   └── tema3/
    │       ├── fundamentos/      ← sección 3.1
    │       ├── herramientas/     ← sección 3.2
    │       ├── virtuales/        ← sección 3.3
    │       ├── reactiva/         ← sección 3.4
    │       └── monitor/          ← el monitor de servicios, en paralelo
    └── test/java/es/uib/prgava/tema3/monitor/
```

### `tema3.fundamentos` — sección 3.1

| Clase | Qué es | Sección |
|---|---|---|
| `DemoFormasDeCrearHilos` | cuatro formas de crear un hilo; el orden de los saludos cambia en cada ejecución | 3.1.2 |
| `DemoHilos` | dos hilos a la vez, `start` y `join` | 3.1.2 |
| `DemoCarrera` | cuatro hilos y un `contador++` sin proteger: se pierden incrementos | 3.1.4 |
| `Contador` | el contador con sus dos métodos `synchronized` | 3.1.5 |
| `DemoExclusion` | el mismo experimento con `Contador`: cuatro millones exactos | 3.1.5 |
| `DemoVisibilidad` | un hilo que no se entera de que otro le ha pedido parar; con `volatile` sí se entera | 3.1.5 |
| `DemoInterbloqueo` | dos hilos y dos cerrojos en orden contrario. **No termina**: páralo con Ctrl+C | 3.1.6 |

### `tema3.herramientas` — sección 3.2

| Clase | Qué es | Sección |
|---|---|---|
| `DemoEjecutar` | seis tareas `Runnable` entregadas con `execute` a tres hilos; el orden de la salida cambia | 3.2.1 |
| `DemoEjecutor` | las mismas seis tareas como `Callable`, con `submit` y `Future`: la salida sale en orden | 3.2.1 |
| `DemoFuturoCompletable` | dos consultas a la vez combinadas con `CompletableFuture`, y una que sigue sola | 3.2.2 |
| `DemoContarPalabras` | contar palabras con `get` y `put` pierde cuentas; con `merge`, no | 3.2.3 |
| `DemoContadores` | sin proteger, con cerrojo y con `AtomicLong`: correcto o no, y cuánto tarda | 3.2.4 |
| `DemoPasaplatos` | un cocinero, dos camareros y un pasaplatos con `ArrayBlockingQueue`: `put` y `take` esperan | 3.2.5 |
| `DemoTryLock` | el interbloqueo de 3.1.6, deshecho con `tryLock` y un plazo | 3.2.6 |

### `tema3.virtuales` — sección 3.3

| Clase | Qué es | Sección |
|---|---|---|
| `DemoMilHilos` | diez mil esperas con doscientos hilos de plataforma y con hilos virtuales | 3.3.2 |
| `DemoSemaforo` | diez descargas, sin límite y con un semáforo de tres permisos | 3.3.3 |
| `DemoValorConAmbito` | un `ScopedValue` que llega a las subtareas de un ámbito y no a otros hilos *(vista previa)* | 3.3.5 |

### `tema3.reactiva` — sección 3.4

| Clase | Qué es | Sección |
|---|---|---|
| `SuscriptorLento` | un suscriptor que pide los elementos de uno en uno | 3.4.2 |
| `DemoContrapresion` | `submit` espera, `offer` descarta: las dos estrategias de contrapresión | 3.4.2 |

### `tema3.monitor` — el ejemplo unificador

| Clase | Qué es | Sección |
|---|---|---|
| `SondaLenta` | una sonda que tarda lo que tardaría la red | 3.1.3 |
| `Escenario` | los datos de todos los experimentos: latencias fijas, uno de cada diez caído | 3.1.3 |
| `RondaConHilos` | la ronda con un hilo por servicio | 3.1.3 |
| `DemoRondas` | ronda secuencial frente a ronda con hilos | 3.1.3 |
| `DemoRepositorioCompartido` | prueba de esfuerzo: el repositorio del Tema 2 pierde resultados y revienta | 3.1.3 |
| `RepositorioSincronizado` | decorador que protege cualquier repositorio con `synchronized` | 3.1.5 |
| `RondaConEjecutor` | la ronda sobre un ejecutor, con `invokeAll` y un plazo | 3.2.1 |
| `DemoRondaConEjecutor` | la ronda con ejecutores de distinto tamaño | 3.2.1 |
| `RepositorioConcurrente` | el índice por servicio con `ConcurrentHashMap` y `ConcurrentLinkedQueue` | 3.2.3 |
| `NotificadorAsincrono` | productor-consumidor: las alertas se avisan desde otro hilo | 3.2.5 |
| `DemoNotificadorAsincrono` | un notificador lento, directo y a través de la cola | 3.2.5 |
| `DemoMilServicios` | mil servicios con hilos de plataforma, con hilos virtuales y con un límite | 3.3.2 |
| `SondaLimitada` | decorador que limita los sondeos simultáneos con un semáforo | 3.3.3 |
| `RondaEstructurada` | la ronda como ámbito de concurrencia estructurada *(vista previa)* | 3.3.4 |
| `DemoRondaEstructurada` | la ronda estructurada con un plazo holgado y con uno corto *(vista previa)* | 3.3.4 |
| `RepositorioPublicador` | decorador que publica cada resultado guardado como un flujo | 3.4.2 |
| `PanelEnVivo` | un suscriptor que muestra los resultados según llegan | 3.4.2 |
| `DemoPanelEnVivo` | una ronda con el panel suscrito | 3.4.2 |

### La prueba

`PruebaDeEsfuerzoTest` convierte la prueba de esfuerzo del tema en una prueba unitaria: ocho hilos
guardan a la vez en el mismo repositorio, y el repositorio sincronizado y el concurrente tienen que
conservarlo todo. No hay prueba para el repositorio sin proteger, y es a propósito: una carrera no
se demuestra con una prueba que falla «casi siempre», sino con una frecuencia, como hace el tema.

## Las clases de los temas 1 y 2 que vienen copiadas

Las clases de `tema3.monitor` usan tipos que se construyeron antes: del Tema 1, `Monitor`,
`Sonda`, `SondaBase`, `Servicio` con sus tres implementaciones, `Resultado`, `Estado`, `Alerta`,
`Notificador`, `PoliticaDeAlerta`, `FallosConsecutivos`, las tres interfaces del repositorio y
`RepositorioEnMemoria`; del Tema 2, `RepositorioConVentana` y `VentanaDeslizante`. Para que este
proyecto **se clone y funcione sin configurar nada**, esas diecinueve clases están copiadas aquí en
lugar de declararse como dependencia de
[`t1-ejemplos`](https://github.com/UIB-22354-Programacion-Avanzada/t1-ejemplos) y
[`t2-ejemplos`](https://github.com/UIB-22354-Programacion-Avanzada/t2-ejemplos).

**Son una copia, y su original está en esos dos repositorios.** Si encuentras algo que corregir en
ellas, el sitio donde corregirlo es su tema. Aquí están para que el Tema 3 se pueda ejecutar, no
para evolucionar.

## Lo que el código no dice

Los tiempos y los recuentos que aparecen en el tema **se midieron en una máquina de dos núcleos**.
En la tuya saldrán otros, y en este tema eso no es un detalle: las carreras aparecen con más
frecuencia cuantos más núcleos haya, y los tiempos de las rondas dependen de cuántos hilos pueden
avanzar a la vez. Lo que tiene que coincidir es la **forma** de los resultados —qué versión pierde
datos y cuál no, qué ronda dura la suma y cuál el máximo—, no los números.

Y varios programas no dan nunca la misma salida dos veces: el orden de los saludos de
`DemoFormasDeCrearHilos`, los incrementos perdidos de `DemoCarrera`, los resultados que consigue
guardar `DemoRepositorioCompartido`. No es un defecto de los ejemplos: es de lo que trata el tema.

## Integridad académica

El uso de asistentes de IA se rige por las **condiciones de uso de la IA** de la
[guía docente](https://uib-22354-programacion-avanzada.github.io/website/es/informaciones/guia-docente.html).

## Licencia

Código bajo licencia [MIT](LICENSE) — copyright © 2026 Alejandro Mesejo. Los enunciados y el resto
del material docente están en el
[sitio web de la asignatura](https://uib-22354-programacion-avanzada.github.io/website/), bajo
licencia [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/deed.en).
