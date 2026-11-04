// DemoValorConAmbito.java
package es.uib.prgava.tema3.virtuales;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Un identificador de ronda que llega hasta el código que escribe en el registro sin pasarlo como
 * parámetro por todos los métodos intermedios.
 */
public final class DemoValorConAmbito {

    private DemoValorConAmbito() { }

    private static final ScopedValue<Integer> RONDA = ScopedValue.newInstance();

    /** Muy por debajo en la pila de llamadas: no recibe la ronda, la lee. */
    private static void registrar(String mensaje) {
        String prefijo = RONDA.isBound() ? "[ronda " + RONDA.get() + "] " : "[fuera de ronda] ";
        System.out.println("   " + prefijo + mensaje);
    }

    private static void comprobar(String servicio) {
        registrar("compruebo " + servicio);
    }

    public static void main(String[] args) {
        for (int numero = 1; numero <= 2; numero++) {
            ScopedValue.where(RONDA, numero).run(() -> {
                comprobar("host-1.uib.es");
                comprobar("host-2.uib.es");
            });
        }
        registrar("esto ya no esta dentro de ninguna ronda");

        try (ExecutorService ejecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            ScopedValue.where(RONDA, 3).run(() -> ejecutor.submit(() -> registrar("desde otro hilo")));
        }
    }
}
