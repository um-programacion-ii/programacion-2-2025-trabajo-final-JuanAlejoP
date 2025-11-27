package com.juanalejop.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.juanalejop.backend.domain.Evento} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoDTO implements Serializable {

    private Long id;

    @NotNull
    private Long idCatedra;

    @NotNull
    private String titulo;

    private String resumen;

    private String descripcion;

    @NotNull
    private ZonedDateTime fechaHora;

    private String direccion;

    private String imagenUrl;

    @NotNull
    private Double precio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCatedra() {
        return idCatedra;
    }

    public void setIdCatedra(Long idCatedra) {
        this.idCatedra = idCatedra;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ZonedDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(ZonedDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    // Campo transitorio (no se guarda en BD local, viene del Proxy)
    private java.util.List<com.juanalejop.backend.service.dto.proxy.AsientoProxyDto> asientos;

    public java.util.List<com.juanalejop.backend.service.dto.proxy.AsientoProxyDto> getAsientos() {
        return asientos;
    }

    public void setAsientos(java.util.List<com.juanalejop.backend.service.dto.proxy.AsientoProxyDto> asientos) {
        this.asientos = asientos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoDTO)) {
            return false;
        }

        EventoDTO eventoDTO = (EventoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoDTO{" +
            "id=" + getId() +
            ", idCatedra=" + getIdCatedra() +
            ", titulo='" + getTitulo() + "'" +
            ", resumen='" + getResumen() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", fechaHora='" + getFechaHora() + "'" +
            ", direccion='" + getDireccion() + "'" +
            ", imagenUrl='" + getImagenUrl() + "'" +
            ", precio=" + getPrecio() +
            "}";
    }
}
