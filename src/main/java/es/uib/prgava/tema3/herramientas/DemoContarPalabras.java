// DemoContarPalabras.java
package es.uib.prgava.tema3.herramientas;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Cuatro hilos cuentan las mismas palabras en un {@code ConcurrentHashMap}: primero leyendo y
 * escribiendo por separado, después con una sola operación atómica. Cada forma se repite veinte
 * veces, porque lo que describe una carrera es con qué frecuencia aparece.
 */
public final class DemoContarPalabras {

    private static final int HILOS = 4;
    private static final int VUELTAS = 100_000;
    private static final int REPETICIONES = 20;
    private static final List<String> PALABRAS = List.of("red", "sonda", "hilo", "cola", "mapa");

    private DemoContarPalabras() { }

    /** Comprobar y después actuar: dos operaciones seguras que juntas no lo son. */
    private static void contarConGetYPut(Map<String, Integer> cuentas, String palabra) {
        Integer actual = cuentas.get(palabra);
        cuentas.put(palabra, actual == null ? 1 : actual + 1);
    }

    /** Leer, sumar y escribir en una sola operación atómica. */
    private static void contarConMerge(Map<String, Integer> cuentas, String palabra) {
        cuentas.merge(palabra, 1, Integer::sum);
    }

    /** Lanza los hilos, espera a que terminen y devuelve cuántas palabras se han perdido. */
    private static long perdidas(BiConsumer<Map<String, Integer>, String> contar)
            throws InterruptedException {
        Map<String, Integer> cuentas = new ConcurrentHashMap<>();
        Thread[] hilos = new Thread[HILOS];
        for (int h = 0; h < HILOS; h++) {
            hilos[h] = new Thread(() -> {
                for (int i = 0; i < VUELTAS; i++) {
                    for (String palabra : PALABRAS) {
                        contar.accept(cuentas, palabra);
                    }
                }
            });
            hilos[h].start();
        }
        for (Thread hilo : hilos) {
            hilo.join();
        }
        long contadas = 0;
        for (int n : cuentas.values()) {
            contadas += n;
        }
        return (long) HILOS * VUELTAS * PALABRAS.size() - contadas;
    }

    private static void medir(String nombre, BiConsumer<Map<String, Integer>, String> contar)
            throws InterruptedException {
        int conPerdidas = 0;
        long total = 0;
        for (int r = 0; r < REPETICIONES; r++) {
            long p = perdidas(contar);
            if (p > 0) {
                conPerdidas++;
            }
            total += p;
        }
        System.out.printf("   %-11s %2d de %d con perdidas, %8d palabras perdidas en total%n",
                nombre, conPerdidas, REPETICIONES, total);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("   cada prueba cuenta " + HILOS * VUELTAS * PALABRAS.size() + " palabras");
        medir("get y put:", DemoContarPalabras::contarConGetYPut);
        medir("merge:", DemoContarPalabras::contarConMerge);
    }
}
