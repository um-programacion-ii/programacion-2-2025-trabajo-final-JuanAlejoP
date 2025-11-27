package com.juanalejop.backend.service.dto.proxy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventoAsientosProxyDto {
    private Long eventoId;
    private List<AsientoProxyDto> asientos;

    // Getters y Setters
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoProxyDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoProxyDto> asientos) { this.asientos = asientos; }
}
