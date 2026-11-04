// NotificadorAsincrono.java
package es.uib.prgava.tema3.monitor;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import es.uib.prgava.tema1.monitor.Alerta;
import es.uib.prgava.tema1.monitor.Notificador;

/**
 * Un notificador que no hace esperar a quien notifica.
 *
 * <p>Las comprobaciones son los <strong>productores</strong>: dejan cada alerta en una cola y
 * vuelven inmediatamente a su trabajo. Un único hilo <strong>consumidor</strong>, el repartidor,
 * saca las alertas de la cola y se las entrega al notificador de destino, que puede ser todo lo
 * lento que sea un servidor de correo. Al cerrar, el repartidor entrega lo que quede pendiente y
 * termina.
 */
public final class NotificadorAsincrono implements Notificador, AutoCloseable {

    private final BlockingQueue<Alerta> pendientes = new LinkedBlockingQueue<>();
    private final Notificador destino;
    private final Thread repartidor;
    private volatile boolean cerrado = false;

    public NotificadorAsincrono(Notificador destino) {
        this.destino = Objects.requireNonNull(destino);
        this.repartidor = new Thread(this::repartir, "repartidor-de-alertas");
        this.repartidor.start();
    }

    /** Lo llaman los productores: encola y vuelve sin esperar a la entrega. */
    @Override
    public void notificar(Alerta alerta) {
        if (cerrado) {
            throw new IllegalStateException("El notificador ya está cerrado");
        }
        pendientes.add(alerta);
    }

    /** El bucle del consumidor: hasta que lo cierren y no quede nada pendiente. */
    private void repartir() {
        try {
            while (!cerrado || !pendientes.isEmpty()) {
                Alerta alerta = pendientes.poll(100, TimeUnit.MILLISECONDS);
                if (alerta != null) {
                    destino.notificar(alerta);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Deja de aceptar alertas, espera a que se entreguen las pendientes y termina.
     *
     * <p>No declara {@code InterruptedException}: si a quien cierra lo interrumpen mientras espera,
     * se restaura la marca de interrupción para que la vea quien esté más arriba.
     */
    @Override
    public void close() {
        cerrado = true;
        try {
            repartidor.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
