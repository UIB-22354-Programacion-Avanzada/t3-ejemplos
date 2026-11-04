// ServicioDns.java
package es.uib.prgava.tema1.monitor;

public record ServicioDns(String dominio) implements Servicio {
    public ServicioDns {
        if (dominio == null || dominio.isBlank()) {
            throw new IllegalArgumentException("Dominio DNS vacío");
        }
        dominio = dominio.strip().toLowerCase();
    }

    @Override
    public String nombre() { return "dns:" + dominio; }
}
