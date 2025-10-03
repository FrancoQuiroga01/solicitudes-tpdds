package ar.edu.utn.dds.k3003.model.app;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.Model.Solicitud;
import ar.edu.utn.dds.k3003.model.Repository.JpaSolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class Fachada implements FachadaSolicitudes {

    private final JpaSolicitudRepository solicitudRepository;
    private final FachadaFuente fachadaFuente;

    public Fachada(JpaSolicitudRepository solicitudRepository, FachadaFuente fachadaFuente) {
        this.solicitudRepository = solicitudRepository;
        this.fachadaFuente = fachadaFuente;
    }

    @Override
    @Transactional
    public SolicitudDTO agregar(SolicitudDTO dto) {
        if (dto.hechoId() == null || dto.hechoId().isBlank()) {
            throw new IllegalArgumentException("El hechoId no puede ser nulo o vacío.");
        }
        if (dto.descripcion() == null || dto.descripcion().trim().length() < 500) {
            throw new IllegalArgumentException("La descripción debe tener al menos 500 caracteres.");
        }

        //reviso en Fuentes que el hecho exista. si existe entonces esta activo
        try {
            HechoDTO hecho = fachadaFuente.buscarHechoXId(dto.hechoId());
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("El hecho_id no existe en Fuentes: " + dto.hechoId());
        }

        Solicitud solicitud = new Solicitud(dto.hechoId(), dto.descripcion().trim());
        solicitudRepository.save(solicitud);
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

        if (estado != null) {
            solicitud.setEstado(estado);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            solicitud.setDescripcion(descripcion.trim());
        }

        solicitudRepository.save(solicitud);
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
