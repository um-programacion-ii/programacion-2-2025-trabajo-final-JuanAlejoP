package com.juanalejop.proxy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Esto evita errores si la cátedra agrega campos nuevos
public class AsientoDto {
    private int fila;
    private int columna;
    private String estado; // "Vendido" o "Bloqueado"
    private String expira; // Fecha (solo si está bloqueado)

    // Constructores
    public AsientoDto() {}

    public AsientoDto(int fila, int columna, String estado) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }

    // Getters y Setters
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