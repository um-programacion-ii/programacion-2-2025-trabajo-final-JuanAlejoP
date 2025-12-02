package com.juanalejop.movil

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.juanalejop.movil.ui.screens.EventoDetalleScreen
import com.juanalejop.movil.ui.screens.LoginScreen
import com.juanalejop.movil.ui.screens.EventosScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.juanalejop.movil.ui.screens.MapaAsientosScreen
import com.juanalejop.movil.ui.screens.CargaDatosScreen // Importar

enum class CurrentScreen {
    LOGIN,
    HOME,
    DETALLE,
    MAPA,
    CARGA_DATOS
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(CurrentScreen.LOGIN) }
        var selectedEventoId by remember { mutableStateOf<Long?>(null) } // Variable para guardar el ID
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
                            currentScreen = CurrentScreen.CARGA_DATOS // <-- Navegar
                        }
                    )
                }
            }
            CurrentScreen.CARGA_DATOS -> {
                CargaDatosScreen(
                    asientos = asientosSeleccionados,
                    onBack = { currentScreen = CurrentScreen.MAPA },
                    onConfirmarCompra = { datos ->
                        // Aquí termina el Milestone 5.
                        // En el Milestone 6 enviaremos 'datos' al Backend.
                        println("Listo para comprar: $datos")
                    }
                )
            }
        }
    }
}