// DemoMilServicios.java
package es.uib.prgava.tema3.monitor;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uib.prgava.tema1.monitor.FallosConsecutivos;
import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema1.monitor.Sonda;

/**
 * Una ronda de mil servicios: con cien hilos de plataforma, con un hilo virtual por servicio, y
 * con hilos virtuales y como mucho cien sondeos a la vez.
 */
public final class DemoMilServicios {

    private DemoMilServicios() { }

    private static void ronda(String nombre, Sonda sonda, List<Servicio> servicios, ExecutorService ejecutor)
            throws InterruptedException {
        var monitor = new Monitor(sonda, new FallosConsecutivos(3), new RepositorioConcurrente());
        var hilosDelSistema = ManagementFactory.getThreadMXBean();
        hilosDelSistema.resetPeakThreadCount();
        try (ejecutor) {
            long inicio = System.nanoTime();
            var resultados = new RondaConEjecutor(monitor, ejecutor).ejecutar(servicios, Duration.ofSeconds(60));
            long milis = (System.nanoTime() - inicio) / 1_000_000;
            System.out.printf("   %-40s %5d ms, %4d resultados, como mucho %4d hilos del sistema%n",
                    nombre, milis, resultados.size(), hilosDelSistema.getPeakThreadCount());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var servicios = Escenario.servicios(1_000);
        var sonda = Escenario.sonda(servicios);

        ronda("100 hilos de plataforma:", sonda, servicios, Executors.newFixedThreadPool(100));
        ronda("un hilo virtual por servicio:", sonda, servicios, Executors.newVirtualThreadPerTaskExecutor());
        ronda("hilos virtuales, 100 sondeos a la vez:", new SondaLimitada(sonda, 100), servicios,
                Executors.newVirtualThreadPerTaskExecutor());
    }
}
