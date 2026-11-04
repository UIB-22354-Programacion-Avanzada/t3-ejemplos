// DemoCarrera.java
package es.uib.prgava.tema3.fundamentos;

/**
 * Cuatro hilos incrementan un mismo contador un millón de veces cada uno, sin ninguna
 * sincronización. El resultado esperado es cuatro millones; el obtenido, otro.
 */
public final class DemoCarrera {

    private DemoCarrera() { }

    private static int contador = 0;

    public static void main(String[] args) throws InterruptedException {
        final int hilos = 4;
        final int veces = 1_000_000;

        Thread[] trabajadores = new Thread[hilos];
        for (int i = 0; i < hilos; i++) {
            trabajadores[i] = new Thread(() -> {
                for (int k = 0; k < veces; k++) {
                    contador++;
                }
            });
            trabajadores[i].start();
        }
        for (Thread t : trabajadores) {
            t.join();
        }

        System.out.println("   esperado: " + (hilos * veces));
        System.out.println("   obtenido: " + contador);
        System.out.println("   perdidos: " + (hilos * veces - contador));
    }
}
