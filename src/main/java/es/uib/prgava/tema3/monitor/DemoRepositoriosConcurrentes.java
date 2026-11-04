// DemoRepositoriosConcurrentes.java
package es.uib.prgava.tema3.monitor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import es.uib.prgava.tema1.monitor.Estado;
import es.uib.prgava.tema1.monitor.RepositorioResultados;
import es.uib.prgava.tema1.monitor.Resultado;
import es.uib.prgava.tema1.monitor.Servicio;
import es.uib.prgava.tema2.monitor.RepositorioConVentana;
import es.uib.prgava.tema3.herramientas.RepositorioComprobarYActuar;

/**
 * La prueba de esfuerzo de 3.1 sobre tres repositorios, repetida veinte veces cada uno: lo que
 * interesa del error de comprobar y actuar no es si aparece, sino con qué frecuencia.
 */
public final class DemoRepositoriosConcurrentes {

    private DemoRepositoriosConcurrentes() { }

    private static final int HILOS = 8;
    private static final int POR_HILO = 20_000;
    private static final int REPETICIONES = 20;

    /** Resultados que faltan al terminar una prueba de esfuerzo sobre {@code repositorio}. */
    private static long perdidos(RepositorioResultados repositorio)
            throws InterruptedException, ExecutionException {
        List<Servicio> servicios = Escenario.servicios(50);
        try (ExecutorService ejecutor = Executors.newFixedThreadPool(HILOS)) {
            List<Future<?>> futuros = new ArrayList<>();
            for (int h = 0; h < HILOS; h++) {
                final int desplazamiento = h;
                futuros.add(ejecutor.submit(() -> {
                    for (int k = 0; k < POR_HILO; k++) {
                        Servicio servicio = servicios.get((k + desplazamiento) % servicios.size());
                        repositorio.guardar(new Resultado(servicio, Instant.EPOCH, Estado.ACTIVO, Duration.ZERO));
                    }
                }));
            }
            for (Future<?> futuro : futuros) {
                futuro.get();
            }
        }
        long guardados = 0;
        for (Servicio servicio : repositorio.servicios()) {
            guardados += repositorio.historial(servicio).size();
        }
        return (long) HILOS * POR_HILO - guardados;
    }

    private static void medir(String nombre, java.util.function.Supplier<RepositorioResultados> fabrica)
            throws InterruptedException, ExecutionException {
        long conPerdidas = 0;
        long totalPerdidos = 0;
        long inicio = System.nanoTime();
        for (int i = 0; i < REPETICIONES; i++) {
            long p = perdidos(fabrica.get());
            if (p > 0) {
                conPerdidas++;
                totalPerdidos += p;
            }
        }
        long milis = (System.nanoTime() - inicio) / 1_000_000;
        System.out.printf("   %-30s %2d de %d con perdidas, %4d perdidos en total, %5d ms%n",
                nombre, conPerdidas, REPETICIONES, totalPerdidos, milis);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        medir("RepositorioSincronizado:", () -> new RepositorioSincronizado(new RepositorioConVentana(10)));
        medir("RepositorioConcurrente:", RepositorioConcurrente::new);
        medir("RepositorioComprobarYActuar:", RepositorioComprobarYActuar::new);
    }
}
