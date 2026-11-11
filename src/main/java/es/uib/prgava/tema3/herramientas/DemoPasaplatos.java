// DemoPasaplatos.java
package es.uib.prgava.tema3.herramientas;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Un cocinero deja platos en un pasaplatos con sitio para tres, y dos camareros los recogen. */
public final class DemoPasaplatos {

    private static final BlockingQueue<String> PASAPLATOS = new ArrayBlockingQueue<>(3);
    private static final String FIN = "fin del servicio";
    private static final long INICIO = System.nanoTime();

    private DemoPasaplatos() { }

    /** Milisegundos transcurridos desde que empezó el programa. */
    private static long ahora() {
        return (System.nanoTime() - INICIO) / 1_000_000;
    }

    /** Escribe qué ha pasado y cuándo, y cuánto ha esperado antes, si ha sido apreciable. */
    private static void anotar(String que, long antes) {
        long espera = ahora() - antes;
        System.out.printf("   %4d ms  %s%s%n", ahora(), que,
                espera > 20 ? "  (ha esperado " + espera + " ms)" : "");
    }

    private static void cocinar() throws InterruptedException {
        for (int i = 1; i <= 8; i++) {
            Thread.sleep(100);                              // cocinar cuesta 100 ms
            long antes = ahora();
            PASAPLATOS.put("plato " + i);                   // espera si esta lleno
            anotar("cocinero deja el plato " + i, antes);
        }
        PASAPLATOS.put(FIN);                                // un aviso de fin
        PASAPLATOS.put(FIN);                                // por cada camarero
    }

    private static void servir() throws InterruptedException {
        while (true) {
            long antes = ahora();
            String plato = PASAPLATOS.take();               // espera si esta vacio
            if (plato.equals(FIN)) {
                return;
            }
            anotar(Thread.currentThread().getName() + " recoge el " + plato, antes);
            Thread.sleep(500);                              // servirlo cuesta 500 ms
        }
    }

    /** Arranca un hilo con ese nombre que cocina o sirve; si lo interrumpen, termina. */
    private static Thread arrancar(String nombre, boolean cocina) {
        return Thread.ofPlatform().name(nombre).start(() -> {
            try {
                if (cocina) {
                    cocinar();
                } else {
                    servir();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] hilos = {arrancar("cocinero", true), arrancar("camarero-1", false),
                          arrancar("camarero-2", false)};
        for (Thread hilo : hilos) {
            hilo.join();
        }
        System.out.printf("   %4d ms  se cierra la cocina%n", ahora());
    }
}
