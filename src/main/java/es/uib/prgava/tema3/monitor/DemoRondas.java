// DemoRondas.java
package es.uib.prgava.tema3.monitor;

import es.uib.prgava.tema1.monitor.FallosConsecutivos;
import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema1.monitor.RepositorioEnMemoria;

/** La misma ronda de veinte servicios, primero uno detrás de otro y después a la vez. */
public final class DemoRondas {

    private DemoRondas() { }

    public static void main(String[] args) throws InterruptedException {
        var servicios = Escenario.servicios(20);
        var monitor = new Monitor(Escenario.sonda(servicios), new FallosConsecutivos(3),
                new RepositorioEnMemoria());

        long inicio = System.nanoTime();
        monitor.comprobar(servicios);
        long secuencial = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("   ronda secuencial:   " + secuencial + " ms");

        inicio = System.nanoTime();
        new RondaConHilos(monitor).ejecutar(servicios);
        long concurrente = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("   ronda con hilos:    " + concurrente + " ms");
    }
}
