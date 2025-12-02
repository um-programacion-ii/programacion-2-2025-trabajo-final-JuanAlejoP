package com.juanalejop.backend.service;

import com.juanalejop.backend.service.dto.proxy.EventoAsientosProxyDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import java.util.Optional;

@Service
public class ProxyService {

    private final RestTemplate restTemplate;

    @Value("${integration.proxy.url}")
    private String proxyUrl;

    @Value("${integration.proxy.api-key}")
    private String proxyApiKey;

    public ProxyService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public Optional<EventoAsientosProxyDto> obtenerAsientos(Long eventoId) {
        try {
            // Configurar Headers (Autenticación con el Proxy)
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // URL: http://localhost:8081/api/proxy/eventos/{id}/asientos
            String url = proxyUrl + "/eventos/" + eventoId + "/asientos";

            // Hacer la llamada GET
            ResponseEntity<EventoAsientosProxyDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                EventoAsientosProxyDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
        } catch (Exception e) {
            // Si el Proxy da 404 (no hay asientos ocupados) o está caído, no rompemos todo.
            // Simplemente devolvemos vacío y el evento se mostrará con todos libres.
            System.err.println("⚠️ No se pudo obtener asientos del Proxy: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Llama al Proxy para bloquear asientos.
     * Proxy URL esperada: POST /api/proxy/bloquear
     */
    public boolean bloquearAsientos(Object payload) {
        return enviarPostAlProxy("/bloquear", payload);
    }

    /**
     * Llama al Proxy para confirmar una venta.
     * Proxy URL esperada: POST /api/proxy/vender
     */
    public boolean realizarVenta(Object payload) {
        return enviarPostAlProxy("/vender", payload);
    }

    // Método auxiliar privado para no repetir código
    private boolean enviarPostAlProxy(String endpoint, Object payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(payload, headers);
            String url = proxyUrl + endpoint; // ej: http://localhost:8081/api/proxy/bloquear

            // Esperamos un booleano o un objeto de respuesta.
            // Para simplificar, asumimos que si el Proxy devuelve 200 OK, es true.
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("❌ Error comunicando con Proxy (" + endpoint + "): " + e.getMessage());
            return false;
        }
    }
}
