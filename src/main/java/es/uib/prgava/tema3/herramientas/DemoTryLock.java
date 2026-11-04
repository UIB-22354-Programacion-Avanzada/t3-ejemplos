// DemoTryLock.java
package es.uib.prgava.tema3.herramientas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * El interbloqueo de {@code DemoInterbloqueo}, deshecho con cerrojos explícitos: quien no consigue
 * el segundo cerrojo a tiempo suelta el primero, espera un poco y vuelve a intentarlo.
 */
public final class DemoTryLock {

    private DemoTryLock() { }

    private static final Lock RECURSO_A = new ReentrantLock();
    private static final Lock RECURSO_B = new ReentrantLock();

    /** Intenta tomar {@code primero} y después {@code segundo}; si no puede, suelta y reintenta. */
    private static void trabajar(String nombre, Lock primero, Lock segundo, long espera)
            throws InterruptedException {
        int intento = 1;
        while (true) {
            primero.lock();
            try {
                Thread.sleep(100);
                if (segundo.tryLock(50, TimeUnit.MILLISECONDS)) {
                    try {
                        System.out.println("   " + nombre + ": tengo los dos al intento " + intento);
                        return;
                    } finally {
                        segundo.unlock();
                    }
                }
                System.out.println("   " + nombre + ": no consigo el segundo, suelto y reintento");
            } finally {
                primero.unlock();
            }
            Thread.sleep(espera);
            intento++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread primero = new Thread(() -> {
            try {
                trabajar("primero", RECURSO_A, RECURSO_B, 30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread segundo = new Thread(() -> {
            try {
                trabajar("segundo", RECURSO_B, RECURSO_A, 170);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        primero.start();
        segundo.start();
        primero.join();
        segundo.join();
        System.out.println("   los dos han terminado");
    }
}
