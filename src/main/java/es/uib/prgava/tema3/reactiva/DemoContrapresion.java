// DemoContrapresion.java
package es.uib.prgava.tema3.reactiva;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SubmissionPublisher;

/**
 * Un publicador rápido y un suscriptor lento, con un búfer de cuatro elementos entre los dos. Dos
 * estrategias cuando el búfer se llena: esperar ({@code submit}) o descartar ({@code offer}).
 */
public final class DemoContrapresion {

    private DemoContrapresion() { }

    private static final int ELEMENTOS = 12;
    private static final int BUFER = 4;

    public static void main(String[] args) {
        try (ExecutorService ejecutor = Executors.newVirtualThreadPerTaskExecutor()) {

            System.out.println("--- submit: el publicador espera cuando el bufer esta lleno");
            var lento = new SuscriptorLento();
            long inicio = System.nanoTime();
            try (var publicador = new SubmissionPublisher<Integer>(ejecutor, BUFER)) {
                publicador.subscribe(lento);
                for (int i = 1; i <= ELEMENTOS; i++) {
                    publicador.submit(i);
                }
                long milis = (System.nanoTime() - inicio) / 1_000_000;
                System.out.println("   el publicador termino de publicar en " + milis + " ms");
            }
            System.out.println("   el suscriptor recibio " + lento.recibidos());

            System.out.println("--- offer: el publicador descarta lo que no cabe");
            var otroLento = new SuscriptorLento();
            List<Integer> descartados = new ArrayList<>();
            inicio = System.nanoTime();
            try (var publicador = new SubmissionPublisher<Integer>(ejecutor, BUFER)) {
                publicador.subscribe(otroLento);
                for (int i = 1; i <= ELEMENTOS; i++) {
                    publicador.offer(i, (suscriptor, elemento) -> {
                        descartados.add(elemento);
                        return false;
                    });
                }
                long milis = (System.nanoTime() - inicio) / 1_000_000;
                System.out.println("   el publicador termino de publicar en " + milis + " ms");
            }
            System.out.println("   el suscriptor recibio " + otroLento.recibidos());
            System.out.println("   se descartaron       " + descartados);
        }
    }
}
