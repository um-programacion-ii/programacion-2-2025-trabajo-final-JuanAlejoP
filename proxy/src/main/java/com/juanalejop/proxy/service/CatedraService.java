package com.juanalejop.proxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CatedraService {

    private final RestTemplate restTemplate;

    // URL base de la cátedra
    private final String CATEDRA_URL = "http://192.168.194.250:8080/api/endpoints/v1";

    // Token del application.yml
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

    // --- 1. MÉTODO PARA LISTAS RÁPIDAS (Payload 3) ---
    public ResponseEntity<Object> getEventosResumidos() {
        return hacerGet(CATEDRA_URL + "/eventos-resumidos");
    }

    // --- 2. MÉTODO NUEVO PARA SINCRONIZACIÓN (Payload 4 - Con filas/cols) ---
    public ResponseEntity<Object> getEventosCompletos() {
        return hacerGet(CATEDRA_URL + "/eventos");
    }

    // --- Helper para peticiones GET ---
    private ResponseEntity<Object> hacerGet(String url) {
        try {
            System.out.println("🌐 Pidiendo GET a Cátedra: " + url);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(catedraToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            return restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        } catch (Exception e) {
            System.err.println("❌ Error GET en Cátedra: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    private boolean enviarPost(String url, Object payload) {
        try {
            System.out.println("🌐 Conectando con Cátedra: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(catedraToken);

            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("resultado")) {
                boolean resultadoReal = Boolean.TRUE.equals(response.getBody().get("resultado"));
                System.out.println("✅ Cátedra respondió. Resultado real: " + resultadoReal);
                return resultadoReal;
            }

            return false;

        } catch (Exception e) {
            System.err.println("❌ ERROR REAL en Cátedra: " + e.getMessage());
            return false;
        }
    }
}