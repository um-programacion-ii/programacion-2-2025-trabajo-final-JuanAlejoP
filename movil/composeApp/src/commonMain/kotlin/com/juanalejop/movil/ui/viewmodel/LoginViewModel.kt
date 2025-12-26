package com.juanalejop.movil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanalejop.movil.data.network.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var passwordVisible by mutableStateOf(false)

    fun onLoginClick(onSuccess: () -> Unit) {
        if (username.isBlank() || password.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            authRepository.login(username, password)
                .onSuccess {
                    isLoading = false
                    onSuccess()
                }
                .onFailure {
                    isLoading = false
                    errorMessage = "Credenciales inválidas"
                }
        }
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun resetState() {
        username = ""
        password = ""
        errorMessage = null
        isLoading = false
        passwordVisible = false
    }
}