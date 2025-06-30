package ar.edu.utn.dds.k3003.model.Model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Solicitud {

    @Id
    private String solicitudId;
    private String hechoId;
    @Column(length = 4000)
    private String descripcion;


    @Enumerated(EnumType.STRING)
    private EstadoSolicitudBorradoEnum estado;

    public Solicitud(String hechoId, String descripcion) {
        this.solicitudId = UUID.randomUUID().toString();
        this.hechoId = hechoId;
        this.descripcion = descripcion;
        this.estado = EstadoSolicitudBorradoEnum.CREADA;
    }

    public Solicitud() {

    }

    public String getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(String solicitudId) {
        this.solicitudId = solicitudId;
    }

    public String getHechoId() {
        return hechoId;
    }

    public void setHechoId(String hechoId) {
        this.hechoId = hechoId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoSolicitudBorradoEnum getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudBorradoEnum estado) {
        this.estado = estado;
    }

    public SolicitudDTO toDTO() {
        return new SolicitudDTO(solicitudId,descripcion,estado,hechoId);
    }
}
