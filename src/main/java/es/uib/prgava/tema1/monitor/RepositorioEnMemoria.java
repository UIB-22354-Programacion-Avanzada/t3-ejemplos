// RepositorioEnMemoria.java
package es.uib.prgava.tema1.monitor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class RepositorioEnMemoria implements RepositorioResultados {
    private final List<Resultado> datos = new ArrayList<>();

    @Override
    public void guardar(Resultado resultado) {
        datos.add(resultado);
    }

    @Override
    public List<Resultado> historial(Servicio servicio) {
        var suyos = new ArrayList<Resultado>();
        for (var resultado : datos) {
            if (resultado.servicio().equals(servicio)) {
                suyos.add(resultado);
            }
        }
        return List.copyOf(suyos);
    }

    @Override
    public Set<Servicio> servicios() {
        var vistos = new LinkedHashSet<Servicio>();
        for (var resultado : datos) {
            vistos.add(resultado.servicio());
        }
        return Set.copyOf(vistos);
    }
}
