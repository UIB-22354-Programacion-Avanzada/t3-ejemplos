// Contador.java
package es.uib.prgava.tema3.fundamentos;

/**
 * Un contador que se puede compartir entre hilos.
 *
 * <p>Los dos métodos son {@code synchronized}: para ejecutar cualquiera de ellos, un hilo tiene
 * que obtener antes el cerrojo intrínseco de este objeto, y mientras lo tiene ningún otro hilo
 * puede entrar en ninguno de los dos.
 */
public final class Contador {

    private long valor;

    public synchronized void incrementar() {
        valor++;
    }

    public synchronized long valor() {
        return valor;
    }
}
