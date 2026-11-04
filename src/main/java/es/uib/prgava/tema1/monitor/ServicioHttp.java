// ServicioHttp.java
package es.uib.prgava.tema1.monitor;

import java.net.URI;

public record ServicioHttp(URI url) implements Servicio {
    public ServicioHttp {
        if (url == null || url.getHost() == null) {
            throw new IllegalArgumentException("URL HTTP inválida: " + url);
        }
    }

    @Override
    public String nombre() { return url.toString(); }
}
