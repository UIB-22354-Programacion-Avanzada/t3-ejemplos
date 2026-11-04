// LectorResultados.java
package es.uib.prgava.tema1.monitor;

import java.util.List;
import java.util.Set;

public interface LectorResultados {
    /** Historial de un servicio, del más antiguo al más reciente. */
    List<Resultado> historial(Servicio servicio);

    /** Servicios con al menos un resultado registrado. */
    Set<Servicio> servicios();
}
