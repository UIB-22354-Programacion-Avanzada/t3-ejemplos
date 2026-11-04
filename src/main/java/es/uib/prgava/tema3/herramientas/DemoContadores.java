// DemoContadores.java
package es.uib.prgava.tema3.herramientas;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Tres maneras de contar desde cuatro hilos: sin proteger, con un cerrojo y con una variable
 * atómica. Las tres hacen el mismo trabajo; solo dos lo hacen bien.
 */
public final class DemoContadores {

    private DemoContadores() { }

    private static final int HILOS = 4;
    private static final int VECES = 5_000_000;

    private static long sinProteger = 0;
    private static long conCerrojo = 0;
    private static final Object CERROJO = new Object();
    private static final AtomicLong atomico = new AtomicLong();

    /** Lanza {@code HILOS} hilos que ejecutan {@code incremento} {@code VECES} veces, y los espera. */
    private static long cronometrar(Runnable incremento) throws InterruptedException {
        Thread[] hilos = new Thread[HILOS];
        long inicio = System.nanoTime();
        for (int i = 0; i < HILOS; i++) {
            hilos[i] = new Thread(() -> {
                for (int k = 0; k < VECES; k++) {
                    incremento.run();
                }
            });
            hilos[i].start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }
        return (System.nanoTime() - inicio) / 1_000_000;
    }

    public static void main(String[] args) throws InterruptedException {
        long esperado = (long) HILOS * VECES;

        long t1 = cronometrar(() -> sinProteger++);
        long t2 = cronometrar(() -> {
            synchronized (CERROJO) {
                conCerrojo++;
            }
        });
        long t3 = cronometrar(atomico::incrementAndGet);

        System.out.printf("   %-14s %10s %10s %8s%n", "", "obtenido", "esperado", "tiempo");
        System.out.printf("   %-14s %10d %10d %5d ms%n", "sin proteger", sinProteger, esperado, t1);
        System.out.printf("   %-14s %10d %10d %5d ms%n", "synchronized", conCerrojo, esperado, t2);
        System.out.printf("   %-14s %10d %10d %5d ms%n", "AtomicLong", atomico.get(), esperado, t3);
    }
}
