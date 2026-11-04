// DemoRepositorioCompartido.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import es.uib.prgava.tema1.monitor.Estado;
import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema2.monitor.RepositorioConVentana;

/**
 * Prueba de esfuerzo: ocho hilos guardan veinte mil resultados cada uno en el mismo repositorio,
 * repartidos entre cincuenta servicios. Primero en el repositorio del Tema 2 tal cual, después
 * envuelto en el decorador sincronizado.
 */
public final class DemoRepositorioCompartido {

    private DemoRepositorioCompartido() { }

    private static final int HILOS = 8;
    private static final int POR_HILO = 20_000;

    private static void esforzar(String nombre, RepositorioResultados repositorio)
            throws InterruptedException {
        List<Servicio> servicios = Escenario.servicios(50);
        var reventados = new AtomicInteger();

        Thread[] hilos = new Thread[HILOS];
        for (int h = 0; h < HILOS; h++) {
            final int desplazamiento = h;
            hilos[h] = new Thread(() -> {
                try {
                    for (int k = 0; k < POR_HILO; k++) {
                        Servicio servicio = servicios.get((k + desplazamiento) % servicios.size());
                        repositorio.guardar(new Resultado(servicio, Instant.EPOCH, Estado.ACTIVO, Duration.ZERO));
                    }
                } catch (RuntimeException e) {
                    reventados.incrementAndGet();
                    System.out.println("      un hilo revienta con " + e.getClass().getSimpleName());
                }
            });
            hilos[h].start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }

        long guardados = 0;
        for (Servicio servicio : repositorio.servicios()) {
            guardados += repositorio.historial(servicio).size();
        }
        System.out.printf("   %-28s esperados %d, guardados %d, hilos reventados %d%n",
                nombre, HILOS * POR_HILO, guardados, reventados.get());
    }

    public static void main(String[] args) throws InterruptedException {
        esforzar("RepositorioConVentana:", new RepositorioConVentana(10));
        esforzar("RepositorioSincronizado:", new RepositorioSincronizado(new RepositorioConVentana(10)));
    }
}
