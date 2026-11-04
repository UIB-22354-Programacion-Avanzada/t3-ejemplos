// DemoInterbloqueo.java
package es.uib.prgava.tema3.fundamentos;

/**
 * Dos hilos y dos cerrojos, tomados en orden contrario. Este programa no termina: cada hilo espera
 * un cerrojo que tiene el otro. Páralo con Ctrl+C, o mejor, antes de pararlo mira qué está haciendo
 * con {@code jcmd}, como explica la técnica de depuración.
 */
public final class DemoInterbloqueo {

    private DemoInterbloqueo() { }

    private static final Object RECURSO_A = new Object();
    private static final Object RECURSO_B = new Object();

    private static void pausa() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        System.out.println("   proceso " + ProcessHandle.current().pid());

        Thread primero = new Thread(() -> {
            synchronized (RECURSO_A) {
                System.out.println("   primero: tengo A, voy a por B");
                pausa();
                synchronized (RECURSO_B) {
                    System.out.println("   primero: tengo los dos");
                }
            }
        }, "primero");

        Thread segundo = new Thread(() -> {
            synchronized (RECURSO_B) {
                System.out.println("   segundo: tengo B, voy a por A");
                pausa();
                synchronized (RECURSO_A) {
                    System.out.println("   segundo: tengo los dos");
                }
            }
        }, "segundo");

        primero.start();
        segundo.start();
    }
}
