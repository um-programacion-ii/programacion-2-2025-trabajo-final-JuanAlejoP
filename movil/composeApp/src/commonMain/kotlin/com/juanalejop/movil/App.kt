package com.juanalejop.movil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.juanalejop.movil.ui.screens.EventoDetalleScreen
import com.juanalejop.movil.ui.screens.LoginScreen
import com.juanalejop.movil.ui.screens.EventosScreen // Importante importar esto
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class CurrentScreen {
    LOGIN,
    HOME,
    DETALLE
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(CurrentScreen.LOGIN) }
        var selectedEventoId by remember { mutableStateOf<Long?>(null) } // Variable para guardar el ID

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
                        onComprarClick = { /* Próximo issue */ }
                    )
                }
            }
        }
    }
}