package com.juanalejop.backend.service;

import com.juanalejop.backend.service.dto.proxy.EventoAsientosProxyDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = proxyUrl + "/eventos/" + eventoId + "/asientos";

            ResponseEntity<EventoAsientosProxyDto> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                EventoAsientosProxyDto.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Optional.of(response.getBody());
            }
            return Optional.empty();

        } catch (HttpClientErrorException.NotFound e) {
            // 🟢 COMPORTAMIENTO ESPERADO: Si Redis no tiene el evento, asumimos que está todo libre.
            System.out.println("ℹ️ Evento " + eventoId + ": Sin reservas (404). Retornando mapa libre.");
            EventoAsientosProxyDto dtoVacio = new EventoAsientosProxyDto();
            dtoVacio.setAsientos(new ArrayList<>());
            return Optional.of(dtoVacio);
        } catch (Exception e) {
            System.err.println("⚠️ Error obteniendo asientos del Proxy: " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean bloquearAsientos(Object payload) {
        return enviarPostAlProxy("/bloquear", payload);
    }

    public boolean realizarVenta(Object payload) {
        return enviarPostAlProxy("/vender", payload);
    }

    private boolean enviarPostAlProxy(String endpoint, Object payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // CORREGIDO: Eliminada la variable 'entity' que no se usaba
            HttpEntity<Object> entityConBody = new HttpEntity<>(payload, headers);

            String url = proxyUrl + endpoint;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entityConBody, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            System.err.println("❌ Error comunicando con Proxy (" + endpoint + "): " + e.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> obtenerListaEventos() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = proxyUrl + "/eventos-full";

            ResponseEntity<List> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                List.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (List<Map<String, Object>>) response.getBody();
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error sincronizando eventos: " + e.getMessage());
        }
        return List.of(); // Devuelve lista inmutable vacía si falla
    }
}
