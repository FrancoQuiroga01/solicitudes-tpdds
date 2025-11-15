package ar.edu.utn.dds.k3003.model.app;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.Model.Solicitud;
import ar.edu.utn.dds.k3003.model.Repository.JpaSolicitudRepository;
import ar.edu.utn.dds.k3003.model.clients.BuscadorProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.DistributionSummary;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class Fachada implements FachadaSolicitudes {

    private final JpaSolicitudRepository solicitudRepository;
    private final FachadaFuente fachadaFuente;
    private final BuscadorProxy buscadorProxy;


    // --- Métricas (campos) ---
    private Counter creadasCounter;
    private Counter errorHechoNoExisteCounter;
    private Timer crearTimer;
    private DistributionSummary descripcionLen;


    @Autowired
    void bindMeters(MeterRegistry registry) {
        this.creadasCounter = Counter.builder("solicitudes.creadas")
                .description("Solicitudes creadas")
                .tag("estado", "CREADA")
                .register(registry);

        this.errorHechoNoExisteCounter = Counter.builder("solicitudes.error")
                .description("Errores al crear solicitudes")
                .tag("tipo", "HECHO_NO_EXISTE")
                .register(registry);

        this.crearTimer = Timer.builder("solicitudes.crear.latencia")
                .description("Latencia de POST /solicitudes")
                .publishPercentileHistogram()
                .register(registry);

        this.descripcionLen = DistributionSummary.builder("solicitudes.descripcion.length")
                .description("Largo de la descripción al crear")
                .publishPercentileHistogram()
                .register(registry);
    }

    public Fachada(JpaSolicitudRepository solicitudRepository, FachadaFuente fachadaFuente, BuscadorProxy buscadorProxy) {
        this.solicitudRepository = solicitudRepository;
        this.fachadaFuente = fachadaFuente;
        this.buscadorProxy = buscadorProxy;
    }

    @Override
    @Transactional
    public SolicitudDTO agregar(SolicitudDTO dto) {
        final Timer.Sample sample = Timer.start();
        if (dto.hechoId() == null || dto.hechoId().isBlank()) {
            throw new IllegalArgumentException("El hechoId no puede ser nulo o vacío.");
        }
        if (dto.descripcion() == null || dto.descripcion().trim().length() < 500) {
            throw new IllegalArgumentException("La descripción debe tener al menos 500 caracteres.");
        }
        descripcionLen.record(dto.descripcion().trim().length());
        try {
            HechoDTO hecho = fachadaFuente.buscarHechoXId(dto.hechoId());
        } catch (NoSuchElementException e) {
            errorHechoNoExisteCounter.increment();
            throw new IllegalArgumentException("El hecho_id no existe en Fuentes: " + dto.hechoId());
        }

        Solicitud solicitud = new Solicitud(dto.hechoId(), dto.descripcion().trim());
        solicitudRepository.save(solicitud);
        creadasCounter.increment();
        sample.stop(crearTimer);
        return solicitud.toDTO();
    }

    @Override
    @Transactional
    public SolicitudDTO modificar(String solicitudId, EstadoSolicitudBorradoEnum estado, String descripcion) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada"));

        if (descripcion != null && !descripcion.isBlank() && descripcion.trim().length() < 500) {
            throw new IllegalArgumentException("La descripción debe tener al menos 500 caracteres.");
        }

        EstadoSolicitudBorradoEnum estadoAnterior = solicitud.getEstado();

        if (estado != null) {
            solicitud.setEstado(estado);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            solicitud.setDescripcion(descripcion.trim());
        }

        solicitudRepository.save(solicitud);

        if (estadoAnterior != EstadoSolicitudBorradoEnum.ACEPTADA &&
                solicitud.getEstado() == EstadoSolicitudBorradoEnum.ACEPTADA) {

            buscadorProxy.ocultarHecho(solicitud.getHechoId());
        }
        return solicitud.toDTO();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudDTO> buscarSolicitudXHecho(String hechoId) {
        return solicitudRepository.findByHechoId(hechoId).stream()
                .map(Solicitud::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudDTO buscarSolicitudXId(String solicitudId) {
        return solicitudRepository.findById(solicitudId)
                .map(Solicitud::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaActivo(String hechoId) {
        return solicitudRepository.findByHechoId(hechoId).stream()
                .noneMatch(s -> s.getEstado() == EstadoSolicitudBorradoEnum.ACEPTADA);
    }

    public boolean noTieneSolicitudes(String hechoId) {
        return solicitudRepository.findByHechoId(hechoId).isEmpty();
    }

    @Transactional(readOnly = true)
    public List<String> hechosElegibles(List<String> hechoIds) {
        if (hechoIds == null || hechoIds.isEmpty()) return List.of();
        return hechoIds.stream()
                .filter(this::noTieneSolicitudes)
                .toList();
    }
    @Override
    public void setFachadaFuente(FachadaFuente fachadaFuente) {

    }
}
