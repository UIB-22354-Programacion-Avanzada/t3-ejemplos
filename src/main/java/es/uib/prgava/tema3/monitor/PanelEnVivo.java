// PanelEnVivo.java
package es.uib.prgava.tema3.monitor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import es.uib.prgava.tema1.monitor.Resultado;

/**
 * Un suscriptor que escribe cada resultado en cuanto llega, con el tiempo transcurrido desde que se
 * suscribió. Es el embrión de un panel de control que se actualiza solo.
 */
public final class PanelEnVivo implements Flow.Subscriber<Resultado> {

    private final CompletableFuture<Integer> fin = new CompletableFuture<>();
    private Flow.Subscription suscripcion;
    private long inicio;
    private int recibidos;

    @Override
    public void onSubscribe(Flow.Subscription suscripcion) {
        this.suscripcion = suscripcion;
        this.inicio = System.nanoTime();
        suscripcion.request(1);
    }

    @Override
    public void onNext(Resultado resultado) {
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.printf("   %5d ms  %-22s %-9s %4d ms%n", milis, resultado.servicio().nombre(),
                resultado.estado(), resultado.latencia().toMillis());
        recibidos++;
        suscripcion.request(1);
    }

    @Override
    public void onError(Throwable error) {
        fin.completeExceptionally(error);
    }

    @Override
    public void onComplete() {
        fin.complete(recibidos);
    }

    /** Espera a que el flujo se cierre y devuelve cuántos resultados se mostraron. */
    public int esperarFin() {
        return fin.join();
    }
}
