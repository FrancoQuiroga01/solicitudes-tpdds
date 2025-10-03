package ar.edu.utn.dds.k3003.model.clients;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Component;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class FuentesProxy implements FachadaFuente {

    private final FuentesRetrofitClient service;
    private final String baseUrl;

    public FuentesProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        String raw = System.getenv().getOrDefault(
                "URL_FUENTES",
                "https://two025-tp-entrega-2-bcamposgh.onrender.com"
        );
        this.baseUrl = raw.endsWith("/") ? raw : raw + "/";


        // Logging HTTP básico (request/response + bodies)
        var logging = new HttpLoggingInterceptor(System.out::println);
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        var http = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(10))
                .build();

        var retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .client(http)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        this.service = retrofit.create(FuentesRetrofitClient.class);
    }

    @Override
    public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
        return null;
    }

    @Override
    public ColeccionDTO buscarColeccionXId(String nombre) throws NoSuchElementException {

        try {
            Response<ColeccionDTO> resp = service.get(nombre).execute();
            if (resp.isSuccessful() && resp.body() != null) {
                return resp.body();
            }
            if (resp.code() == 404) {
                throw new NoSuchElementException("Colección no encontrada: " + nombre);
            }
            throw new RuntimeException("Fuentes respondió HTTP " + resp.code());
        } catch (IOException e) {
            throw new RuntimeException("Error de red conectando a Fuentes", e);
        }
    }

    @Override
    public List<ColeccionDTO> colecciones() {
        try {
            Response<List<ColeccionDTO>> resp = service.getAll().execute();
            if (resp.isSuccessful() && resp.body() != null) {
                return resp.body();
            }
            throw new RuntimeException("Error al obtener lista de colecciones, HTTP " + resp.code());
        } catch (IOException e) {
            throw new RuntimeException("Error de red al conectar con Fuentes", e);
        }
    }

    @Override
    public HechoDTO agregar(HechoDTO hechoDTO) {
        return null;
    }

    @Override
    public HechoDTO buscarHechoXId(String hechoId) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<HechoDTO> buscarHechosXColeccion(String coleccionId) throws NoSuchElementException {
        return List.of();
    }

    public void setProcesadorPdI(FachadaProcesadorPdI procesador) {

    }

    @Override
    public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {
        return null;
    }

}