package com.juanalejop.proxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventoAsientosDto {
    private Long eventoId;
    private List<AsientoDto> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public List<AsientoDto> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoDto> asientos) { this.asientos = asientos; }

    @Override
    public String toString() {
        return "Evento " + eventoId + " (" + (asientos != null ? asientos.size() : 0) + " asientos ocupados)";
    }
}