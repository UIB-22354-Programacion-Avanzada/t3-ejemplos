// DemoEjecutor.java
package es.uib.prgava.tema3.herramientas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** Seis tareas, tres hilos: el ejecutor reparte y el programa recoge los resultados. */
public final class DemoEjecutor {

    private DemoEjecutor() { }

    /** Una tarea que tarda un poco y devuelve un texto con el hilo que la ejecutó. */
    private static Callable<String> tarea(int numero) {
        return () -> {
            Thread.sleep(200);
            return "tarea " + numero + " hecha por " + Thread.currentThread().getName();
        };
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long inicio = System.nanoTime();
        try (ExecutorService ejecutor = Executors.newFixedThreadPool(3)) {
            List<Future<String>> futuros = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                futuros.add(ejecutor.submit(tarea(i)));
            }
            System.out.println("   main: seis tareas entregadas, sigo sin esperar");

            for (Future<String> futuro : futuros) {
                System.out.println("   " + futuro.get());
            }
        }
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("   total: unos " + milis + " ms");
    }
}
