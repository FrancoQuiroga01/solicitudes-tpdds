package ar.edu.utn.dds.k3003.model.Repository;


import ar.edu.utn.dds.k3003.model.Model.Solicitud;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("!test")
public interface JpaSolicitudRepository extends JpaRepository<Solicitud, String> {

    List<Solicitud> findByHechoId(String hechoId);
}
