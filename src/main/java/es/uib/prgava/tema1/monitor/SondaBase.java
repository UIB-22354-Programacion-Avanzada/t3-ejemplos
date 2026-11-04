// SondaBase.java
package es.uib.prgava.tema1.monitor;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public abstract class SondaBase implements Sonda {
    private final Duration umbral;

    protected SondaBase(Duration umbral) {
        this.umbral = Objects.requireNonNull(umbral);
    }

    /** Método plantilla: fija el algoritmo; las subclases solo implementan {@link #medir}. */
    @Override
    public final Resultado sondear(Servicio servicio) {
        var latencia = medir(servicio);
        if (latencia.isEmpty()) {
            return Resultado.caido(servicio);
        }
        return clasificar(servicio, latencia.get());
    }

    /** Paso variable: latencia observada, o vacío si el servicio no responde. */
    protected abstract Optional<Duration> medir(Servicio servicio);

    /** Gancho con implementación por defecto; una subclase puede afinarlo. */
    protected Resultado clasificar(Servicio servicio, Duration latencia) {
        return latencia.compareTo(umbral) > 0
                ? Resultado.degradado(servicio, latencia)
                : Resultado.activo(servicio, latencia);
    }
}
