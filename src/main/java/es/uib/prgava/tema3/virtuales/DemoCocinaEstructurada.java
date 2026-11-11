// DemoCocinaEstructurada.java
package es.uib.prgava.tema3.virtuales;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;

/** El pasaplatos de 3.2.5 como ámbito estructurado: si el camarero falla, se cierra la cocina. */
public final class DemoCocinaEstructurada {

    private static final BlockingQueue<String> PASAPLATOS = new ArrayBlockingQueue<>(3);
    private static final String FIN = "fin del servicio";
    private static final long INICIO = System.nanoTime();

    private DemoCocinaEstructurada() { }

    private static void anotar(String que) {
        System.out.printf("   %4d ms  %s%n", (System.nanoTime() - INICIO) / 1_000_000, que);
    }

    private static int cocinar() throws InterruptedException {
        try {
            for (int i = 1; i <= 8; i++) {
                Thread.sleep(100);                              // cocinar cuesta 100 ms
                PASAPLATOS.put("plato " + i);                   // espera si esta lleno
                anotar("cocinero deja el plato " + i);
            }
            PASAPLATOS.put(FIN);
            return 8;
        } catch (InterruptedException e) {
            anotar("cocinero: cancelado");
            throw e;
        }
    }

    private static int servir() throws InterruptedException {
        int servidos = 0;
        while (true) {
            String plato = PASAPLATOS.take();                   // espera si esta vacio
            if (plato.equals(FIN)) {
                return servidos;
            }
            if (plato.equals("plato 3")) {
                anotar("camarero: se le cae el plato 3");
                throw new IllegalStateException("se ha caido el plato 3");
            }
            anotar("camarero recoge el " + plato);
            Thread.sleep(500);                                  // servirlo cuesta 500 ms
            servidos++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try (var cocina = StructuredTaskScope.open(Joiner.<Integer>awaitAllSuccessfulOrThrow())) {
            cocina.fork(() -> cocinar());
            cocina.fork(() -> servir());
            cocina.join();
            anotar("servicio completo");
        } catch (StructuredTaskScope.FailedException e) {
            anotar("se cierra la cocina: " + e.getCause().getMessage());
        }
    }
}
