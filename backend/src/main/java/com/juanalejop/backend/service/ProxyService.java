package com.juanalejop.backend.service;

import com.juanalejop.backend.service.dto.proxy.EventoAsientosProxyDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProxyService {

    private final Logger log = LoggerFactory.getLogger(ProxyService.class);
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
            // Si Redis no tiene el evento (404), asumimos que no hay reservas y retornamos una lista vacía.
            log.debug("Evento {}: Sin reservas (404). Retornando mapa libre.", eventoId);
            EventoAsientosProxyDto dtoVacio = new EventoAsientosProxyDto();
            dtoVacio.setAsientos(new ArrayList<>());
            return Optional.of(dtoVacio);
        } catch (Exception e) {
            log.error("Error obteniendo asientos del Proxy para evento {}: {}", eventoId, e.getMessage());
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

            HttpEntity<Object> entityConBody = new HttpEntity<>(payload, headers);

            String url = proxyUrl + endpoint;

            ResponseEntity<String> response = restTemplate.postForEntity(url, entityConBody, String.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Error comunicando con Proxy ({}): {}", endpoint, e.getMessage());
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
            log.error("Error sincronizando eventos: {}", e.getMessage());
        }
        return List.of();
    }
}
