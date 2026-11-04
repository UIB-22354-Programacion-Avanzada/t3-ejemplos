// RepositorioConcurrente.java
package es.uib.prgava.tema3.monitor;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * El índice por servicio del Tema 2, hecho con estructuras concurrentes.
 *
 * <p>No hay ni un {@code synchronized}: el mapa es un {@code ConcurrentHashMap}, cuyo
 * {@code computeIfAbsent} es atómico, y la lista de cada servicio es una cola concurrente que admite
 * añadir desde varios hilos a la vez. Dos hilos que guardan resultados de servicios distintos no se
 * esperan nunca.
 */
public final class RepositorioConcurrente implements RepositorioResultados {

    private final ConcurrentMap<Servicio, Queue<Resultado>> porServicio = new ConcurrentHashMap<>();

    @Override
    public void guardar(Resultado resultado) {
        porServicio.computeIfAbsent(resultado.servicio(), s -> new ConcurrentLinkedQueue<>())
                .add(resultado);
    }

    @Override
    public List<Resultado> historial(Servicio servicio) {
        Queue<Resultado> suyos = porServicio.get(servicio);
        return suyos == null ? List.of() : List.copyOf(suyos);
    }

    @Override
    public Set<Servicio> servicios() {
        return Set.copyOf(porServicio.keySet());
    }
}
