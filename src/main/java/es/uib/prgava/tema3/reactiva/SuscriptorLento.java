// SuscriptorLento.java
package es.uib.prgava.tema3.reactiva;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

/**
 * Un suscriptor que tarda cien milisegundos en procesar cada elemento y que los pide de uno en uno.
 *
 * <p>El método {@code request(1)} es la contrapresión: el suscriptor le dice al publicador cuántos
 * elementos más puede aceptar, y el publicador no le manda ni uno más.
 */
public final class SuscriptorLento implements Flow.Subscriber<Integer> {

    private final List<Integer> recibidos = new ArrayList<>();
    private final CompletableFuture<List<Integer>> fin = new CompletableFuture<>();
    private Flow.Subscription suscripcion;

    @Override
    public void onSubscribe(Flow.Subscription suscripcion) {
        this.suscripcion = suscripcion;
        suscripcion.request(1);
    }

    @Override
    public void onNext(Integer elemento) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        recibidos.add(elemento);
        suscripcion.request(1);
    }

    @Override
    public void onError(Throwable error) {
        fin.completeExceptionally(error);
    }

    @Override
    public void onComplete() {
        fin.complete(List.copyOf(recibidos));
    }

    /** Lo que llegó a procesar, cuando el publicador cierre. */
    public List<Integer> recibidos() {
        return fin.join();
    }
}
