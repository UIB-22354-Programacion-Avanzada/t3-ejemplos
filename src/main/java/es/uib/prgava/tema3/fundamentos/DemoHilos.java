// DemoHilos.java
package es.uib.prgava.tema3.fundamentos;

/** Dos hilos que trabajan a la vez y un hilo principal que los espera. */
public final class DemoHilos {

    private DemoHilos() { }

    /** Una tarea que escribe tres líneas con una pausa entre cada una. */
    private static Runnable tarea(String nombre, long pausaMilis) {
        return () -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("   " + nombre + " paso " + i
                        + "  (hilo " + Thread.currentThread().getName() + ")");
                try {
                    Thread.sleep(pausaMilis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        };
    }

    public static void main(String[] args) throws InterruptedException {
        Thread rapido = new Thread(tarea("rapido", 100), "hilo-rapido");
        Thread lento = new Thread(tarea("lento ", 250), "hilo-lento");

        long inicio = System.nanoTime();
        rapido.start();
        lento.start();
        System.out.println("   main: he arrancado los dos y sigo");

        rapido.join();
        lento.join();
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.println("   main: los dos han terminado en unos " + milis + " ms");
    }
}
