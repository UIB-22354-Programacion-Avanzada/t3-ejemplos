// VentanaDeslizante.java
package es.uib.prgava.tema2.monitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import es.uib.prgava.tema1.monitor.Resultado;

/**
 * Los últimos {@code capacidad} resultados de <strong>un</strong> servicio, en orden de llegada.
 *
 * <p>Añadir y consultar cuestan tiempo constante, y la memoria ocupada no depende del número de
 * comprobaciones realizadas, sino solo de la capacidad.
 */
public final class VentanaDeslizante {

    private final int capacidad;
    private final Deque<Resultado> ultimos = new ArrayDeque<>();

    public VentanaDeslizante(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("Capacidad no positiva: " + capacidad);
        }
        this.capacidad = capacidad;
    }

    /** Añade un resultado y, si la ventana estaba llena, olvida el más antiguo. Coste O(1). */
    public void agregar(Resultado resultado) {
        if (ultimos.size() == capacidad) {
            ultimos.removeFirst();
        }
        ultimos.addLast(resultado);
    }

    /** Los resultados de la ventana, del más antiguo al más reciente. Coste O(capacidad). */
    public List<Resultado> contenido() {
        return List.copyOf(ultimos);
    }

    public int cuantos() {
        return ultimos.size();
    }
}
