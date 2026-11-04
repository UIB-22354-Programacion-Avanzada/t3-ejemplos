// RepositorioComprobarYActuar.java
package es.uib.prgava.tema3.herramientas;

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
 * Las mismas estructuras concurrentes que {@code RepositorioConcurrente}, y un error: el método
 * {@code guardar} comprueba y después actúa en dos pasos separados.
 */
public final class RepositorioComprobarYActuar implements RepositorioResultados {

    private final ConcurrentMap<Servicio, Queue<Resultado>> porServicio = new ConcurrentHashMap<>();

    @Override
    public void guardar(Resultado resultado) {
        Servicio servicio = resultado.servicio();
        if (!porServicio.containsKey(servicio)) {                   // comprobar
            porServicio.put(servicio, new ConcurrentLinkedQueue<>());  // actuar
        }
        porServicio.get(servicio).add(resultado);
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
