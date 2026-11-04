// DemoMilHilos.java
package es.uib.prgava.tema3.virtuales;

import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Diez mil tareas que esperan cien milisegundos cada una: con un ejecutor de doscientos hilos de
 * plataforma y con un hilo virtual por tarea. Al final de cada una, cuántos hilos del sistema
 * operativo llegó a haber a la vez.
 */
public final class DemoMilHilos {

    private DemoMilHilos() { }

    private static final int TAREAS = 10_000;

    private static void esperar() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void medir(String nombre, ExecutorService ejecutor) {
        var hilosDelSistema = ManagementFactory.getThreadMXBean();
        hilosDelSistema.resetPeakThreadCount();
        long inicio = System.nanoTime();
        try (ejecutor) {
            for (int i = 0; i < TAREAS; i++) {
                ejecutor.submit(DemoMilHilos::esperar);
            }
        }
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.printf("   %-34s %6d ms, como mucho %4d hilos del sistema%n",
                nombre, milis, hilosDelSistema.getPeakThreadCount());
    }

    public static void main(String[] args) {
        medir("200 hilos de plataforma:", Executors.newFixedThreadPool(200));
        medir("un hilo virtual por tarea:", Executors.newVirtualThreadPerTaskExecutor());
    }
}
