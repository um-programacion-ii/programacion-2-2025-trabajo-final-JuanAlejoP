package com.juanalejop.movil.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AsientoSimple(
    val fila: Int,
    val columna: Int
)

@Serializable
data class SolicitudBloqueo(
    val eventoId: Long,
    val asientos: List<AsientoSimple>
)

@Serializable
data class AsientoPersona(
    val fila: Int,
    val columna: Int,
    val persona: String
)

@Serializable
data class SolicitudVenta(
    val eventoId: Long,
    val asientos: List<AsientoPersona>
)