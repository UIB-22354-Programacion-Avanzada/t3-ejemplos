// PoliticaDeAlerta.java
package es.uib.prgava.tema1.monitor;

import java.util.List;

@FunctionalInterface
public interface PoliticaDeAlerta {
    /** Decide si el historial reciente de un servicio (del más antiguo al más nuevo) justifica una alerta. */
    boolean debeAlertar(List<Resultado> historial);
}
