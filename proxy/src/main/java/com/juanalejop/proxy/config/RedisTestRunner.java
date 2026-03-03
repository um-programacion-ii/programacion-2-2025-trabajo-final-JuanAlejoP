package com.juanalejop.proxy.config;

import com.juanalejop.proxy.dto.EventoAsientosDto;
import com.juanalejop.proxy.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RedisTestRunner implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(RedisTestRunner.class);

    private final RedisService redisService;

    public RedisTestRunner(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void run(String... args) {
        long eventoId = 1L;
        LOG.debug("Verificando disponibilidad de datos en Redis para evento ID: {}", eventoId);

        try {
            Optional<EventoAsientosDto> resultado = redisService.getAsientos(eventoId);

            if (resultado.isPresent()) {
                LOG.info("Redis operativo. Datos encontrados para evento {}: {} asientos.", eventoId, resultado.get().getAsientos().size());
            } else {
                LOG.info("Redis operativo. Sin datos cacheados para evento {} (caché vacía).", eventoId);
            }
        } catch (Exception e) {
            LOG.warn("No se pudo contactar con Redis al inicio: {}", e.getMessage());
        }
    }
}