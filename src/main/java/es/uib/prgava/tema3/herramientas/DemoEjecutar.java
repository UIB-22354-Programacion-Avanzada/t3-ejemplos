// DemoEjecutar.java
package es.uib.prgava.tema3.herramientas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Seis tareas sin resultado, entregadas a un ejecutor de tres hilos con execute. */
public final class DemoEjecutar {

    private DemoEjecutar() { }

    /** Una tarea que tarda un poco y dice qué hilo la ejecuta. */
    private static Runnable tarea(int numero) {
        return () -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            System.out.println("   tarea " + numero + " hecha por " + Thread.currentThread().getName());
        };
    }

    public static void main(String[] args) {
        long inicio = System.nanoTime();
        try (ExecutorService ejecutor = Executors.newFixedThreadPool(3)) {
            for (int i = 1; i <= 6; i++) {
                ejecutor.execute(tarea(i));
            }
            System.out.println("   main: seis tareas entregadas");
        }
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("   total: unos " + milis + " ms");
    }
}
