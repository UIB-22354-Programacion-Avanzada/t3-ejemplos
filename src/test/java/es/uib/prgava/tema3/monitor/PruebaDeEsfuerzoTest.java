// PruebaDeEsfuerzoTest.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import es.uib.prgava.tema1.monitor.Estado;
import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema2.monitor.RepositorioConVentana;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * La prueba de esfuerzo de 3.1.3, convertida en prueba: ocho hilos guardan a la
 * vez en el mismo repositorio, y las dos versiones seguras tienen que conservarlo todo.
 *
 * <p>No hay prueba para el repositorio sin proteger, y es a propósito: una prueba que falla
 * «casi siempre» no es una prueba. Que una carrera exista no se demuestra con una prueba unitaria,
 * sino con una frecuencia, como hace el tema; lo que sí se puede exigir siempre es que las
 * versiones correctas no pierdan nada.
 */
class PruebaDeEsfuerzoTest {

    private static final int HILOS = 8;
    private static final int POR_HILO = 5_000;

    /** Lanza los hilos, espera a que terminen y devuelve cuántos han reventado. */
    private static int esforzar(RepositorioResultados repositorio) throws InterruptedException {
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
                }
            });
            hilos[h].start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }
        return reventados.get();
    }

    private static long guardados(RepositorioResultados repositorio) {
        long total = 0;
        for (Servicio servicio : repositorio.servicios()) {
            total += repositorio.historial(servicio).size();
        }
        return total;
    }

    @Test
    void elRepositorioSincronizadoNoPierdeNada() throws InterruptedException {
        var repositorio = new RepositorioSincronizado(new RepositorioConVentana(10));
        assertEquals(0, esforzar(repositorio), "ningún hilo debe reventar");
        assertEquals((long) HILOS * POR_HILO, guardados(repositorio));
    }

    @Test
    void elRepositorioConcurrenteNoPierdeNada() throws InterruptedException {
        var repositorio = new RepositorioConcurrente();
        assertEquals(0, esforzar(repositorio), "ningún hilo debe reventar");
        assertEquals((long) HILOS * POR_HILO, guardados(repositorio));
    }
}
