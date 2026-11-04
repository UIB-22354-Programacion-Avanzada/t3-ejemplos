// DemoPanelEnVivo.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uib.prgava.tema1.monitor.FallosConsecutivos;
import es.uib.prgava.tema1.monitor.Monitor;

/** Una ronda de veinte servicios con un panel suscrito a los resultados. */
public final class DemoPanelEnVivo {

    private DemoPanelEnVivo() { }

    public static void main(String[] args) throws InterruptedException {
        var servicios = Escenario.servicios(20);
        var panel = new PanelEnVivo();

        try (var repositorio = new RepositorioPublicador(new RepositorioConcurrente());
             ExecutorService ejecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            repositorio.suscribir(panel);
            var monitor = new Monitor(Escenario.sonda(servicios), new FallosConsecutivos(3), repositorio);
            new RondaConEjecutor(monitor, ejecutor).ejecutar(servicios, Duration.ofSeconds(10));
        }
        System.out.println("   el panel mostro " + panel.esperarFin() + " resultados");
    }
}
