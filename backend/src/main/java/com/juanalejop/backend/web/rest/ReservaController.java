package com.juanalejop.backend.web.rest;

import com.juanalejop.backend.domain.Evento;
import com.juanalejop.backend.repository.EventoRepository;
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
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ProxyService proxyService;
    private final VentaService ventaService;
    private final TicketService ticketService;
    private final EventoService eventoService;
    private final EventoRepository eventoRepository;

    public ReservaController(ProxyService proxyService,
                             VentaService ventaService,
                             TicketService ticketService,
                             EventoService eventoService,
                             EventoRepository eventoRepository) {
        this.proxyService = proxyService;
        this.ventaService = ventaService;
        this.ticketService = ticketService;
        this.eventoService = eventoService;
        this.eventoRepository = eventoRepository;
    }

    @PostMapping("/bloquear")
    public ResponseEntity<?> bloquearAsientos(@RequestBody SolicitudBloqueoDTO solicitud) {
        Evento eventoLocal = eventoRepository.findById(solicitud.getEventoId()).orElse(null);

        if (eventoLocal != null && eventoLocal.getIdCatedra() != null) {
            solicitud.setEventoId(eventoLocal.getIdCatedra());
        }

        boolean exito = proxyService.bloquearAsientos(solicitud);

        if (exito) {
            return ResponseEntity.ok().body(Map.of("mensaje", "Bloqueo exitoso"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se pudieron bloquear los asientos"));
        }
    }

    @PostMapping("/vender")
    public ResponseEntity<?> realizarVenta(@RequestBody SolicitudVentaDTO solicitud) {

        Long idEventoLocal = solicitud.getEventoId();
        Evento eventoLocal = eventoRepository.findById(idEventoLocal).orElse(null);

        if (eventoLocal == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Evento no encontrado localmente"));
        }

        if (eventoLocal.getIdCatedra() != null) {
            solicitud.setEventoId(eventoLocal.getIdCatedra());
        }

        if (solicitud.getPrecioVenta() == null || solicitud.getPrecioVenta() == 0.0) {
            solicitud.setPrecioVenta(eventoLocal.getPrecio());
        }

        if (solicitud.getFecha() == null) {
            String fechaEstricta = java.time.Instant.now()
                .truncatedTo(java.time.temporal.ChronoUnit.MILLIS)
                .toString();
            solicitud.setFecha(fechaEstricta);
        }

        boolean exitoCatedra = proxyService.realizarVenta(solicitud);

        if (exitoCatedra) {
            try {
                solicitud.setEventoId(idEventoLocal);
                guardarVentaLocal(solicitud, eventoLocal);

                return ResponseEntity.ok().body(Map.of("mensaje", "Venta exitosa y registrada"));
            } catch (Exception e) {
                return ResponseEntity.ok().body(Map.of("mensaje", "Venta confirmada (con advertencia local)"));
            }
        } else {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Falló la venta en Cátedra"));
        }
    }

    private void guardarVentaLocal(SolicitudVentaDTO solicitud, Evento eventoEntity) {
        VentaDTO venta = new VentaDTO();
        venta.setFecha(ZonedDateTime.now());

        if (eventoEntity != null && eventoEntity.getPrecio() != null) {
            double total = eventoEntity.getPrecio() * solicitud.getAsientos().size();
            venta.setTotal(total);
        } else {
            venta.setTotal(0.0);
        }

        venta.setEstado("CONFIRMADA");

        VentaDTO ventaGuardada = ventaService.save(venta);

        for (SolicitudVentaDTO.AsientoPersonaDTO asiento : solicitud.getAsientos()) {
            TicketDTO ticket = new TicketDTO();
            ticket.setFila(asiento.getFila());
            ticket.setColumna(asiento.getColumna());
            ticket.setNombrePersona(asiento.getPersona());
            ticket.setVenta(ventaGuardada);

            ticketService.save(ticket);
        }
    }
}
