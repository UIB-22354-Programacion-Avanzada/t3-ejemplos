// Estado.java
package es.uib.prgava.tema1.monitor;

public enum Estado {
    ACTIVO,      // responde dentro del tiempo esperado
    DEGRADADO,   // responde, pero con latencia por encima del umbral
    CAIDO        // no responde
}
