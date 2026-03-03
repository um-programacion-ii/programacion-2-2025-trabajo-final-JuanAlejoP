package com.juanalejop.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CatedraService {

    private final Logger log = LoggerFactory.getLogger(CatedraService.class);
    private final RestTemplate restTemplate;

    private final String CATEDRA_URL = "http://192.168.194.250:8080/api/endpoints/v1";

    @Value("${proxy.catedra-token}")
    private String catedraToken;

    public CatedraService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public boolean bloquearAsientos(Object payload) {
        return enviarPost(CATEDRA_URL + "/bloquear-asientos", payload);
    }

    public boolean realizarVenta(Object payload) {
        return enviarPost(CATEDRA_URL + "/realizar-venta", payload);
    }

    public ResponseEntity<Object> getEventosResumidos() {
        return hacerGet(CATEDRA_URL + "/eventos-resumidos");
    }

    public ResponseEntity<Object> getEventosCompletos() {
        return hacerGet(CATEDRA_URL + "/eventos");
    }

    private ResponseEntity<Object> hacerGet(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(catedraToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        } catch (Exception e) {
            log.error("Error al realizar GET a Cátedra [{}]: {}", url, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean enviarPost(String url, Object payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(catedraToken);

            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("resultado")) {
                return Boolean.TRUE.equals(response.getBody().get("resultado"));
            }

            return false;

        } catch (Exception e) {
            log.error("Error al realizar POST a Cátedra [{}]: {}", url, e.getMessage());
            return false;
        }
    }
}