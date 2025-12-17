package com.juanalejop.backend.service.dto.reserva;

import java.io.Serializable;
// import java.time.ZonedDateTime; <--- BORRAR O COMENTAR ESTO
import java.util.List;

public class SolicitudVentaDTO implements Serializable {

    private Long eventoId;
    private Double precioVenta;

    // --- CAMBIO AQUÍ: Usamos String para controlar el formato exacto ---
    private String fecha;
    // ------------------------------------------------------------------

    private List<AsientoPersonaDTO> asientos;

    // ... getters y setters de eventoId, precioVenta, asientos ...

    // --- GETTERS Y SETTERS DE FECHA ACTUALIZADOS ---
    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    // -----------------------------------------------

    // (El resto de la clase y AsientoPersonaDTO queda igual)
    // ...
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    public List<AsientoPersonaDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoPersonaDTO> asientos) { this.asientos = asientos; }

    public static class AsientoPersonaDTO implements Serializable {
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
