package ar.edu.utn.dds.k3003.model.Controller;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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


    @PostMapping
    public ResponseEntity<SolicitudDTO> crear(@RequestBody SolicitudDTO solicitud) {
        return ResponseEntity.ok(fachada.agregar(solicitud));
    }

    @PatchMapping
    public ResponseEntity<SolicitudDTO> modificar(@RequestParam("id") String id,
                                                  @RequestParam("estado") EstadoSolicitudBorradoEnum estado,
                                                  @RequestParam("descripcion") String descripcion) {
        return ResponseEntity.ok(fachada.modificar(id, estado, descripcion));
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

