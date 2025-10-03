package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.dtos.ColeccionDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }

    @Autowired
    private FachadaFuente fachadaFuente;

    @PostConstruct
    public void init() {
        System.out.println("Probando conexión...");
        List<ColeccionDTO> lista = fachadaFuente.colecciones();
        lista.forEach(c -> System.out.println("Colección encontrada: " + c.nombre()));
    }



}
