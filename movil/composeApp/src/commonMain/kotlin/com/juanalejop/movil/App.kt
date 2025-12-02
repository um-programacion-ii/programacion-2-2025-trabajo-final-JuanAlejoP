package com.juanalejop.movil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.juanalejop.movil.ui.screens.EventoDetalleScreen
import com.juanalejop.movil.ui.screens.LoginScreen
import com.juanalejop.movil.ui.screens.EventosScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.juanalejop.movil.ui.screens.MapaAsientosScreen

enum class CurrentScreen {
    LOGIN,
    HOME,
    DETALLE,
    MAPA
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(CurrentScreen.LOGIN) }
        var selectedEventoId by remember { mutableStateOf<Long?>(null) } // Variable para guardar el ID
        // Guardaremos los asientos elegidos para el próximo paso (Carga de Datos)
        var asientosSeleccionados by remember { mutableStateOf<List<com.juanalejop.movil.data.model.Asiento>>(emptyList()) }

        when (currentScreen) {
            CurrentScreen.LOGIN -> {
                LoginScreen(onLoginSuccess = { currentScreen = CurrentScreen.HOME })
            }
            CurrentScreen.HOME -> {
                EventosScreen(
                    onEventoClick = { id ->
                        selectedEventoId = id      // 1. Guardamos el ID
                        currentScreen = CurrentScreen.DETALLE // 2. Navegamos (Cumplimos la tarea)
                    }
                )
            }
            CurrentScreen.DETALLE -> {
                if (selectedEventoId != null) {
                    EventoDetalleScreen(
                        eventoId = selectedEventoId!!,
                        onBack = { currentScreen = CurrentScreen.HOME },
                        onComprarClick = {
                            currentScreen = CurrentScreen.MAPA // <-- Navegar al mapa
                        }
                    )
                }
            }
            CurrentScreen.MAPA -> {
                if (selectedEventoId != null) {
                    MapaAsientosScreen(
                        eventoId = selectedEventoId!!,
                        onBack = { currentScreen = CurrentScreen.DETALLE },
                        onContinuar = { seleccion ->
                            asientosSeleccionados = seleccion
                            println("Asientos elegidos: $seleccion")
                            // Aquí iremos a la pantalla de "Cargar Personas" (Issue 5.5)
                        }
                    )
                }
            }
        }
    }
}