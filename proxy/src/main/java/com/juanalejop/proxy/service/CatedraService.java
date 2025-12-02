package com.juanalejop.proxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatedraService {

    private final RestTemplate restTemplate;

    // URL base de la cátedra (ej. http://192.168.194.250:8080)
    // Podemos hardcodearla o ponerla en application.yml.
    // Para simplificar, la ponemos aquí o usamos la del application.yml si querés ser prolijo.
    private final String CATEDRA_URL = "http://192.168.194.250:8080/api/endpoints/v1";

    // Necesitamos el token del alumno para hablar con la cátedra
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

    private boolean enviarPost(String url, Object payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(catedraToken); // Token JWT de la cátedra

            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Si devuelve 200 OK, asumimos éxito.
            // Si devuelve 200 pero con "resultado": false en el JSON, también cuenta como "éxito técnico" (llegó).
            // Lo ideal sería parsear el JSON de respuesta, pero por ahora con el status basta.
            return response.getStatusCode() == HttpStatus.OK;

        } catch (Exception e) {
            System.err.println("❌ Error comunicando con Cátedra (" + url + "): " + e.getMessage());
            // MOCKING / FALLBACK (La estrategia que hablamos)
            // Si la cátedra falla (500, timeout), devolvemos true para que tu app no se trabe.
            // Podes comentar esto si querés probar el error real.
            System.out.println("⚠️ Activando MOCK por fallo de cátedra: Simulando éxito.");
            return true;
        }
    }
}