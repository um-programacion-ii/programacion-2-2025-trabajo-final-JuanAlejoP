package com.juanalejop.proxy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juanalejop.proxy.dto.AsientoDto;
import com.juanalejop.proxy.dto.EventoAsientosDto;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<EventoAsientosDto> getAsientos(Long eventoId) {
        String key = "evento_" + eventoId;
        String json = redisTemplate.opsForValue().get(key);

        if (json == null) {
            return Optional.empty();
        }

        try {
            EventoAsientosDto dto = objectMapper.readValue(json, EventoAsientosDto.class);

            if (dto.getAsientos() != null) {
                List<AsientoDto> asientosValidos = dto.getAsientos().stream()
                        .filter(this::esAsientoValido)
                        .collect(Collectors.toList());

                dto.setAsientos(asientosValidos);
            }

            return Optional.of(dto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean esAsientoValido(AsientoDto asiento) {
        if ("Vendido".equalsIgnoreCase(asiento.getEstado())) {
            return true;
        }

        if ("Bloqueado".equalsIgnoreCase(asiento.getEstado())) {
            if (asiento.getExpira() == null) {
                return true;
            }
            try {
                Instant fechaExpiracion = Instant.parse(asiento.getExpira());
                Instant ahora = Instant.now();

                if (ahora.isAfter(fechaExpiracion)) {
                    return false;
                }
            } catch (Exception e) {
                return true;
            }
        }

        return true;
    }
}