// Resultado.java
package es.uib.prgava.tema1.monitor;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public record Resultado(Servicio servicio, Instant instante, Estado estado, Duration latencia) {

    public Resultado {
        Objects.requireNonNull(servicio, "servicio");
        Objects.requireNonNull(instante, "instante");
        Objects.requireNonNull(estado, "estado");
        Objects.requireNonNull(latencia, "latencia");
        if (latencia.isNegative()) {
            throw new IllegalArgumentException("Latencia negativa: " + latencia);
        }
    }

    public static Resultado activo(Servicio servicio, Duration latencia) {
        return new Resultado(servicio, Instant.now(), Estado.ACTIVO, latencia);
    }

    public static Resultado degradado(Servicio servicio, Duration latencia) {
        return new Resultado(servicio, Instant.now(), Estado.DEGRADADO, latencia);
    }

    public static Resultado caido(Servicio servicio) {
        return new Resultado(servicio, Instant.now(), Estado.CAIDO, Duration.ZERO);
    }

    public boolean esFallo() {
        return estado == Estado.CAIDO;
    }
}
