package ar.edu.utn.dds.k3003.model.Repository;

import ar.edu.utn.dds.k3003.model.Model.Solicitud;

import java.util.List;
import java.util.Optional;

public interface SolicitudRepository{

    void guardarSolicitud(Solicitud solicitud);
    Optional<Solicitud> findById(String id);
    List<Solicitud> findByHechoId(String hechoId);
}
