// RepositorioPublicador.java
package es.uib.prgava.tema3.monitor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * Decorador que, además de guardar cada resultado, lo publica como un flujo al que se pueden
 * suscribir quienes quieran verlos en el momento en que llegan.
 *
 * <p>El repositorio interno tiene que ser seguro para varios hilos, porque las comprobaciones
 * guardan a la vez; el publicador ya lo es.
 */
public final class RepositorioPublicador implements RepositorioResultados, AutoCloseable {

    private final RepositorioResultados interno;
    private final SubmissionPublisher<Resultado> publicador = new SubmissionPublisher<>();

    public RepositorioPublicador(RepositorioResultados interno) {
        this.interno = Objects.requireNonNull(interno);
    }

    public void suscribir(Flow.Subscriber<? super Resultado> suscriptor) {
        publicador.subscribe(suscriptor);
    }

    @Override
    public void guardar(Resultado resultado) {
        interno.guardar(resultado);
        publicador.submit(resultado);
    }

    @Override
    public List<Resultado> historial(Servicio servicio) {
        return interno.historial(servicio);
    }

    @Override
    public Set<Servicio> servicios() {
        return interno.servicios();
    }

    /** Cierra el flujo: los suscriptores reciben {@code onComplete} cuando hayan procesado todo. */
    @Override
    public void close() {
        publicador.close();
    }
}
