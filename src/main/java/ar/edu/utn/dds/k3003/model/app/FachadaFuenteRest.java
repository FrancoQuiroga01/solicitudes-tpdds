package ar.edu.utn.dds.k3003.model.app;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Primary
public class FachadaFuenteRest implements FachadaFuente {
    private final RestTemplate restTemplate;
    private final String baseUrl = "https://tpa-ddsi-2025-grupo-9-fuentes.onrender.com";

    public FachadaFuenteRest() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public ColeccionDTO agregar(ColeccionDTO coleccionDTO) {
        return null;
    }

    @Override
    public ColeccionDTO buscarColeccionXId(String s) throws NoSuchElementException {
        return null;
    }

    @Override
    public HechoDTO agregar(HechoDTO hechoDTO) {
        return null;
    }

    @Override
    public HechoDTO buscarHechoXId(String id) throws NoSuchElementException {
        try {
            return restTemplate.getForObject(baseUrl + "/api/hecho/{id}", HechoDTO.class,id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoSuchElementException("No se encontró el hecho con id " + id);
        }
    }

    @Override
    public List<HechoDTO> buscarHechosXColeccion(String s) throws NoSuchElementException {
        return List.of();
    }

    @Override
    public void setProcesadorPdI(FachadaProcesadorPdI fachadaProcesadorPdI) {

    }

    @Override
    public PdIDTO agregar(PdIDTO pdIDTO) throws IllegalStateException {
        return null;
    }

    @Override
    public List<ColeccionDTO> colecciones() {
        return List.of();
    }
}
