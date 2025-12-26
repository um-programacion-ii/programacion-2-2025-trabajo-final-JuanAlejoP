package com.juanalejop.proxy.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsientoDto {
    private int fila;

    @JsonProperty("columna")
    @JsonAlias({"col", "c", "Col"})
    private int columna;

    private String estado;
    private String expira;

    public AsientoDto() {}

    public AsientoDto(int fila, int columna, String estado) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getExpira() { return expira; }
    public void setExpira(String expira) { this.expira = expira; }

    @Override
    public String toString() {
        return "Asiento [Fila=" + fila + ", Col=" + columna + ", Estado=" + estado + "]";
    }
}