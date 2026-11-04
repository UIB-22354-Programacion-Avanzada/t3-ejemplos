// DemoSemaforo.java
package es.uib.prgava.tema3.virtuales;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/** Diez descargas simuladas en hilos virtuales: sin límite, y con un semáforo de tres permisos. */
public final class DemoSemaforo {

    private static final int DESCARGAS = 10;
    private static final long DURACION_MILIS = 200;

    /** Cuántas descargas hay en curso ahora mismo, y el máximo que se ha alcanzado. */
    private static final AtomicInteger EN_CURSO = new AtomicInteger();
    private static final AtomicInteger MAXIMO = new AtomicInteger();

    private DemoSemaforo() { }

    /** Simula una descarga que tarda {@code DURACION_MILIS} y anota cuántas hay a la vez. */
    private static void descargar() {
        MAXIMO.accumulateAndGet(EN_CURSO.incrementAndGet(), Math::max);
        try {
            Thread.sleep(DURACION_MILIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            EN_CURSO.decrementAndGet();
        }
    }

    private static void medir(String nombre, Runnable tarea) {
        MAXIMO.set(0);
        long inicio = System.nanoTime();
        try (var ejecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < DESCARGAS; i++) {
                ejecutor.submit(tarea);
            }
        }
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.printf("   %-16s %4d ms, como mucho %2d descargas a la vez%n",
                nombre, milis, MAXIMO.get());
    }

    public static void main(String[] args) {
        medir("sin limite:", DemoSemaforo::descargar);

        var permisos = new Semaphore(3);
        medir("con 3 permisos:", () -> {
            try {
                permisos.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            try {
                descargar();
            } finally {
                permisos.release();
            }
        });
    }
}
