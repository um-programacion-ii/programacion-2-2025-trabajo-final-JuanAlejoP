package com.juanalejop.movil.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    // IMPORTANTE:
    // En el emulador de Android, "localhost" es "10.0.2.2".
    // Si probamos en iOS sería "localhost".
    const val BASE_URL = "http://10.0.2.2:8080/api"
}