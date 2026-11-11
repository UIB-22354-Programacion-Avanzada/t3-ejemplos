// DemoFuturoCompletable.java
package es.uib.prgava.tema3.herramientas;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Dos comprobaciones del mismo servidor —su nombre en el DNS y su puerto TCP— lanzadas a la vez y
 * combinadas en un veredicto cuando llegan las dos, con un plazo para la segunda.
 */
public final class DemoFuturoCompletable {

    private DemoFuturoCompletable() { }

    /** Una consulta que tarda {@code milis} y después devuelve si fue bien. */
    private static boolean consultar(String que, long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        System.out.println("   termina la consulta " + que);
        return true;
    }

    private static void comprobar(ExecutorService ejecutor, long milisTcp) {
        long inicio = System.nanoTime();

        CompletableFuture<Boolean> dns = CompletableFuture
                .supplyAsync(() -> consultar("DNS", 150), ejecutor);
        CompletableFuture<Boolean> tcp = CompletableFuture
                .supplyAsync(() -> consultar("TCP", milisTcp), ejecutor)
                .orTimeout(300, TimeUnit.MILLISECONDS)
                .exceptionally(error -> false);

        String veredicto = dns
                .thenCombine(tcp, (resuelve, responde) -> resuelve && responde ? "disponible" : "con problemas")
                .join();

        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("   veredicto: " + veredicto + ", en unos " + milis + " ms");
    }

    public static void main(String[] args) {
        try (ExecutorService ejecutor = Executors.newFixedThreadPool(2)) {
            System.out.println("--- el puerto TCP contesta en 200 ms");
            comprobar(ejecutor, 200);
            System.out.println("--- el puerto TCP tardaria 2 s; el plazo es de 300 ms");
            comprobar(ejecutor, 2_000);
        }
    }
}
