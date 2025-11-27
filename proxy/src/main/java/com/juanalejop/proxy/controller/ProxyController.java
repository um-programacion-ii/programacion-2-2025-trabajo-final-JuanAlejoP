package com.juanalejop.proxy.controller;

import com.juanalejop.proxy.dto.EventoAsientosDto;
import com.juanalejop.proxy.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final RedisService redisService;

    // Inyectamos el valor del application.yml
    @Value("${proxy.api-key}")
    private String apiKeyConfigurada;

    public ProxyController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/eventos/{id}/asientos")
    public ResponseEntity<?> getAsientosDelEvento(
            @PathVariable Long id,
            @RequestHeader(value = "X-API-KEY", required = false) String apiKeyRecibida) {

        // 1. Verificación de Seguridad
        if (apiKeyRecibida == null || !apiKeyRecibida.equals(apiKeyConfigurada)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado: API Key inválida");
        }

        // 2. Lógica de Negocio (solo si pasó la seguridad)
        return redisService.getAsientos(id)
                .map(ResponseEntity::ok) // Si existe en Redis -> 200 OK + JSON
                .orElse(ResponseEntity.notFound().build()); // Si no existe -> 404 Not Found
    }
}