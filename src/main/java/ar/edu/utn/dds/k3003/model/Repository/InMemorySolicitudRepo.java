package ar.edu.utn.dds.k3003.model.Repository;

import ar.edu.utn.dds.k3003.model.Model.Solicitud;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("test")
public class InMemorySolicitudRepo implements SolicitudRepository {

    private List<Solicitud> solicitudes = new ArrayList<>();

    @Override
    public void guardarSolicitud(Solicitud solicitud) {
        this.deleteById(solicitud.getSolicitudId());
        solicitudes.add(solicitud);
    }

    @Override
    public Optional<Solicitud> findById(String id) {
        return solicitudes.stream()
                .filter(s -> s.getSolicitudId().equals(id))
                .findFirst();
    }

    @Override
    public List<Solicitud> findByHechoId(String hechoId) {
        return solicitudes.stream()
                .filter(s -> s.getHechoId().equals(hechoId))
                .collect(Collectors.toList());
    }

    private void deleteById(String id) {
        solicitudes.removeIf(s -> s.getSolicitudId().equals(id));
    }

}

