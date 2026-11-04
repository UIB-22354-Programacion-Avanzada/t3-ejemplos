// Alerta.java
package es.uib.prgava.tema1.monitor;

import java.time.Instant;
import java.util.Objects;

public record Alerta(Servicio servicio, String mensaje, Instant instante) {
    public Alerta {
        Objects.requireNonNull(servicio);
        Objects.requireNonNull(mensaje);
        Objects.requireNonNull(instante);
    }

    public static Alerta ahora(Servicio servicio, String mensaje) {
        return new Alerta(servicio, mensaje, Instant.now());
    }
}
