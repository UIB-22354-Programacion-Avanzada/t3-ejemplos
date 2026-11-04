// DemoRondaEstructurada.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;

import es.uib.prgava.tema1.monitor.FallosConsecutivos;
import es.uib.prgava.tema1.monitor.Monitor;

/** La ronda de veinte servicios como ámbito estructurado, con un plazo holgado y con uno corto. */
public final class DemoRondaEstructurada {

    private DemoRondaEstructurada() { }

    public static void main(String[] args) throws InterruptedException {
        var servicios = Escenario.servicios(20);
        var repositorio = new RepositorioConcurrente();
        var monitor = new Monitor(Escenario.sonda(servicios), new FallosConsecutivos(3), repositorio);
        var ronda = new RondaEstructurada(monitor);

        for (Duration plazo : new Duration[] { Duration.ofSeconds(10), Duration.ofMillis(500) }) {
            long inicio = System.nanoTime();
            var resultados = ronda.ejecutar(servicios, plazo);
            long milis = (System.nanoTime() - inicio) / 1_000_000;
            System.out.printf("   plazo de %5d ms: %4d ms, %d resultados%n", plazo.toMillis(), milis, resultados.size());
        }
        System.out.println("   servicios con historial: " + repositorio.servicios().size());
    }
}
