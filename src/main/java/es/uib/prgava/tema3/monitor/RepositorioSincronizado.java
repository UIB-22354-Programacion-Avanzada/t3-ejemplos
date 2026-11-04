// RepositorioSincronizado.java
package es.uib.prgava.tema3.monitor;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * Decorador que hace seguro para varios hilos cualquier repositorio.
 *
 * <p>Los tres métodos son {@code synchronized} sobre este objeto, así que nunca hay dos hilos
 * dentro del repositorio interno a la vez. Funciona porque {@code historial} y {@code servicios}
 * devuelven <strong>copias</strong>: lo que sale de aquí ya no está compartido. Si devolvieran una
 * vista de la colección interna, quien la recorriera fuera del cerrojo volvería a tener una carrera.
 */
public final class RepositorioSincronizado implements RepositorioResultados {

    private final RepositorioResultados interno;

    public RepositorioSincronizado(RepositorioResultados interno) {
        this.interno = Objects.requireNonNull(interno);
    }

    @Override
    public synchronized void guardar(Resultado resultado) {
        interno.guardar(resultado);
    }

    @Override
    public synchronized List<Resultado> historial(Servicio servicio) {
        return interno.historial(servicio);
    }

    @Override
    public synchronized Set<Servicio> servicios() {
        return interno.servicios();
    }
}
