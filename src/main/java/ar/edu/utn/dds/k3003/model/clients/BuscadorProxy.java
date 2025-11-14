package ar.edu.utn.dds.k3003.model.clients;
import ar.edu.utn.dds.k3003.model.config.BuscadorConfig;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;


public class BuscadorProxy {

    private final BuscadorRetrofitClient client;

    public BuscadorProxy(@Value("${buscador.url}") String buscadorUrl) {
        this.client = new BuscadorConfig(buscadorUrl).getClient();
    }

    public void ocultarHecho(String hechoId) {
        try {
            client.ocultarHecho(hechoId).execute();
        } catch (Exception e) {
            // Loggear error: consistencia eventual
            System.err.println("Error notificando al Buscador: " + e.getMessage());
        }
    }
}