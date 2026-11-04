// RondaConHilos.java
package es.uib.prgava.tema3.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * Una ronda del monitor con un hilo por servicio.
 *
 * <p>Cada hilo hace exactamente lo que hacía el bucle secuencial del Tema 1 con un servicio:
 * llamar a {@code Monitor.comprobar}. Lo nuevo es solo que las comprobaciones ocurren a la vez, y
 * que la ronda no termina hasta que terminan todas.
 */
public final class RondaConHilos {

    private final Monitor monitor;

    public RondaConHilos(Monitor monitor) {
        this.monitor = Objects.requireNonNull(monitor);
    }

    public void ejecutar(List<Servicio> servicios) throws InterruptedException {
        var hilos = new ArrayList<Thread>();
        for (Servicio servicio : servicios) {
            Thread hilo = new Thread(() -> monitor.comprobar(servicio));
            hilos.add(hilo);
            hilo.start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }
    }
}
