package ar.edu.utn.dds.k3003.model.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("!stub")
public class FuentesConfig {

    @Bean
    public RestTemplate restTemplate(){
        var factory  = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        return new RestTemplate(factory);
    }

    public String fuentesBaseUrl(@Value("${fuentes.base-url}") String baseUrl){
        return  baseUrl;
    }
}
