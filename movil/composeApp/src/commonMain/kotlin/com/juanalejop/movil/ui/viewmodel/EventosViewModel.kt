package com.juanalejop.movil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.AuthRepository
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

class EventosViewModel : ViewModel() {
    private val repository = EventosRepository()
    private val authRepository = AuthRepository()

    var eventos by mutableStateOf<List<Evento>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadEventos()
    }

    fun loadEventos() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.getEventos()
                .onSuccess { lista ->
                    eventos = lista
                    isLoading = false
                }
                .onFailure { error ->
                    isLoading = false
                    if (error.message?.contains("401") == true) {
                        errorMessage = "Sesión expirada"
                    } else {
                        errorMessage = "Error: ${error.message}"
                    }
                }
        }
    }

    fun logout(onLogout: () -> Unit) {
        authRepository.logout()
        onLogout()
    }
}