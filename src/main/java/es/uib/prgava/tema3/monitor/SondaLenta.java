// SondaLenta.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema1.monitor.SondaBase;

/**
 * Una sonda que tarda lo que tardaría la red.
 *
 * <p>Cada servicio tiene una latencia fija, y la sonda <strong>espera</strong> ese tiempo antes de
 * devolver el resultado, igual que esperaría una sonda real a que el servidor contestara. Un
 * servicio sin latencia asignada se considera caído, y cuesta lo que cuesta descubrirlo en la
 * realidad: el tiempo de espera completo.
 *
 * <p>El mapa de latencias se copia al construir y no se modifica después, así que la sonda se puede
 * usar desde varios hilos a la vez sin protegerla.
 */
public final class SondaLenta extends SondaBase {

    private final Map<Servicio, Duration> latencias;
    private final Duration tiempoDeEspera;

    public SondaLenta(Duration umbral, Duration tiempoDeEspera, Map<Servicio, Duration> latencias) {
        super(umbral);
        this.tiempoDeEspera = Objects.requireNonNull(tiempoDeEspera);
        this.latencias = Map.copyOf(latencias);
    }

    @Override
    protected Optional<Duration> medir(Servicio servicio) {
        var latencia = latencias.get(servicio);
        try {
            if (latencia == null) {
                Thread.sleep(tiempoDeEspera);
                return Optional.empty();
            }
            Thread.sleep(latencia);
            return Optional.of(latencia);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
}
