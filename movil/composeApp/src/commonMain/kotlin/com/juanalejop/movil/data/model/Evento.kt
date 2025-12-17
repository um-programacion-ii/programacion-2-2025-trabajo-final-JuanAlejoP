package com.juanalejop.movil.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Evento(
    val id: Long,
    val titulo: String,
    val resumen: String? = null,
    val descripcion: String? = null,
    val fechaHora: String,
    val precio: Double,
    val imagenUrl: String? = null, // Ya viene lleno!
    val direccion: String? = null, // NUEVO

    val filaAsientos: Int? = 10,
    val columnAsientos: Int? = 10,

    val asientos: List<Asiento>? = null,

    // NUEVOS OBJETOS COMPLEJOS
    val eventoTipo: EventoTipo? = null,
    val integrantes: List<Integrante>? = null
)

@Serializable
data class EventoTipo(
    val nombre: String,
    val descripcion: String? = null
)

@Serializable
data class Integrante(
    val nombre: String,
    val apellido: String,
    val identificacion: String? = null
)