package com.juanalejop.backend.web.rest;

import com.juanalejop.backend.domain.Evento;
import com.juanalejop.backend.repository.EventoRepository;
import com.juanalejop.backend.service.EventoService;
import com.juanalejop.backend.service.ProxyService; // <--- NUEVO IMPORT
import com.juanalejop.backend.service.dto.EventoDTO;
import com.juanalejop.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map; // <--- NUEVO IMPORT
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.juanalejop.backend.domain.Evento}.
 */
@RestController
@RequestMapping("/api/eventos")
public class EventoResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventoResource.class);

    private static final String ENTITY_NAME = "evento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventoService eventoService;

    private final EventoRepository eventoRepository;

    private final ProxyService proxyService; // <--- NUEVA INYECCIÓN

    // Constructor actualizado
    public EventoResource(EventoService eventoService, EventoRepository eventoRepository, ProxyService proxyService) {
        this.eventoService = eventoService;
        this.eventoRepository = eventoRepository;
        this.proxyService = proxyService; // <--- ASIGNACIÓN
    }

    /**
     * {@code POST  /eventos} : Create a new evento.
     */
    @PostMapping("")
    public ResponseEntity<EventoDTO> createEvento(@Valid @RequestBody EventoDTO eventoDTO) throws URISyntaxException {
        LOG.debug("REST request to save Evento : {}", eventoDTO);
        if (eventoDTO.getId() != null) {
            throw new BadRequestAlertException("A new evento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        eventoDTO = eventoService.save(eventoDTO);
        return ResponseEntity.created(new URI("/api/eventos/" + eventoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, eventoDTO.getId().toString()))
            .body(eventoDTO);
    }

    /**
     * {@code PUT  /eventos/:id} : Updates an existing evento.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> updateEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EventoDTO eventoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Evento : {}, {}", id, eventoDTO);
        if (eventoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        eventoDTO = eventoService.update(eventoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventoDTO.getId().toString()))
            .body(eventoDTO);
    }

    /**
     * {@code PATCH  /eventos/:id} : Partial updates given fields of an existing evento.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventoDTO> partialUpdateEvento(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EventoDTO eventoDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Evento partially : {}, {}", id, eventoDTO);
        if (eventoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eventoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!eventoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EventoDTO> result = eventoService.partialUpdate(eventoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /eventos} : get all the eventos.
     */
    @GetMapping("")
    public ResponseEntity<List<EventoDTO>> getAllEventos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Eventos");

        // --- MAGIA DE SINCRONIZACIÓN AUTOMÁTICA ---
        // Si no hay eventos en la BD local, intentamos traerlos del Proxy
        if (eventoRepository.count() == 0) {
            LOG.info("📭 Base de datos vacía. Intentando sincronizar eventos desde la Cátedra...");
            try {
                List<Map<String, Object>> eventosExternos = proxyService.obtenerListaEventos();

                if (eventosExternos != null && !eventosExternos.isEmpty()) {
                    for (Map<String, Object> dato : eventosExternos) {
                        try {
                            Evento evento = new Evento();

                            // 1. ID Externo
                            Object idExterno = dato.get("id");
                            if (idExterno != null) {
                                evento.setIdCatedra(Long.valueOf(idExterno.toString()));
                            }

                            // 2. Textos
                            evento.setTitulo((String) dato.get("titulo"));
                            evento.setDescripcion((String) dato.get("descripcion"));
                            evento.setResumen((String) dato.get("resumen"));
                            evento.setImagenUrl((String) dato.get("imagenUrl"));
                            evento.setDireccion((String) dato.get("direccion")); // Ojo, la cátedra a veces no manda dirección, puede quedar null.

                            // 3. PRECIO (Corrección: Buscar 'precioEntrada' O 'precio')
                            Object precioObj = dato.get("precioEntrada");
                            if (precioObj == null) precioObj = dato.get("precio"); // Por si acaso

                            if (precioObj != null) {
                                evento.setPrecio(Double.valueOf(precioObj.toString()));
                            } else {
                                evento.setPrecio(0.0); // Valor default para que no falle la validación
                            }

                            // 4. FECHA (Corrección: Parsear 'fecha')
                            Object fechaObj = dato.get("fecha");
                            if (fechaObj != null) {
                                // Parseamos la fecha ISO 8601 (2026-01-10T11:00:00Z) a ZonedDateTime
                                try {
                                    java.time.ZonedDateTime fecha = java.time.ZonedDateTime.parse(fechaObj.toString());
                                    evento.setFechaHora(fecha);
                                } catch (Exception e) {
                                    LOG.warn("No se pudo parsear la fecha: " + fechaObj);
                                    evento.setFechaHora(java.time.ZonedDateTime.now()); // Fallback para no romper
                                }
                            } else {
                                evento.setFechaHora(java.time.ZonedDateTime.now()); // Fallback obligatorio
                            }

                            // 5. DIMENSIONES (Nuevo)
                            Object filasObj = dato.get("filaAsientos");
                            if (filasObj != null) {
                                evento.setFilaAsientos(Integer.valueOf(filasObj.toString()));
                            } else {
                                evento.setFilaAsientos(10); // Default por seguridad
                            }

                            Object colObj = dato.get("columnAsientos");
                            if (colObj != null) {
                                evento.setColumnAsientos(Integer.valueOf(colObj.toString()));
                            } else {
                                evento.setColumnAsientos(10); // Default por seguridad
                            }

                            // Guardamos en BD H2
                            eventoRepository.save(evento);

                        } catch (Exception innerEx) {
                            LOG.error("Error mapeando un evento individual: " + innerEx.getMessage());
                        }
                    }
                    LOG.info("✅ Sincronización completada. Se guardaron {} eventos.", eventosExternos.size());
                } else {
                    LOG.warn("⚠️ El Proxy devolvió una lista vacía de eventos.");
                }
            } catch (Exception e) {
                LOG.error("❌ Falló la sincronización automática con el Proxy: " + e.getMessage());
            }
        }
        // ------------------------------------------

        Page<EventoDTO> page = eventoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /eventos/:id} : get the "id" evento.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> getEvento(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Evento : {}", id);
        Optional<EventoDTO> eventoDTO = eventoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventoDTO);
    }

    /**
     * {@code DELETE  /eventos/:id} : delete the "id" evento.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Evento : {}", id);
        eventoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
