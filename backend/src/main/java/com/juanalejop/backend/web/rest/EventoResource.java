package com.juanalejop.backend.web.rest;

import com.juanalejop.backend.domain.Evento;
import com.juanalejop.backend.repository.EventoRepository;
import com.juanalejop.backend.service.EventoService;
import com.juanalejop.backend.service.ProxyService;
import com.juanalejop.backend.service.dto.EventoDTO;
import com.juanalejop.backend.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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

@RestController
@RequestMapping("/api/eventos")
public class EventoResource {

    private static final Logger LOG = LoggerFactory.getLogger(EventoResource.class);
    private static final String ENTITY_NAME = "evento";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EventoService eventoService;
    private final EventoRepository eventoRepository;
    private final ProxyService proxyService;

    @Value("${integration.proxy.api-key}")
    private String internalApiKey;

    public EventoResource(EventoService eventoService, EventoRepository eventoRepository, ProxyService proxyService) {
        this.eventoService = eventoService;
        this.eventoRepository = eventoRepository;
        this.proxyService = proxyService;
    }

    // --- 🆕 NUEVO ENDPOINT INTERNO (Llamado por el Proxy cuando Kafka avisa) ---
    @PostMapping("/sincronizar")
    public ResponseEntity<Void> forzarSincronizacion(
        @RequestHeader(value = "X-API-KEY", required = false) String apiKeyRecibida
    ) {
        // 🛡️ VALIDACIÓN USANDO LA KEY CONFIGURADA
        if (apiKeyRecibida == null || !apiKeyRecibida.equals(internalApiKey)) {
            LOG.warn("⛔ Intento de acceso denegado. Key recibida: " + apiKeyRecibida);
            return ResponseEntity.status(401).build();
        }

        LOG.info("🔄 Sincronización autorizada por Proxy.");
        sincronizarConCatedra();
        return ResponseEntity.ok().build();
    }

    // --- MÉTODO REFACTORIZADO DE SINCRONIZACIÓN (UPSERT) ---
    private void sincronizarConCatedra() {
        try {
            LOG.info("⬇️ Descargando eventos frescos desde el Proxy...");
            List<Map<String, Object>> eventosExternos = proxyService.obtenerListaEventos();

            if (eventosExternos != null && !eventosExternos.isEmpty()) {
                // Traemos todos los locales para buscar duplicados en memoria
                // (Optimización: Evita hacer N consultas a la base)
                List<Evento> eventosLocales = eventoRepository.findAll();

                for (Map<String, Object> dato : eventosExternos) {
                    try {
                        Long idCatedra = Long.valueOf(dato.get("id").toString());

                        // Buscamos si ya existe localmente
                        Evento evento = eventosLocales.stream()
                            .filter(e -> e.getIdCatedra() != null && e.getIdCatedra().equals(idCatedra))
                            .findFirst()
                            .orElse(new Evento()); // Si no existe, creamos uno nuevo

                        // Mapeo de datos (Actualiza tanto nuevos como existentes)
                        evento.setIdCatedra(idCatedra);
                        evento.setTitulo((String) dato.get("titulo"));
                        evento.setDescripcion((String) dato.get("descripcion"));
                        evento.setResumen((String) dato.get("resumen"));
                        evento.setImagenUrl((String) dato.get("imagenUrl"));

                        // Dirección (puede ser null)
                        if (dato.get("direccion") != null) {
                            evento.setDireccion((String) dato.get("direccion"));
                        }

                        // Precio
                        Object precioObj = dato.get("precioEntrada");
                        if (precioObj == null) precioObj = dato.get("precio");
                        evento.setPrecio(precioObj != null ? Double.valueOf(precioObj.toString()) : 0.0);

                        // Fecha
                        Object fechaObj = dato.get("fecha");
                        if (fechaObj != null) {
                            try {
                                evento.setFechaHora(java.time.ZonedDateTime.parse(fechaObj.toString()));
                            } catch (Exception e) {
                                evento.setFechaHora(java.time.ZonedDateTime.now());
                            }
                        } else {
                            evento.setFechaHora(java.time.ZonedDateTime.now());
                        }

                        // Dimensiones
                        Object filasObj = dato.get("filaAsientos");
                        evento.setFilaAsientos(filasObj != null ? Integer.valueOf(filasObj.toString()) : 10);

                        Object colObj = dato.get("columnAsientos");
                        evento.setColumnAsientos(colObj != null ? Integer.valueOf(colObj.toString()) : 10);

                        // Guardamos (Update o Insert)
                        eventoRepository.save(evento);

                    } catch (Exception innerEx) {
                        LOG.error("⚠️ Error procesando un evento individual: " + innerEx.getMessage());
                    }
                }
                LOG.info("✅ Sincronización completada. Base de datos actualizada.");
            } else {
                LOG.warn("⚠️ El Proxy devolvió una lista vacía.");
            }
        } catch (Exception e) {
            LOG.error("❌ Falló la sincronización con Proxy: " + e.getMessage());
        }
    }

    // --- MÉTODOS REST ESTÁNDAR ---

    @PostMapping("")
    public ResponseEntity<EventoDTO> createEvento(@Valid @RequestBody EventoDTO eventoDTO) throws URISyntaxException {
        if (eventoDTO.getId() != null) throw new BadRequestAlertException("A new evento cannot already have an ID", ENTITY_NAME, "idexists");
        eventoDTO = eventoService.save(eventoDTO);
        return ResponseEntity.created(new URI("/api/eventos/" + eventoDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, eventoDTO.getId().toString()))
            .body(eventoDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> updateEvento(@PathVariable(value = "id") final Long id, @Valid @RequestBody EventoDTO eventoDTO) throws URISyntaxException {
        if (eventoDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, eventoDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!eventoRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventoDTO.getId().toString()))
            .body(eventoService.update(eventoDTO));
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EventoDTO> partialUpdateEvento(@PathVariable(value = "id") final Long id, @NotNull @RequestBody EventoDTO eventoDTO) throws URISyntaxException {
        if (eventoDTO.getId() == null) throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        if (!Objects.equals(id, eventoDTO.getId())) throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        if (!eventoRepository.existsById(id)) throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        Optional<EventoDTO> result = eventoService.partialUpdate(eventoDTO);
        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, eventoDTO.getId().toString()));
    }

    @GetMapping("")
    public ResponseEntity<List<EventoDTO>> getAllEventos(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Eventos");

        // Si la base está vacía al inicio, sincronizamos automáticamente
        if (eventoRepository.count() == 0) {
            LOG.info("📭 Base de datos vacía al inicio. Sincronizando...");
            sincronizarConCatedra();
        }

        Page<EventoDTO> page = eventoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> getEvento(@PathVariable("id") Long id) {
        Optional<EventoDTO> eventoDTO = eventoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(eventoDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable("id") Long id) {
        eventoService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
