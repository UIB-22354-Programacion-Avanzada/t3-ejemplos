// RondaConEjecutor.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * Una ronda del monitor sobre un ejecutor, con un plazo para la ronda entera.
 *
 * <p>La ronda no crea hilos: entrega una tarea por servicio al ejecutor que recibe, y quien la
 * construye decide qué ejecutor es. Las comprobaciones que no terminan dentro del plazo se cancelan
 * y no aparecen en el resultado.
 */
public final class RondaConEjecutor {

    private final Monitor monitor;
    private final ExecutorService ejecutor;

    public RondaConEjecutor(Monitor monitor, ExecutorService ejecutor) {
        this.monitor = Objects.requireNonNull(monitor);
        this.ejecutor = Objects.requireNonNull(ejecutor);
    }

    /** Los resultados que llegaron a tiempo, en el orden de la lista de servicios. */
    public List<Resultado> ejecutar(List<Servicio> servicios, Duration plazo) throws InterruptedException {
        List<Callable<Resultado>> tareas = new ArrayList<>();
        for (Servicio servicio : servicios) {
            tareas.add(() -> monitor.comprobar(servicio));
        }

        List<Future<Resultado>> futuros = ejecutor.invokeAll(tareas, plazo.toMillis(), TimeUnit.MILLISECONDS);

        List<Resultado> resultados = new ArrayList<>();
        for (Future<Resultado> futuro : futuros) {
            try {
                resultados.add(futuro.get());
            } catch (CancellationException e) {
                // no terminó dentro del plazo: invokeAll la ha cancelado
            } catch (ExecutionException e) {
                // la comprobación lanzó una excepción; la causa está en e.getCause()
            }
        }
        return List.copyOf(resultados);
    }
}
