package com.juanalejop.proxy.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value; // Importante

@Component
public class KafkaConsumerListener {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String BACKEND_NOTIFY_URL = "http://localhost:8080/api/eventos/sincronizar";

    // LEEMOS LA KEY DEL ARCHIVO YML QUE ME MOSTRASTE
    @Value("${proxy.api-key}")
    private String proxyApiKey;

    @KafkaListener(topics = "eventos-actualizacion")
    public void recibirMensaje(String mensaje) {
        System.out.println("=================================================");
        System.out.println("📨 ¡MENSAJE DE KAFKA RECIBIDO!");

        try {
            System.out.println("🔔 Notificando al Backend...");

            // Preparamos el Header con la clave real
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", proxyApiKey); // Usamos la variable inyectada
            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(BACKEND_NOTIFY_URL, HttpMethod.POST, entity, Void.class);

            System.out.println("✅ Notificación enviada correctamente.");

        } catch (Exception e) {
            System.err.println("❌ Error al notificar al Backend: " + e.getMessage());
        }
        System.out.println("=================================================");
    }
}