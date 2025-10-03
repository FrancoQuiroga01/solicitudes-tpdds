package ar.edu.utn.dds.k3003.model.clients;

import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface FuentesRetrofitClient {
    @GET("api/colecciones/{nombre}")
    Call<ColeccionDTO> get(@Path("nombre") String nombre);

    @GET("api/colecciones")
    Call<List<ColeccionDTO>> getAll();
}
