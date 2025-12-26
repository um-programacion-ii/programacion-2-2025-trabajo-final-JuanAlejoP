package com.juanalejop.movil.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juanalejop.movil.data.model.Asiento
import com.juanalejop.movil.data.model.Evento
import com.juanalejop.movil.data.network.EventosRepository
import kotlinx.coroutines.launch

class MapaAsientosViewModel : ViewModel() {
    private val repository = EventosRepository()

    var eventoState by mutableStateOf<Evento?>(null)
        private set

    var selectedAsientos by mutableStateOf<Set<Asiento>>(emptySet())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun cargarMapa(eventoId: Long) {
        selectedAsientos = emptySet()

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.getEvento(eventoId)
                .onSuccess { eventoDescargado ->
                    eventoState = eventoDescargado
                    isLoading = false
                }
                .onFailure { e ->
                    isLoading = false
                    errorMessage = "Error cargando asientos: ${e.message ?: "Error de red"}"
                }
        }
    }

    fun toggleAsiento(asiento: Asiento) {
        if (asiento.estado != "Libre") return

        val isSelected = selectedAsientos.any { it.fila == asiento.fila && it.columna == asiento.columna }

        if (isSelected) {
            selectedAsientos = selectedAsientos.filterNot {
                it.fila == asiento.fila && it.columna == asiento.columna
            }.toSet()
        } else {
            if (selectedAsientos.size < 4) {
                selectedAsientos = selectedAsientos + asiento
            }
        }
    }
}