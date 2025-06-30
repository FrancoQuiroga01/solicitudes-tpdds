package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import ar.edu.utn.dds.k3003.model.Model.Solicitud;
import ar.edu.utn.dds.k3003.model.Repository.JpaSolicitudRepository;
import ar.edu.utn.dds.k3003.model.app.Fachada;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FachadaTest {

    @Mock
    private JpaSolicitudRepository solicitudRepository;

    @InjectMocks
    private Fachada fachada;

    private Solicitud solicitudEjemplo;

    @BeforeEach
    void init() {
        solicitudEjemplo = new Solicitud("hecho001", "a".repeat(500));
        solicitudEjemplo.setSolicitudId("solicitud-123");
    }

    @Test
    void agregarSolicitudValida() {
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> {
            Solicitud s = invocation.getArgument(0);
            s.setSolicitudId("solicitud-123");
            return s;
        });

        SolicitudDTO dto = new SolicitudDTO(null, "a".repeat(500), EstadoSolicitudBorradoEnum.CREADA, "hecho001");
        SolicitudDTO resultado = fachada.agregar(dto);

        assertNotNull(resultado.id());
        assertEquals("hecho001", resultado.hechoId());
        assertEquals(EstadoSolicitudBorradoEnum.CREADA, resultado.estado());
    }

    @Test
    void agregarSolicitudConDescripcionCortaFallaConExcepcion() {
        SolicitudDTO dto = new SolicitudDTO(null, "desc corta", EstadoSolicitudBorradoEnum.CREADA, "hecho002");

        assertThrows(IllegalArgumentException.class, () -> fachada.agregar(dto));
    }

    @Test
    void modificarSolicitudValida() {
        when(solicitudRepository.findById("solicitud-123")).thenReturn(Optional.of(solicitudEjemplo));
        when(solicitudRepository.save(any(Solicitud.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String nuevaDesc = "nueva descripción".repeat(50);
        SolicitudDTO modificada = fachada.modificar("solicitud-123", EstadoSolicitudBorradoEnum.ACEPTADA, nuevaDesc);

        assertEquals(EstadoSolicitudBorradoEnum.ACEPTADA, modificada.estado());
        assertTrue(modificada.descripcion().startsWith("nueva desc"));
    }

    @Test
    void modificarSolicitudInexistenteFallaConExcepcion() {
        when(solicitudRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
                fachada.modificar("inexistente", EstadoSolicitudBorradoEnum.RECHAZADA, "desc".repeat(100)));
    }

    @Test
    void buscarSolicitudXIdDevuelveSolicitudCorrecta() {
        when(solicitudRepository.findById("solicitud-123")).thenReturn(Optional.of(solicitudEjemplo));

        SolicitudDTO resultado = fachada.buscarSolicitudXId("solicitud-123");

        assertEquals("solicitud-123", resultado.id());
    }

    @Test
    void buscarSolicitudXHechoDevuelveListaCorrecta() {
        Solicitud s1 = new Solicitud("hecho100", "d".repeat(500));
        s1.setHechoId("id1");
        Solicitud s2 = new Solicitud("hecho100", "e".repeat(500));
        s2.setHechoId("id2");

        when(solicitudRepository.findByHechoId("hecho100")).thenReturn(List.of(s1, s2));

        List<SolicitudDTO> solicitudes = fachada.buscarSolicitudXHecho("hecho100");

        assertEquals(2, solicitudes.size());
    }

    @Test
    void estaActivoDevuelveFalseSiHayAceptada() {
        Solicitud s = new Solicitud("hechoCensurado", "f".repeat(500));
        s.setEstado(EstadoSolicitudBorradoEnum.ACEPTADA);
        s.setHechoId("idAceptada");

        when(solicitudRepository.findByHechoId("hechoCensurado")).thenReturn(List.of(s));

        assertFalse(fachada.estaActivo("hechoCensurado"));
    }

    @Test
    void estaActivoDevuelveTrueSiNoHayAceptadas() {
        Solicitud s = new Solicitud("hechoLibre", "x".repeat(500));
        s.setEstado(EstadoSolicitudBorradoEnum.CREADA);
        s.setSolicitudId("id1");

        when(solicitudRepository.findByHechoId("hechoLibre")).thenReturn(List.of(s));

        assertTrue(fachada.estaActivo("hechoLibre"));
    }
}