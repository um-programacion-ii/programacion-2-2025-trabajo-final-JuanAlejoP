package com.juanalejop.proxy.config;

import com.juanalejop.proxy.dto.EventoAsientosDto;
import com.juanalejop.proxy.service.RedisService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RedisTestRunner implements CommandLineRunner {

    private final RedisService redisService;

    public RedisTestRunner(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================================================");
        System.out.println("🧪 INICIANDO PRUEBA DE REDIS (Issue 2.1)");

        long eventoIdPrueba = 1L; // El profesor dijo que el evento 1 tiene datos
        System.out.println("🔍 Buscando asientos para evento ID: " + eventoIdPrueba);

        Optional<EventoAsientosDto> resultado = redisService.getAsientos(eventoIdPrueba);

        if (resultado.isPresent()) {
            EventoAsientosDto datos = resultado.get();
            System.out.println("✅ ¡ÉXITO! Datos recibidos de Redis:");
            System.out.println("   Encabezado: " + datos.toString());

            if (datos.getAsientos() != null) {
                datos.getAsientos().forEach(asiento ->
                        System.out.println("   -> " + asiento.toString())
                );
            }
        } else {
            System.out.println("⚠️ No se encontraron datos para el evento " + eventoIdPrueba);
            System.out.println("   (Esto es normal si el servidor de cátedra reinició y nadie compró nada aún)");
        }
        System.out.println("=================================================");
    }
}