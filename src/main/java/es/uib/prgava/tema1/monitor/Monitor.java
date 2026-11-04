// Monitor.java (con observadores)
package es.uib.prgava.tema1.monitor;

import java.util.ArrayList;
import java.util.List;

public final class Monitor {
    private final Sonda sonda;
    private final PoliticaDeAlerta politica;
    private final RepositorioResultados repositorio;
    private final List<Notificador> notificadores = new ArrayList<>();

    public Monitor(Sonda sonda, PoliticaDeAlerta politica, RepositorioResultados repositorio) {
        this.sonda = sonda;
        this.politica = politica;
        this.repositorio = repositorio;
    }

    public void suscribir(Notificador notificador) {
        notificadores.add(notificador);
    }

    public void desuscribir(Notificador notificador) {
        notificadores.remove(notificador);
    }

    public void comprobar(List<Servicio> servicios) {
        for (var servicio : servicios) {
            comprobar(servicio);
        }
    }

    public Resultado comprobar(Servicio servicio) {
        var resultado = sonda.sondear(servicio);
        repositorio.guardar(resultado);
        if (politica.debeAlertar(repositorio.historial(servicio))) {
            var alerta = Alerta.ahora(servicio, "condición: " + politica);
            for (var notificador : notificadores) {
                notificador.notificar(alerta);
            }
        }
        return resultado;
    }
}
