package com.juanalejop.movil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

class EventoDetalleViewModel : ViewModel() {
    private val repository = EventosRepository()

    var evento by mutableStateOf<Evento?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun cargarDetalle(eventoId: Long) {
        viewModelScope.launch {
            isLoading = true
            repository.getEvento(eventoId)
                .onSuccess {
                    evento = it
                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                }
        }
    }
}