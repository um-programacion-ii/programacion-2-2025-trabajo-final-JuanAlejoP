package com.juanalejop.backend.service.dto.reserva;

import java.util.List;

public class SolicitudBloqueoDTO {
    private Long eventoId;
    private List<AsientoSimpleDTO> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public List<AsientoSimpleDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoSimpleDTO> asientos) { this.asientos = asientos; }

    public static class AsientoSimpleDTO {
        private int fila;
        private int columna;

        public int getFila() { return fila; }
        public void setFila(int fila) { this.fila = fila; }
        public int getColumna() { return columna; }
        public void setColumna(int columna) { this.columna = columna; }
    }
}
