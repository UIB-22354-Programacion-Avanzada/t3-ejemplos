// DemoFormasDeCrearHilos.java
package es.uib.prgava.tema3.fundamentos;

/** Cuatro formas de crear un hilo, todas con la misma tarea. */
public final class DemoFormasDeCrearHilos {

    private DemoFormasDeCrearHilos() { }

    /** Forma 1: una subclase de Thread que redefine run. */
    private static final class HiloSaludador extends Thread {
        HiloSaludador() {
            super("forma-1");
        }

        @Override
        public void run() {
            saludar();
        }
    }

    /** Forma 2: una clase con nombre que implementa Runnable. */
    private static final class TareaSaludo implements Runnable {
        @Override
        public void run() {
            saludar();
        }
    }

    /** La tarea común: decir en qué hilo se ejecuta. */
    private static void saludar() {
        System.out.println("   hola desde " + Thread.currentThread().getName());
    }

    public static void main(String[] args) throws InterruptedException {
        Thread h1 = new HiloSaludador();                              // 1
        Thread h2 = new Thread(new TareaSaludo(), "forma-2");         // 2
        Thread h3 = new Thread(() -> saludar(), "forma-3");           // 3
        Thread h4 = Thread.ofPlatform().name("forma-4").unstarted(    // 4
                DemoFormasDeCrearHilos::saludar);

        for (Thread h : new Thread[] {h1, h2, h3, h4}) {
            h.start();
        }
        for (Thread h : new Thread[] {h1, h2, h3, h4}) {
            h.join();
        }
        System.out.println("   main: los cuatro han terminado");
    }
}
