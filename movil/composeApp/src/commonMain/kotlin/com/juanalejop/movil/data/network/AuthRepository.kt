package com.juanalejop.movil.data.network

import com.juanalejop.movil.data.model.LoginRequest
import com.juanalejop.movil.data.model.LoginResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object TokenManager {
    var jwt: String? = null
        private set

    fun saveToken(token: String) {
        jwt = token
    }
}

class AuthRepository {
    private val client = KtorClient.client
    private val baseUrl = KtorClient.BASE_URL

    suspend fun login(user: String, pass: String): Result<String> {
        return try {
            val response: LoginResponse = client.post("$baseUrl/authenticate") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username = user, password = pass))
            }.body()

            TokenManager.saveToken(response.idToken)
            Result.success(response.idToken)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}