package com.juanalejop.proxy.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerListener {

    /**
     * Escucha el tópico 'eventos-actualizacion'.
     * Spring Boot se encarga de toda la magia de conexión usando lo que pusimos en application.yml
     */
    @KafkaListener(topics = "eventos-actualizacion")
    public void recibirMensaje(String mensaje) {
        System.out.println("=================================================");
        System.out.println("📨 ¡MENSAJE DE KAFKA RECIBIDO!");
        System.out.println("Contenido: " + mensaje);
        System.out.println("=================================================");

    }
}