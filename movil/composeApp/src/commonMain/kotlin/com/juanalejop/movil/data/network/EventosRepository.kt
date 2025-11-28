package com.juanalejop.movil.data.network

import com.juanalejop.movil.data.model.Evento
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

class EventosRepository {
    private val client = KtorClient.client
    private val baseUrl = KtorClient.BASE_URL

    suspend fun getEventos(): Result<List<Evento>> {
        return try {
            val token = TokenManager.jwt ?: throw Exception("No hay sesión activa")
            val eventos: List<Evento> = client.get("$baseUrl/eventos") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
            Result.success(eventos)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getEvento(id: Long): Result<Evento> {
        return try {
            val token = TokenManager.jwt ?: throw Exception("No hay sesión activa")

            // Llamamos a /api/eventos/{id}
            val evento: Evento = client.get("$baseUrl/eventos/$id") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()

            Result.success(evento)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}