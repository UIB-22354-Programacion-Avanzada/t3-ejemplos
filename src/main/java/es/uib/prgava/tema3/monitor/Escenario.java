// Escenario.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema1.monitor.ServicioTcp;

/**
 * Los datos de los experimentos del tema: {@code n} servicios con latencias fijas entre 50 y 250
 * ms, de los que uno de cada diez está caído.
 *
 * <p>Las latencias se calculan a partir de la posición del servicio, no al azar, para que dos
 * ejecuciones del mismo experimento tarden lo mismo.
 */
public final class Escenario {

    private Escenario() { }

    public static final Duration UMBRAL = Duration.ofMillis(200);
    public static final Duration TIEMPO_DE_ESPERA = Duration.ofSeconds(1);

    public static List<Servicio> servicios(int n) {
        var lista = new ArrayList<Servicio>();
        for (int i = 0; i < n; i++) {
            lista.add(new ServicioTcp("host-" + i + ".uib.es", 443));
        }
        return List.copyOf(lista);
    }

    /** Latencia del servicio {@code i}; los múltiplos de diez no tienen, porque están caídos. */
    public static Map<Servicio, Duration> latencias(List<Servicio> servicios) {
        var mapa = new HashMap<Servicio, Duration>();
        for (int i = 0; i < servicios.size(); i++) {
            if (i % 10 != 0) {
                mapa.put(servicios.get(i), Duration.ofMillis(50 + (i * 37) % 200));
            }
        }
        return mapa;
    }

    public static SondaLenta sonda(List<Servicio> servicios) {
        return new SondaLenta(UMBRAL, TIEMPO_DE_ESPERA, latencias(servicios));
    }
}
