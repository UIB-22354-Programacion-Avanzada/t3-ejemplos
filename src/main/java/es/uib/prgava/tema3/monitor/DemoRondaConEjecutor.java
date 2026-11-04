// DemoRondaConEjecutor.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uib.prgava.tema1.monitor.FallosConsecutivos;
import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema2.monitor.RepositorioConVentana;

/**
 * La ronda de veinte servicios con ejecutores de distinto tamaño, y una última con un plazo más
 * corto que el tiempo de espera de los servicios caídos.
 */
public final class DemoRondaConEjecutor {

    private DemoRondaConEjecutor() { }

    public static void main(String[] args) throws InterruptedException {
        var servicios = Escenario.servicios(20);
        var monitor = new Monitor(Escenario.sonda(servicios), new FallosConsecutivos(3),
                new RepositorioSincronizado(new RepositorioConVentana(10)));
        var plazoHolgado = Duration.ofSeconds(10);

        for (int hilos : new int[] { 1, 4, 10, 20 }) {
            try (ExecutorService ejecutor = Executors.newFixedThreadPool(hilos)) {
                long inicio = System.nanoTime();
                var resultados = new RondaConEjecutor(monitor, ejecutor).ejecutar(servicios, plazoHolgado);
                long milis = (System.nanoTime() - inicio) / 1_000_000;
                System.out.printf("   %2d hilos: %5d ms, %d resultados%n", hilos, milis, resultados.size());
            }
        }

        try (ExecutorService ejecutor = Executors.newFixedThreadPool(20)) {
            long inicio = System.nanoTime();
            var resultados = new RondaConEjecutor(monitor, ejecutor).ejecutar(servicios, Duration.ofMillis(500));
            long milis = (System.nanoTime() - inicio) / 1_000_000;
            System.out.printf("   20 hilos y plazo de 500 ms: %d ms, %d resultados%n", milis, resultados.size());
        }
    }
}
