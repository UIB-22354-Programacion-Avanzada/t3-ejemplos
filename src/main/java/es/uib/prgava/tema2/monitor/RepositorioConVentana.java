// RepositorioConVentana.java
package es.uib.prgava.tema2.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;

/**
 * Repositorio en memoria con dos índices: el historial completo por servicio, para los informes, y
 * una ventana de los últimos resultados por servicio, para la política de alerta.
 *
 * <p>Las dos operaciones que se ejecutan una vez por comprobación —{@code guardar} y
 * {@code ultimos}— cuestan tiempo constante y no crecen con el número de rondas.
 */
public final class RepositorioConVentana implements RepositorioResultados {

    private final int capacidadVentana;
    private final Map<Servicio, List<Resultado>> porServicio = new HashMap<>();
    private final Map<Servicio, VentanaDeslizante> ventanas = new HashMap<>();

    public RepositorioConVentana(int capacidadVentana) {
        if (capacidadVentana < 1) {
            throw new IllegalArgumentException("Capacidad no positiva: " + capacidadVentana);
        }
        this.capacidadVentana = capacidadVentana;
    }

    /** Coste O(1) esperado y amortizado, en las dos estructuras. */
    @Override
    public void guardar(Resultado resultado) {
        var servicio = resultado.servicio();
        porServicio.computeIfAbsent(servicio, s -> new ArrayList<>()).add(resultado);
        ventanas.computeIfAbsent(servicio, s -> new VentanaDeslizante(capacidadVentana))
                .agregar(resultado);
    }

    /**
     * Lo que la política de alerta necesita para decidir: los últimos resultados del servicio.
     *
     * <p>Coste constante respecto al número de comprobaciones realizadas.
     */
    public List<Resultado> ultimos(Servicio servicio) {
        var ventana = ventanas.get(servicio);
        return ventana == null ? List.of() : ventana.contenido();
    }

    /** El historial completo, para los informes. Coste O(k) en los k resultados del servicio. */
    @Override
    public List<Resultado> historial(Servicio servicio) {
        return List.copyOf(porServicio.getOrDefault(servicio, List.of()));
    }

    @Override
    public Set<Servicio> servicios() {
        return Set.copyOf(porServicio.keySet());
    }
}
