package com.juanalejop.backend.service.dto.reserva;

import java.util.List;

public class SolicitudVentaDTO {
    private Long eventoId;
    private List<AsientoPersonaDTO> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoPersonaDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoPersonaDTO> asientos) { this.asientos = asientos; }

    public static class AsientoPersonaDTO {
        private int fila;
        private int columna;
        private String persona; // Nombre y Apellido

        public int getFila() { return fila; }
        public void setFila(int fila) { this.fila = fila; }
        public int getColumna() { return columna; }
        public void setColumna(int columna) { this.columna = columna; }
        public String getPersona() { return persona; }
        public void setPersona(String persona) { this.persona = persona; }
    }
}
