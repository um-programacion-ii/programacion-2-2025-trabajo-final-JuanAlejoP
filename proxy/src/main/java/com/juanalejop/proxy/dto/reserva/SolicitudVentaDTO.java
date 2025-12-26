package com.juanalejop.proxy.dto.reserva;

import java.util.List;

public class SolicitudVentaDTO {
    private Long eventoId;

    private Double precioVenta;
    private String fecha;

    private List<AsientoPersonaDTO> asientos;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public List<AsientoPersonaDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoPersonaDTO> asientos) { this.asientos = asientos; }

    public static class AsientoPersonaDTO {
        private int fila;
        private int columna;
        private String persona;

        public int getFila() { return fila; }
        public void setFila(int fila) { this.fila = fila; }
        public int getColumna() { return columna; }
        public void setColumna(int columna) { this.columna = columna; }
        public String getPersona() { return persona; }
        public void setPersona(String persona) { this.persona = persona; }
    }
}