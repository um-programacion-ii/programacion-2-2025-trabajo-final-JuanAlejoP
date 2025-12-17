package com.juanalejop.backend.service.dto.proxy;

import com.fasterxml.jackson.annotation.JsonAlias;  // <--- IMPORTAR
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // <--- IMPORTAR

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsientoProxyDto {
    private int fila;

    // --- CAMBIO CLAVE AQUÍ TAMBIÉN ---
    @JsonProperty("columna")
    @JsonAlias({"col", "c", "Col"})
    private int columna;
    // ---------------------------------

    private String estado;

    // Getters y Setters
    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
