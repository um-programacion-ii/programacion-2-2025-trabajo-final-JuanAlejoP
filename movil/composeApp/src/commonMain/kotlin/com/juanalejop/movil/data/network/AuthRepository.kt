package com.juanalejop.movil.data.network

import com.juanalejop.movil.data.model.LoginRequest
import com.juanalejop.movil.data.model.LoginResponse
import com.juanalejop.movil.data.model.RegisterRequest
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

object TokenManager {
    var jwt: String? = null
        private set

    fun saveToken(token: String) {
        jwt = token
    }

    fun clearToken() {
        jwt = null
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

    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = client.post("$baseUrl/register") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.Created) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error registro: ${response.status}"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun logout() {
        TokenManager.clearToken()
    }
}