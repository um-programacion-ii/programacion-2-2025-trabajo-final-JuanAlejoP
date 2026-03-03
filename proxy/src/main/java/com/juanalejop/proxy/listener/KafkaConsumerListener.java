package com.juanalejop.proxy.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KafkaConsumerListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerListener.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BACKEND_NOTIFY_URL = "http://localhost:8080/api/eventos/sincronizar";

    @Value("${proxy.api-key}")
    private String proxyApiKey;

    @KafkaListener(topics = "eventos-actualizacion")
    public void recibirMensaje(String mensaje) {
        log.info("Mensaje Kafka recibido: {}", mensaje);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(BACKEND_NOTIFY_URL, HttpMethod.POST, entity, Void.class);
            log.info("Notificación de sincronización enviada al Backend correctamente.");

        } catch (Exception e) {
            log.error("Error al notificar al Backend: {}", e.getMessage());
        }
    }
}