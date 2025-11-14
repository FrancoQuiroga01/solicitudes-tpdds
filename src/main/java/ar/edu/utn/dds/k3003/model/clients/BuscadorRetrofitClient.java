package ar.edu.utn.dds.k3003.model.clients;

import retrofit2.Call;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

public interface BuscadorRetrofitClient {

    @PATCH("/api/buscador/ocultar/hecho/{hechoId}")
    Call<Void> ocultarHecho(@Path("hechoId") String hechoId);
}
