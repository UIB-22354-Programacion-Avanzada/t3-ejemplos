// Servicio.java (con fábrica estática)
package es.uib.prgava.tema1.monitor;

import java.net.URI;

public interface Servicio {

    String nombre();

    /**
     * Crea un servicio a partir de su forma textual:
     * {@code http://...}, {@code https://...}, {@code dns:dominio} o {@code tcp:host:puerto}.
     * @throws IllegalArgumentException si el texto no describe un servicio válido
     */
    static Servicio desde(String texto) {
        var t = texto.strip();
        if (t.startsWith("http://") || t.startsWith("https://")) {
            return new ServicioHttp(URI.create(t));
        }
        if (t.startsWith("dns:")) {
            return new ServicioDns(t.substring(4));
        }
        if (t.startsWith("tcp:")) {
            var partes = t.substring(4).split(":");
            if (partes.length != 2) {
                throw new IllegalArgumentException("se esperaba tcp:host:puerto, no " + t);
            }
            return new ServicioTcp(partes[0], Integer.parseInt(partes[1]));
        }
        throw new IllegalArgumentException("tipo de servicio desconocido: " + t);
    }
}
