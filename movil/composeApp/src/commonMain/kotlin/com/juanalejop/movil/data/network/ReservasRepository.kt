package com.juanalejop.movil.data.network

import com.juanalejop.movil.data.model.SolicitudBloqueo
import com.juanalejop.movil.data.model.SolicitudVenta
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class ReservasRepository {
    private val client = KtorClient.client
    private val baseUrl = KtorClient.BASE_URL

    suspend fun bloquearAsientos(solicitud: SolicitudBloqueo): Result<String> {
        return hacerPost("$baseUrl/reservas/bloquear", solicitud)
    }

    suspend fun realizarVenta(solicitud: SolicitudVenta): Result<String> {
        return hacerPost("$baseUrl/reservas/vender", solicitud)
    }

    private suspend inline fun <reified T> hacerPost(url: String, body: T): Result<String> {
        return try {
            val token = TokenManager.jwt ?: throw Exception("No hay sesión activa")
            
            // Si el servidor devuelve 200 OK, Ktor nos da el cuerpo. Si es error, tira excepción.
            val response: String = client.post(url) {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

            Result.success(response)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
