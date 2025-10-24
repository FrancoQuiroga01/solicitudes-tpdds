package ar.edu.utn.dds.k3003.model.Controller;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.net.URI;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudesController {

    private final FachadaSolicitudes fachada;

    @Autowired
    public SolicitudesController(FachadaSolicitudes fachada) {
        this.fachada = fachada;
    }

    @GetMapping
    public ResponseEntity<List<SolicitudDTO>> listarPorHecho (@RequestParam("hecho") String hecho) {
        return ResponseEntity.ok(fachada.buscarSolicitudXHecho(hecho));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudDTO> obtenerPorId(@PathVariable("id") String id) {
        return ResponseEntity.ok(fachada.buscarSolicitudXId(id));
    }


    @Timed(value="http.solicitudes.post.latency", histogram = true, percentiles = {0.5,0.95,0.99})
    @PostMapping
    public ResponseEntity<SolicitudDTO> crear(@RequestBody SolicitudDTO solicitud) {
        var dto = fachada.agregar(solicitud);
        return ResponseEntity
                .created(URI.create("/solicitudes/" + dto.id()))
                .body(dto);
    }


    @PatchMapping
    public ResponseEntity<SolicitudDTO> modificar(@RequestParam("id") String id,
                                                  @RequestParam("estado") EstadoSolicitudBorradoEnum estado,
                                                  @RequestParam("descripcion") String descripcion) {
        return ResponseEntity.ok(fachada.modificar(id, estado, descripcion));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudDTO> cambiarEstado(
            @PathVariable String id,
            @RequestParam EstadoSolicitudBorradoEnum estado,
            @RequestBody(required = false) Map<String,String> body) {
        String nuevaDesc = body != null ? body.get("descripcion") : null;
        return ResponseEntity.ok(fachada.modificar(id, estado, nuevaDesc));
    }


    @GetMapping("/hechos/{hechoId}/sin-solicitudes")
    public ResponseEntity<Map<String, Object>> sinSolicitudes(@PathVariable String hechoId) {
        boolean ok = ((ar.edu.utn.dds.k3003.model.app.Fachada)fachada).noTieneSolicitudes(hechoId);
        return ResponseEntity.ok(Map.of("hechoId", hechoId, "sinSolicitudes", ok));
    }

    @PostMapping("/hechos/hechos-sin-solicitudes")
    public ResponseEntity<Map<String, List<String>>> elegibles(@RequestBody Map<String, List<String>> body) {
        var ids = body.getOrDefault("ids", List.of());
        var elegibles = ((ar.edu.utn.dds.k3003.model.app.Fachada)fachada).hechosElegibles(ids);
        return ResponseEntity.ok(Map.of("hechosSinSolicitudes", elegibles));
    }



}

