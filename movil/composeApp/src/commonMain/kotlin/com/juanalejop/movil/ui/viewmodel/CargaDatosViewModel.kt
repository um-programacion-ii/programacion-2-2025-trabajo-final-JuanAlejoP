package com.juanalejop.movil.ui.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.juanalejop.movil.data.model.Asiento

class CargaDatosViewModel : ViewModel() {
    val nombresState = mutableStateMapOf<Int, String>()

    fun updateNombre(index: Int, nombre: String) {
        nombresState[index] = nombre
    }

    fun isFormValid(totalAsientos: Int): Boolean {
        return (0 until totalAsientos).all { i ->
            !nombresState[i].isNullOrBlank()
        }
    }

    fun prepararDatosCompra(asientos: List<Asiento>): Map<Asiento, String> {
        return asientos.mapIndexed { index, asiento ->
            asiento to (nombresState[index] ?: "")
        }.toMap()
    }

    fun resetForm() {
        nombresState.clear()
    }
}