// DemoViaje.java
package es.uib.prgava.tema3.virtuales;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;

/** El presupuesto de un viaje: tres consultas a la vez, y si una falla, ninguna sirve. */
public final class DemoViaje {

    private DemoViaje() { }

    /** Simula la consulta a un proveedor: tarda {@code milis} y devuelve un precio, o falla. */
    private static int consultar(String que, long milis, int precio, boolean falla)
            throws InterruptedException {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            System.out.println("   " + que + ": cancelada");
            throw e;
        }
        if (falla) {
            System.out.println("   " + que + ": falla");
            throw new IllegalStateException("no queda " + que + " disponible");
        }
        System.out.println("   " + que + ": " + precio + " euros");
        return precio;
    }

    private static void presupuestar(boolean fallaHotel) throws InterruptedException {
        long inicio = System.nanoTime();
        try (var ambito = StructuredTaskScope.open(Joiner.<Integer>allSuccessfulOrThrow())) {
            Subtask<Integer> vuelo = ambito.fork(() -> consultar("vuelo", 300, 180, false));
            Subtask<Integer> hotel = ambito.fork(() -> consultar("hotel", 100, 240, fallaHotel));
            Subtask<Integer> coche = ambito.fork(() -> consultar("coche", 200, 90, false));
            ambito.join();
            System.out.println("   total: " + (vuelo.get() + hotel.get() + coche.get()) + " euros");
        } catch (StructuredTaskScope.FailedException e) {
            System.out.println("   sin presupuesto: " + e.getCause().getMessage());
        }
        System.out.println("   en unos " + (System.nanoTime() - inicio) / 1_000_000 + " ms");
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- los tres proveedores contestan");
        presupuestar(false);
        System.out.println("--- el hotel falla a los 100 ms");
        presupuestar(true);
    }
}
