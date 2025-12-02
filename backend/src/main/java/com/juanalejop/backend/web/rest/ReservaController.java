package com.juanalejop.backend.web.rest;

import com.juanalejop.backend.service.ProxyService;
import com.juanalejop.backend.service.dto.reserva.SolicitudBloqueoDTO;
import com.juanalejop.backend.service.dto.reserva.SolicitudVentaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ProxyService proxyService;

    public ReservaController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquearAsientos(@RequestBody SolicitudBloqueoDTO solicitud) {
        // Delegamos al Proxy (que llamará a Cátedra)
        boolean exito = proxyService.bloquearAsientos(solicitud);

        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Bloqueo exitoso\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"mensaje\": \"No se pudieron bloquear los asientos\"}");
        }
    }

    @PostMapping("/vender")
    public ResponseEntity<?> realizarVenta(@RequestBody SolicitudVentaDTO solicitud) {
        // Delegamos al Proxy (que llamará a Cátedra)
        boolean exito = proxyService.realizarVenta(solicitud);

        if (exito) {
            // TODO: Aquí deberíamos guardar la venta en nuestra BD local (H2/MySQL)
            // Lo haremos en el Issue 6.3 para mantener el orden.
            return ResponseEntity.ok().body("{\"mensaje\": \"Venta exitosa\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"mensaje\": \"Falló la venta\"}");
        }
    }
}
