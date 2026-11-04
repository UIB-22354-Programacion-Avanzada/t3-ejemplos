// Sonda.java
package es.uib.prgava.tema1.monitor;

public interface Sonda {
    Resultado sondear(Servicio servicio);

    default boolean disponible(Servicio servicio) {
        return sondear(servicio).estado() != Estado.CAIDO;
    }
}
