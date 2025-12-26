package com.juanalejop.movil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanalejop.movil.data.model.RegisterRequest
import com.juanalejop.movil.data.network.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onRegisterClick(onSuccess: () -> Unit) {
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            errorMessage = "Completa los campos obligatorios (*)"
            return
        }
        if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }
        if (password.length < 4) {
            errorMessage = "La contraseña debe tener al menos 4 caracteres"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val request = RegisterRequest(
                login = username,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )

            authRepository.register(request)
                .onSuccess {
                    isLoading = false
                    onSuccess()
                }
                .onFailure {
                    isLoading = false
                    errorMessage = "Error al registrar. Revisa si el usuario o email ya existen."
                }
        }
    }

    fun resetState() {
        firstName = ""
        lastName = ""
        username = ""
        email = ""
        password = ""
        confirmPassword = ""
        errorMessage = null
        isLoading = false
    }
}