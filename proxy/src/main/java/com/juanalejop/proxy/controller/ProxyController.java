package com.juanalejop.proxy.controller;

import com.juanalejop.proxy.dto.EventoAsientosDto;
import com.juanalejop.proxy.service.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.juanalejop.proxy.dto.reserva.SolicitudBloqueoDTO;
import com.juanalejop.proxy.dto.reserva.SolicitudVentaDTO;
import com.juanalejop.proxy.service.CatedraService;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final RedisService redisService;
    private final CatedraService catedraService;

    // Inyectamos el valor del application.yml
    @Value("${proxy.api-key}")
    private String apiKeyConfigurada;

    public ProxyController(RedisService redisService, CatedraService catedraService) {
        this.redisService = redisService;
        this.catedraService = catedraService;
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
    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquear(
            @RequestBody SolicitudBloqueoDTO body,
            @RequestHeader(value = "X-API-KEY", required = false) String apiKeyRecibida) {

        if (!validarApiKey(apiKeyRecibida)) return unauthorized();

        boolean exito = catedraService.bloquearAsientos(body);
        return exito ? ResponseEntity.ok("Bloqueo enviado") : ResponseEntity.status(500).body("Error en cátedra");
    }
    @PostMapping("/vender")
    public ResponseEntity<?> vender(
            @RequestBody SolicitudVentaDTO body,
            @RequestHeader(value = "X-API-KEY", required = false) String apiKeyRecibida) {

        if (!validarApiKey(apiKeyRecibida)) return unauthorized();

        boolean exito = catedraService.realizarVenta(body);
        return exito ? ResponseEntity.ok("Venta enviada") : ResponseEntity.status(500).body("Error en cátedra");
    }
    // Métodos auxiliares para no repetir código de seguridad
    private boolean validarApiKey(String recibida) {
        return recibida != null && recibida.equals(apiKeyConfigurada);
    }

    private ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado");
    }
}