// RondaEstructurada.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * Una ronda del monitor como un ámbito de concurrencia estructurada, con plazo.
 *
 * <p>Cada comprobación es una subtarea del ámbito, en su propio hilo virtual. Cuando el bloque
 * {@code try} termina, todas las subtareas han terminado: las que no acabaron antes del plazo han
 * sido canceladas <strong>y esperadas</strong>, de modo que ninguna sigue trabajando después de la
 * ronda.
 *
 * <p>Usa una API en vista previa en Java 25: hay que compilar y ejecutar con
 * {@code --enable-preview}.
 */
public final class RondaEstructurada {

    private final Monitor monitor;

    public RondaEstructurada(Monitor monitor) {
        this.monitor = Objects.requireNonNull(monitor);
    }

    /** Los resultados que llegaron a tiempo, en el orden de la lista de servicios. */
    public List<Resultado> ejecutar(List<Servicio> servicios, Duration plazo) throws InterruptedException {
        List<Subtask<Resultado>> subtareas = new ArrayList<>();

        try (var ronda = StructuredTaskScope.open(Joiner.<Resultado>awaitAll(),
                configuracion -> configuracion.withTimeout(plazo))) {
            for (Servicio servicio : servicios) {
                subtareas.add(ronda.fork(() -> monitor.comprobar(servicio)));
            }
            try {
                ronda.join();
            } catch (StructuredTaskScope.TimeoutException e) {
                // venció el plazo: el ámbito ha cancelado las subtareas que faltaban
            }
        }

        List<Resultado> resultados = new ArrayList<>();
        for (Subtask<Resultado> subtarea : subtareas) {
            if (subtarea.state() == Subtask.State.SUCCESS) {
                resultados.add(subtarea.get());
            }
        }
        return List.copyOf(resultados);
    }
}
