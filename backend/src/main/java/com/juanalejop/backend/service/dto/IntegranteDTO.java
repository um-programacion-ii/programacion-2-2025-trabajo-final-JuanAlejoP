package com.juanalejop.backend.service.dto;
import java.io.Serializable;

public class IntegranteDTO implements Serializable {
    private String nombre;
    private String apellido;
    private String identificacion;

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
}
