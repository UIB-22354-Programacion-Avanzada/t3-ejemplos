// SondaLimitada.java
package es.uib.prgava.tema3.monitor;

import java.util.Objects;
import java.util.concurrent.Semaphore;

import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema1.monitor.Sonda;

/**
 * Decorador que limita cuántos sondeos hay en curso a la vez.
 *
 * <p>Con hilos virtuales no se limita la concurrencia con el tamaño de un ejecutor, porque no hay
 * ejecutor que dimensionar. Se limita en el sitio donde está el recurso escaso —aquí, la red y los
 * servidores que se sondean— con un semáforo de {@code simultaneos} permisos.
 */
public final class SondaLimitada implements Sonda {

    private final Sonda interna;
    private final Semaphore permisos;

    public SondaLimitada(Sonda interna, int simultaneos) {
        this.interna = Objects.requireNonNull(interna);
        if (simultaneos < 1) {
            throw new IllegalArgumentException("Hace falta al menos un permiso: " + simultaneos);
        }
        this.permisos = new Semaphore(simultaneos);
    }

    @Override
    public Resultado sondear(Servicio servicio) {
        try {
            permisos.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Resultado.caido(servicio);
        }
        try {
            return interna.sondear(servicio);
        } finally {
            permisos.release();
        }
    }
}
