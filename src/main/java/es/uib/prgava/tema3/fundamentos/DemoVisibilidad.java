// DemoVisibilidad.java
package es.uib.prgava.tema3.fundamentos;

/**
 * Un hilo da vueltas hasta que otro le pide que pare cambiando un atributo. Sin sincronización
 * ninguna, el aviso puede no llegar nunca.
 */
public final class DemoVisibilidad {

    private DemoVisibilidad() { }

    private static boolean parar = false;

    public static void main(String[] args) throws InterruptedException {
        Thread trabajador = new Thread(() -> {
            long vueltas = 0;
            while (!parar) {
                vueltas++;
            }
            System.out.println("   el trabajador ha visto el aviso tras " + vueltas + " vueltas");
        });
        trabajador.setDaemon(true);
        trabajador.start();

        Thread.sleep(500);
        parar = true;
        System.out.println("   main: aviso enviado");

        trabajador.join(3_000);
        System.out.println("   tres segundos despues, el trabajador sigue vivo: " + trabajador.isAlive());
    }
}
