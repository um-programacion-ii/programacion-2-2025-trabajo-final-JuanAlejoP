package com.juanalejop.movil.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false
)

@Serializable
data class LoginResponse(
    @SerialName("id_token") val idToken: String
)

@Serializable
data class RegisterRequest(
    val login: String,
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val langKey: String = "es"
)