package ar.edu.utn.dds.k3003.model.config;

import ar.edu.utn.dds.k3003.model.clients.BuscadorRetrofitClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BuscadorConfig {

    private final BuscadorRetrofitClient client;

    public BuscadorConfig(String urlBase) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlBase)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        client = retrofit.create(BuscadorRetrofitClient.class);
    }

    public BuscadorRetrofitClient getClient() {
        return client;
    }
}