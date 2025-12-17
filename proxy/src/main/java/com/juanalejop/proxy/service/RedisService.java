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
            return Optional.empty();
        }

        try {
            // 2. Convertir el String JSON a Objeto Java
            EventoAsientosDto dto = objectMapper.readValue(json, EventoAsientosDto.class);

            // 3. FILTRAR EXPIRADOS (Lógica de Negocio) 🕒
            if (dto.getAsientos() != null) {
                List<AsientoDto> asientosValidos = dto.getAsientos().stream()
                        .filter(this::esAsientoValido) // Aplicamos el filtro
                        .collect(Collectors.toList());

                dto.setAsientos(asientosValidos);
            }

            return Optional.of(dto);
        } catch (Exception e) {
            System.err.println("❌ Error al leer/procesar Redis para evento " + eventoId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Devuelve TRUE si el asiento debe mostrarse (Vendido o Bloqueado vigente).
     * Devuelve FALSE si el bloqueo ya expiró (para que el móvil lo muestre Libre).
     */
    private boolean esAsientoValido(AsientoDto asiento) {
        // Si está VENDIDO, siempre es válido (nunca expira)
        if ("Vendido".equalsIgnoreCase(asiento.getEstado())) {
            return true;
        }

        // Si está BLOQUEADO, verificamos la fecha
        if ("Bloqueado".equalsIgnoreCase(asiento.getEstado())) {
            if (asiento.getExpira() == null) {
                // Si no tiene fecha, asumimos que es válido por seguridad
                return true;
            }
            try {
                // Parseamos fecha ISO-8601 (ej: 2025-11-20T02:30:32Z) que viene de Cátedra
                Instant fechaExpiracion = Instant.parse(asiento.getExpira());
                Instant ahora = Instant.now();

                // Si AHORA es DESPUÉS de la expiración -> Falso (lo borramos de la lista para liberar)
                if (ahora.isAfter(fechaExpiracion)) {
                    System.out.println("⏳ Proxy: Liberando asiento expirado localmente -> F" + asiento.getFila() + "-C" + asiento.getColumna());
                    return false;
                }
            } catch (Exception e) {
                // Si la fecha viene mal formateada, mejor lo dejamos bloqueado por seguridad
                System.err.println("⚠️ Error parseando fecha asiento: " + e.getMessage());
            }
        }

        // Cualquier otro caso, lo dejamos pasar
        return true;
    }
}