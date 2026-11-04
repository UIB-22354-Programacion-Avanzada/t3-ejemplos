// DemoExclusion.java
package es.uib.prgava.tema3.fundamentos;

/** El mismo experimento que {@code DemoCarrera}, con el contador protegido. */
public final class DemoExclusion {

    private DemoExclusion() { }

    public static void main(String[] args) throws InterruptedException {
        final int hilos = 4;
        final int veces = 1_000_000;
        var contador = new Contador();

        Thread[] trabajadores = new Thread[hilos];
        for (int i = 0; i < hilos; i++) {
            trabajadores[i] = new Thread(() -> {
                for (int k = 0; k < veces; k++) {
                    contador.incrementar();
                }
            });
            trabajadores[i].start();
        }
        for (Thread t : trabajadores) {
            t.join();
        }

        System.out.println("   esperado: " + (hilos * veces));
        System.out.println("   obtenido: " + contador.valor());
    }
}
