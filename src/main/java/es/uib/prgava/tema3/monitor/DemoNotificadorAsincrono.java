// DemoNotificadorAsincrono.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.uib.prgava.tema1.monitor.Alerta;
import es.uib.prgava.tema1.monitor.FallosConsecutivos;
import es.uib.prgava.tema1.monitor.Monitor;
import es.uib.prgava.tema1.monitor.Notificador;
import es.uib.prgava.tema2.monitor.RepositorioConVentana;

/**
 * La misma ronda con un notificador lento, primero directo y después a través del notificador
 * asíncrono. Con la política de un fallo, los dos servicios caídos alertan en cada ronda.
 */
public final class DemoNotificadorAsincrono {

    private DemoNotificadorAsincrono() { }

    /** Un notificador que tarda medio segundo en cada envío, como un servidor de correo. */
    private static final Notificador CORREO_LENTO = alerta -> {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("      correo enviado: " + alerta.servicio().nombre());
    };

    private static long ronda(Notificador notificador) throws InterruptedException {
        var servicios = Escenario.servicios(20);
        var monitor = new Monitor(Escenario.sonda(servicios), new FallosConsecutivos(1),
                new RepositorioSincronizado(new RepositorioConVentana(10)));
        monitor.suscribir(notificador);
        try (ExecutorService ejecutor = Executors.newFixedThreadPool(20)) {
            long inicio = System.nanoTime();
            new RondaConEjecutor(monitor, ejecutor).ejecutar(servicios, Duration.ofSeconds(10));
            return (System.nanoTime() - inicio) / 1_000_000;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("   --- con el correo lento directamente");
        System.out.println("   la ronda tarda " + ronda(CORREO_LENTO) + " ms");

        System.out.println("   --- con el correo lento detras del notificador asincrono");
        try (var asincrono = new NotificadorAsincrono(CORREO_LENTO)) {
            System.out.println("   la ronda tarda " + ronda(asincrono) + " ms");
            System.out.println("   y los correos salen despues:");
        }
        System.out.println("   cerrado: no queda nada pendiente");
    }
}
