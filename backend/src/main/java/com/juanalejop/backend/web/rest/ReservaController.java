package com.juanalejop.backend.web.rest;

import com.juanalejop.backend.service.EventoService;
import com.juanalejop.backend.service.ProxyService;
import com.juanalejop.backend.service.TicketService;
import com.juanalejop.backend.service.VentaService;
import com.juanalejop.backend.service.dto.TicketDTO;
import com.juanalejop.backend.service.dto.VentaDTO;
import com.juanalejop.backend.service.dto.reserva.SolicitudBloqueoDTO;
import com.juanalejop.backend.service.dto.reserva.SolicitudVentaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ProxyService proxyService;
    private final VentaService ventaService;
    private final TicketService ticketService;
    private final EventoService eventoService;

    public ReservaController(ProxyService proxyService, VentaService ventaService, TicketService ticketService, EventoService eventoService) {
        this.proxyService = proxyService;
        this.ventaService = ventaService;
        this.ticketService = ticketService;
        this.eventoService = eventoService;
    }

    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquearAsientos(@RequestBody SolicitudBloqueoDTO solicitud) {
        boolean exito = proxyService.bloquearAsientos(solicitud);
        if (exito) {
            return ResponseEntity.ok().body("{\"mensaje\": \"Bloqueo exitoso\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"mensaje\": \"No se pudieron bloquear los asientos\"}");
        }
    }

    @PostMapping("/vender")
    public ResponseEntity<?> realizarVenta(@RequestBody SolicitudVentaDTO solicitud) {
        // 1. Delegamos al Proxy (Cátedra)
        boolean exitoCatedra = proxyService.realizarVenta(solicitud);

        if (exitoCatedra) {
            try {
                // 2. Si Cátedra dijo OK, guardamos localmente
                guardarVentaLocal(solicitud);
                return ResponseEntity.ok().body("{\"mensaje\": \"Venta exitosa y registrada\"}");
            } catch (Exception e) {
                // Si falla el guardado local pero Cátedra ya cobró, es un problema grave.
                // En un sistema real usaríamos transacciones distribuidas o cola de reintentos.
                // Para este trabajo, logueamos el error crítico.
                System.err.println("CRITICAL: Venta confirmada en Cátedra pero falló guardado local: " + e.getMessage());
                return ResponseEntity.internalServerError().body("{\"mensaje\": \"Venta confirmada pero error al guardar localmente\"}");
            }
        } else {
            return ResponseEntity.badRequest().body("{\"mensaje\": \"Falló la venta en Cátedra\"}");
        }
    }

    private void guardarVentaLocal(SolicitudVentaDTO solicitud) {
        // Crear Venta
        VentaDTO venta = new VentaDTO();
        venta.setFecha(ZonedDateTime.now());
        venta.setTotal(0.0); // Deberíamos calcularlo con el precio del evento, por ahora 0
        venta.setEstado("CONFIRMADA");

        // Vincular con Evento (buscamos por ID de Cátedra o asumimos que es el mismo si sincronizamos)
        // Simplificación: Asumimos que tenemos el evento local con ese ID
        // venta.setEventoId(solicitud.getEventoId()); <-- Esto depende de cómo tengas el DTO

        VentaDTO ventaGuardada = ventaService.save(venta);

        // Crear Tickets
        for (SolicitudVentaDTO.AsientoPersonaDTO asiento : solicitud.getAsientos()) {
            TicketDTO ticket = new TicketDTO();
            ticket.setFila(asiento.getFila());
            ticket.setColumna(asiento.getColumna());
            ticket.setNombrePersona(asiento.getPersona());
            ticket.setVenta(ventaGuardada); // Relacionar con la venta padre

            ticketService.save(ticket);
        }
    }
}
