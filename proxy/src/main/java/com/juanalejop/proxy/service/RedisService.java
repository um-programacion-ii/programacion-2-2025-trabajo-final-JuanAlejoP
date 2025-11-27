package com.juanalejop.proxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanalejop.proxy.dto.EventoAsientosDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // Spring inyecta automáticamente redisTemplate y objectMapper
    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Busca los asientos de un evento en el Redis de la Cátedra.
     * @param eventoId El ID del evento (ej. 1)
     * @return Un Optional con los datos, o vacío si no hay datos o falla el parseo.
     */
    public Optional<EventoAsientosDto> getAsientos(Long eventoId) {

        String key = "evento_" + eventoId;

        // 1. Obtener el JSON crudo (String) desde Redis
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            // Si es null, significa que no hay info en Redis para este evento
            // (puede que todos los asientos estén libres o el evento no exista)
            return Optional.empty();
        }

        try {
            // 2. Convertir el String JSON a Objeto Java
            EventoAsientosDto dto = objectMapper.readValue(json, EventoAsientosDto.class);
            return Optional.of(dto);
        } catch (Exception e) {
            System.err.println("❌ Error al leer JSON de Redis para evento " + eventoId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}