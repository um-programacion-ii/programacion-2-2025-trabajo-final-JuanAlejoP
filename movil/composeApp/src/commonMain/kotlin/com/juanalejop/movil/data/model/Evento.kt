package com.juanalejop.movil.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Evento(
    val id: Long,
    val titulo: String,
    val resumen: String? = null,
    val descripcion: String? = null,
    val fechaHora: String, // JHipster manda la fecha como texto (ISO-8601)
    val precio: Double,
    val imagenUrl: String? = null,

    val asientos: List<Asiento>? = null // La lista de asientos puede venir vacía o nula
)